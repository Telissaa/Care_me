package pl.wluczak.care_me.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.core.util.Resource
import pl.wluczak.care_me.domain.model.UvIndex

/**
 * Repository interface for retrieving UV index data.
 */
interface UvRepository {
    fun getUvIndex(lat: Double, lng: Double): Flow<Resource<UvIndex>>
}
