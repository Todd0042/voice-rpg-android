package com.voicerpg.engine.model

/**
 * Region/accent option for voice synthesis and speech recognition.
 */
data class RegionOption(
    val code: String,       // ISO 2-letter uppercase country code (e.g. "US", "GB", "MX", "ES"), or "" for Any/Default
    val displayName: String // Localized display name
)

/**
 * Supported language declaration with available regional variants.
 */
data class LanguageOption(
    val code: String,        // ISO 2-letter lowercase language code (e.g. "en", "es", "de", "fr", "pt", "it")
    val displayName: String,  // Name in English
    val nativeName: String,   // Name in native language
    val regions: List<RegionOption>
)

/**
 * Catalog of supported languages and regional accent preferences.
 * Content-neutral: provides generic localization metadata for voice synthesis,
 * speech recognition, and command recognition across all supported languages.
 */
object LanguageCatalog {
    val SUPPORTED_LANGUAGES: List<LanguageOption> = listOf(
        LanguageOption(
            code = "en",
            displayName = "English",
            nativeName = "English",
            regions = listOf(
                RegionOption("", "Any / Default"),
                RegionOption("US", "United States"),
                RegionOption("GB", "United Kingdom"),
                RegionOption("CA", "Canada"),
                RegionOption("AU", "Australia"),
                RegionOption("IE", "Ireland"),
                RegionOption("IN", "India"),
                RegionOption("ZA", "South Africa"),
                RegionOption("NG", "Nigeria"),
                RegionOption("NZ", "New Zealand")
            )
        ),
        LanguageOption(
            code = "es",
            displayName = "Spanish",
            nativeName = "Español",
            regions = listOf(
                RegionOption("", "Cualquiera / Auto"),
                RegionOption("ES", "España"),
                RegionOption("MX", "México"),
                RegionOption("US", "Estados Unidos"),
                RegionOption("AR", "Argentina"),
                RegionOption("CO", "Colombia"),
                RegionOption("CL", "Chile"),
                RegionOption("PE", "Perú")
            )
        ),
        LanguageOption(
            code = "de",
            displayName = "German",
            nativeName = "Deutsch",
            regions = listOf(
                RegionOption("", "Alle / Standard"),
                RegionOption("DE", "Deutschland"),
                RegionOption("AT", "Österreich"),
                RegionOption("CH", "Schweiz")
            )
        ),
        LanguageOption(
            code = "fr",
            displayName = "French",
            nativeName = "Français",
            regions = listOf(
                RegionOption("", "Tous / Défaut"),
                RegionOption("FR", "France"),
                RegionOption("CA", "Canada"),
                RegionOption("BE", "Belgique"),
                RegionOption("CH", "Suisse")
            )
        ),
        LanguageOption(
            code = "pt",
            displayName = "Portuguese",
            nativeName = "Português",
            regions = listOf(
                RegionOption("", "Qualquer / Padrão"),
                RegionOption("BR", "Brasil"),
                RegionOption("PT", "Portugal")
            )
        ),
        LanguageOption(
            code = "it",
            displayName = "Italian",
            nativeName = "Italiano",
            regions = listOf(
                RegionOption("", "Tutti / Predefinito"),
                RegionOption("IT", "Italia"),
                RegionOption("CH", "Svizzera")
            )
        )
    )

    fun getLanguage(code: String): LanguageOption =
        SUPPORTED_LANGUAGES.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: SUPPORTED_LANGUAGES.first()

    fun getRegionsForLanguage(langCode: String): List<RegionOption> =
        getLanguage(langCode).regions

    fun isValidLanguage(code: String): Boolean =
        SUPPORTED_LANGUAGES.any { it.code.equals(code, ignoreCase = true) }
}
