package pl.wluczak.care_me.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO representing response from OpenUV API.
 */
@Serializable
data class UvResponseDto(
    @SerialName("result")
    val result: UvResultDto? = null,
)

@Serializable
data class UvResultDto(
    @SerialName("uv")
    val uv: Double? = null,
    @SerialName("safe_exposure_time")
    val safeExposureTime: SafeExposureTimeDto? = null
)

@Serializable
data class SafeExposureTimeDto(
    @SerialName("st1") val st1: Int? = null,
    @SerialName("st2") val st2: Int? = null,
    @SerialName("st3") val st3: Int? = null,
    @SerialName("st4") val st4: Int? = null,
    @SerialName("st5") val st5: Int? = null,
    @SerialName("st6") val st6: Int? = null
)
