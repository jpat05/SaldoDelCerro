# Informe FinGO — App de control de ingresos y egresos

## Resumen

**FinGO** es una app Android de finanzas personales que permite registrar **ingresos y egresos por categoría** y ver en todo momento el **saldo disponible**. No existe un "capital base": el saldo arranca en 0 y siempre es `ingresos − egresos`. Los datos se guardan localmente (base de datos Room sobre SQLite), por lo que no requiere internet.

La interfaz está construida con **Jetpack Compose + Material 3** y tiene 4 pantallas organizadas con una barra de navegación inferior.

## Tecnología y arquitectura

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.4 |
| UI | Jetpack Compose + Material 3 |
| Persistencia | Room 2.8 (SQLite) + KSP |
| Build | Gradle + AGP 9 |
| Min / Target SDK | 29 / 37 |

Flujo de datos (capas: UI → servicios → DAO → SQLite):

```
Pantalla (Composable) ─► Services ─► DAO (Room) ─► SQLite
        ▲   (Transaction / Category / Ledger)       │
        └──── Flows reactivos (la UI se actualiza sola) ◄┘
```

`MainActivity` (`app/src/main/java/com/movil/saldodelcerro/MainActivity.kt`) crea la base `billetera_db`, arma los tres servicios y los inyecta en `FinGoApp`. Al iniciar, `CategorySeeder` siembra las categorías por defecto (una sola vez).

> Los **ViewModels** todavía no existen: las pantallas usan los servicios directamente. Están previstos para una segunda fase.

## Las 4 pantallas (`FinGoApp.kt`)

1. **Inicio** (`HomeScreen.kt`) — tarjeta con el **saldo disponible**, filas de **Ingresos** y **Egresos** totales, y los **5 últimos movimientos**.
2. **Ingresos** (`IncomeScreen.kt`) — formulario para registrar un ingreso: **monto**, **categoría** y **descripción**.
3. **Egresos** (`ExpenseScreen.kt`) — formulario para registrar un egreso: **monto**, **categoría** y **descripción**.
4. **Resumen** (`SummaryScreen.kt`) — estado financiero (Saldo/Ingresos/Egresos), **gastos por categoría** con barras de progreso y el **historial completo** de movimientos.

## Modelo de datos

- `TransactionEntity` — un movimiento: descripción, monto, `typeId`, fecha. El monto es `BigDecimal` (se persiste como `TEXT`).
- `CategoryEntity` — categoría **dinámica** (tabla `transaction_types`); `isIncome` distingue ingreso/egreso y `active` implementa el borrado lógico.
- `UserEntity` / `UserRole` — usuarios (reservado para login futuro, aún sin uso en pantallas).
- `CategoryTotal` — total egresado por categoría (objeto calculado en el servicio, no una query).
- `LedgerResult` — resultado de una operación del libro: aceptada o rechazada con un motivo.

Las categorías por defecto **no** se siembran desde `AppDatabase`. Las crea `data/seed/CategorySeeder` (10 categorías: 7 de egreso — *Alimentación, Transporte, Vivienda, Salud, Entretenimiento, Educación, Otros gastos* — y 3 de ingreso — *Salario, Freelance, Otros ingresos*). El sembrado es idempotente.

## Lógica clave (`services/`)

- **`TransactionService`** — único punto de escritura de movimientos (una misma ruta para ingresos y egresos).
- **`CategoryService`** — categorías dinámicas; al crear una, el nombre se compara ignorando mayúsculas y acentos (evita duplicados y reactiva una archivada); `archive` / `restore` para el borrado lógico.
- **`LedgerService`** — todo el cálculo, en Kotlin con `BigDecimal` (nunca en SQL, porque el monto es `TEXT` y un `SUM` lo degradaría a `REAL`):
  - **Saldo disponible** = `ingresos − egresos`.
  - **Ingresos / Egresos** = suma de movimientos cuya categoría es de ingreso / egreso.
  - **Gastos por categoría** = egresos agrupados por categoría.
  - Solo cuentan los movimientos de **categorías activas**.
  - **Invariante**: `registerMovement` **rechaza** un egreso mayor que el saldo, y `archiveCategory` **rechaza** archivar una categoría de ingreso que dejaría el saldo en negativo. El saldo nunca queda por debajo de 0.
- **Borrado lógico**: archivar una categoría la oculta de los selectores y la saca de los totales, pero conserva el historial de sus movimientos.
- Al ser `Flow`s de Room, insertar o borrar un movimiento **actualiza las pantallas automáticamente**, sin recargar.

## Formato de dinero

Todos los montos se muestran con `NumberFormat` para Ecuador (`Locale("es-EC")`): `$1.234,56` (coma decimal). El campo de monto acepta la coma como separador decimal. Los movimientos se listan con `+` (verde) o `-` (rojo) según sean ingreso o egreso.

## Cómo compilar y ejecutar

```bash
# Desde la raíz del proyecto:
./gradlew :app:assembleDebug       # Linux / macOS
gradlew.bat :app:assembleDebug     # Windows
# El APK queda en:
#   app/build/outputs/apk/debug/app-debug.apk
```

Desde **Android Studio** o **IntelliJ IDEA**: **Sync Project with Gradle Files** y luego el botón ▶ **Run** (con el celular conectado y la depuración USB activada).
