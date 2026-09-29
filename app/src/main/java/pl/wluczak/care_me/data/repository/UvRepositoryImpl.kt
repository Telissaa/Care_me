package pl.wluczak.care_me.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pl.wluczak.care_me.core.util.Resource
import pl.wluczak.care_me.data.mapper.toDomain
import pl.wluczak.care_me.data.remote.api.UVApi
import pl.wluczak.care_me.domain.model.UvIndex
import pl.wluczak.care_me.domain.repository.UvRepository
import retrofit2.HttpException
import java.io.IOException

/**
 * Implementation of UvRepository retrieving data from UVApi with Resource handling.
 */
class UvRepositoryImpl(
    private val api: UVApi,
) : UvRepository {

    override fun getUvIndex(lat: Double, lng: Double): Flow<Resource<UvIndex>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUvIndex(lat, lng)
            val uvIndex = response.toDomain()
            emit(Resource.Success(uvIndex))
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                403 -> "Invalid API key or daily request limit exceeded."
                404 -> "Location data for UV index not found."
                else -> e.localizedMessage ?: "An unexpected HTTP error occurred"
            }
            emit(Resource.Error(errorMessage))
        } catch (_: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
