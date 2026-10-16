# Informe FinGO — App de control de capital y egresos

## Resumen

**FinGO** es una app Android de finanzas personales que permite registrar un capital base, anotar egresos por categoría y ver en todo momento el saldo disponible. Los datos se guardan de forma local en el dispositivo (base de datos Room, SQLite), por lo que no requiere internet.

La interfaz está construida con **Jetpack Compose + Material 3** y tiene 4 pantallas organizadas con una barra de navegación inferior.

## Tecnología y arquitectura

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.4 |
| UI | Jetpack Compose + Material 3 |
| Persistencia | Room 2.8 (SQLite) + KSP |
| Build | Gradle + AGP 9 |
| Min / Target SDK | 29 / 37 |

Flujo de datos (MVVM simple con Flows de Kotlin):

```
Pantalla (Composable) ─► WalletService ─► DAO (Room) ─► SQLite
        ▲                  (lógica)          │
        └──── Flows reactivos (se actualiza la UI sola) ◄┘
```

`MainActivity` (app/src/main/java/com/movil/saldodelcerro/MainActivity.kt) crea la base de datos `billetera_db`, arma el `WalletService` con los 3 DAOs e inyecta la app `FinGoApp`.

## Las 4 pantallas (`FinGoApp.kt`)

La barra inferior cambia entre 4 pestañas, cada una con su ícono:

1. **Inicio** (`HomeScreen.kt`) — Tarjeta con el **saldo disponible**, filas de **Ingresos** y **Egresos** totales, y los **5 últimos movimientos**.
2. **Capital** (`CapitalBaseScreen.kt`) — Muestra el **capital base actual** y un campo numérico (`$ `) para guardarlo o actualizarlo.
3. **Egresos** (`ExpenseScreen.kt`) — Formulario para registrar un egreso: **monto**, **categoría** (desplegable) y **descripción**.
4. **Resumen** (`SummaryScreen.kt`) — Estado financiero (Saldo/Ingresos/Egresos), **gastos por categoría** con barras de progreso y el **historial completo** de movimientos.

## Modelo de datos

- `TransactionEntity` — un movimiento: descripción, monto, `typeId`, fecha.
- `TransactionTypeEntity` — tipo/categoría; flag `isIncome`: `true` = ingreso, `false` = egreso.
- `UserEntity` / `UserRole` — usuarios (reservado para login futuro, aún sin uso en pantallas).
- `CategoryTotal` — total gastado por categoría (resultado de la query de agrupación).

Al crear la base por primera vez, `AppDatabase` **siembra 6 categorías** por defecto: *Comida, Transporte, Ocio, Vivienda, Salud, Otros*.

## Lógica clave (`WalletService.kt` + `TransactionsDao.kt`)

- **Saldo disponible** = `SUM(CASE WHEN es ingreso THEN monto ELSE -monto END)` sobre todas las transacciones.
- **Ingresos** = suma de movimientos con tipo `isIncome = 1`.
- **Egresos** = suma con `isIncome = 0`.
- **Gastos por categoría** = `SUM` agrupado por nombre de tipo, solo egresos.
- **Capital base**: se modela como una transacción "especial" del tipo `Capital base` (`isIncome = true`). Guardar un nuevo capital **borra el anterior** y escribe el nuevo, así el saldo siempre parte de ese monto.
- `WalletService.availableBalance` etc. son `Flow`s de Room: al insertar o borrar un movimiento, las pantallas que los observan se **actualizan automáticamente** (sin recargar).

## Formato de dinero

Todos los montos se muestran con `DecimalFormat` en dólares: `$ 1,234.56` (`utils/Formatters.kt`, símbolo en la constante `CURRENCY_SYMBOL`). Los movimientos se listan con `+` (verde) o `-` (rojo) según sean ingreso o egreso.

## Cómo compilar y ejecutar

```bash
# Desde la raíz del proyecto:
.\gradlew.bat :app:assembleDebug
# El APK queda en:
#   %USERPROFILE%\.gradle-builds\SaldoDelCerro\app\outputs\apk\debug\app-debug.apk
```

Desde **Android Studio**: conecta el celular con depuración USB → **File → Sync Project with Gradle Files** → botón ▶ **Run**.

> Nota: el proyecto vive dentro de OneDrive; los outputs de build se redirigen a `%USERPROFILE%\.gradle-builds\` para evitar bloqueos de archivos de OneDrive durante la compilación.