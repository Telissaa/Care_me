package pl.wluczak.care_me.data.mapper

import pl.wluczak.care_me.data.remote.dto.UvResponseDto
import pl.wluczak.care_me.domain.model.UvIndex

/**
 * Mapper for converting UvResponseDto to UvIndex domain model.
 */
fun UvResponseDto.toDomain(): UvIndex {
    val uvVal = result?.uv ?: 0.0
    val safeTime = result?.safeExposureTime?.st1
        ?: result?.safeExposureTime?.st2
        ?: result?.safeExposureTime?.st3

    val riskLevel = when {
        uvVal < 3.0 -> "Low"
        uvVal < 6.0 -> "Moderate"
        uvVal < 8.0 -> "High"
        uvVal < 11.0 -> "Very High"
        else -> "Extreme"
    }

    return UvIndex(
        uvValue = uvVal,
        safeSunTimeMinutes = safeTime,
        riskLevel = riskLevel
    )
}
