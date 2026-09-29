package pl.wluczak.care_me

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.wluczak.care_me.core.util.Resource
import pl.wluczak.care_me.data.remote.api.UVApi
import pl.wluczak.care_me.data.remote.dto.SafeExposureTimeDto
import pl.wluczak.care_me.data.remote.dto.UvResponseDto
import pl.wluczak.care_me.data.remote.dto.UvResultDto
import pl.wluczak.care_me.data.repository.UvRepositoryImpl
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UvRepositoryImplTest {

    @Test
    fun getUvIndex_success_emitsLoadingAndSuccess() = runBlocking {
        val fakeApi = object : UVApi {
            override suspend fun getUvIndex(lat: Double, lng: Double): UvResponseDto {
                return UvResponseDto(
                    result = UvResultDto(
                        uv = 4.2,
                        safeExposureTime = SafeExposureTimeDto(st1 = 45),
                    )
                )
            }
        }
        val repository = UvRepositoryImpl(fakeApi)

        val results = repository.getUvIndex(52.2297, 21.0122).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(4.2, (results[1] as Resource.Success).data?.uvValue ?: 0.0, 0.01)
        assertEquals("Moderate", (results[1] as Resource.Success).data?.riskLevel)
    }

    @Test
    fun getUvIndex_httpException_emitsLoadingAndError() = runBlocking {
        val fakeApi = object : UVApi {
            override suspend fun getUvIndex(lat: Double, lng: Double): UvResponseDto {
                throw HttpException(Response.error<UvResponseDto>(403, "".toResponseBody(null)))
            }
        }
        val repository = UvRepositoryImpl(fakeApi)

        val results = repository.getUvIndex(52.2297, 21.0122).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
    }

    @Test
    fun getUvIndex_ioException_emitsLoadingAndConnectionError() = runBlocking {
        val fakeApi = object : UVApi {
            override suspend fun getUvIndex(lat: Double, lng: Double): UvResponseDto {
                throw IOException("Network timeout")
            }
        }
        val repository = UvRepositoryImpl(fakeApi)

        val results = repository.getUvIndex(52.2297, 21.0122).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("Couldn't reach server. Check your internet connection.", (results[1] as Resource.Error).message)
    }
}
