package pl.wluczak.care_me

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.wluczak.care_me.data.mapper.toDomain
import pl.wluczak.care_me.data.remote.dto.SafeExposureTimeDto
import pl.wluczak.care_me.data.remote.dto.UvResponseDto
import pl.wluczak.care_me.data.remote.dto.UvResultDto

class UvMapperTest {

    @Test
    fun uvResponseDto_toDomain_mapsCorrectly() {
        val dto = UvResponseDto(
            result = UvResultDto(
                uv = 5.5,
                safeExposureTime = SafeExposureTimeDto(st1 = 30)
            )
        )

        val domain = dto.toDomain()

        assertEquals(5.5, domain.uvValue, 0.01)
        assertEquals(30, domain.safeSunTimeMinutes)
        assertEquals("Moderate", domain.riskLevel)
    }

    @Test
    fun uvResponseDto_toDomain_handlesNullsGracefully() {
        val dto = UvResponseDto(result = null)

        val domain = dto.toDomain()

        assertEquals(0.0, domain.uvValue, 0.01)
        assertEquals(null, domain.safeSunTimeMinutes)
        assertEquals("Low", domain.riskLevel)
    }
}
