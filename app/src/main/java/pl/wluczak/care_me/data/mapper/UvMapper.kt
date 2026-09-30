package pl.wluczak.care_me.data.mapper

import pl.wluczak.care_me.data.remote.dto.UvResponseDto
import pl.wluczak.care_me.domain.model.UvIndex

/**
 * Mapper for converting UvResponseDto to UvIndex domain model.
 */
fun UvResponseDto.toDomain(): UvIndex {
    val rawUv = requireNotNull(result?.uv) { "UV response is missing the UV value." }
    require(rawUv.isFinite()) { "UV response contains an invalid UV value." }
    val uvVal = rawUv.coerceAtLeast(0.0)

    val rawSafeTime = result?.safeExposureTime?.let { safe ->
        safe.st1 ?: safe.st2 ?: safe.st3 ?: safe.st4 ?: safe.st5 ?: safe.st6
    }
    val safeTime = rawSafeTime?.takeIf { it >= 0 }

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
        riskLevel = riskLevel,
    )
}
