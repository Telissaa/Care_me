package pl.wluczak.care_me.data.remote.api

import pl.wluczak.care_me.data.remote.dto.UvResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UVApi {
    @GET("uv")
    suspend fun getUvIndex(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): UvResponseDto
}
