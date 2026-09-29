package pl.wluczak.care_me.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UvResponseDto(
    @SerialName("result")
    val result: UvResultDto? = null
)

@Serializable
data class UvResultDto(
    @SerialName("uv")
    val uv: Double? = null
)
