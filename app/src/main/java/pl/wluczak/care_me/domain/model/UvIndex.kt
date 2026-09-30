package pl.wluczak.care_me.domain.model

/**
 * Represents UV index data and recommended sun exposure safety.
 */
data class UvIndex(
    val uvValue: Double,
    val safeSunTimeMinutes: Int?,
    val riskLevel: String
)
