package com.movil.saldodelcerro.utils

import java.text.Normalizer
import java.util.Locale

/**
 * Case- and accent-insensitive identity key for user-entered names.
 *
 * "Alimentación", "alimentacion" and "ALIMENTACION" all collapse to "alimentacion", so dynamic
 * category names can be matched without hardcoding every accented variant.
 * [Locale.ROOT] is deliberate: locale-sensitive lowercasing (e.g. the Turkish dotless i) must not
 * change how names compare.
 */
fun String.normalizedKey(): String =
    Normalizer.normalize(trim(), Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .lowercase(Locale.ROOT)
