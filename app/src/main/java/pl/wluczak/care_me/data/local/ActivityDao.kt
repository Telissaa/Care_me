package pl.wluczak.care_me.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.data.local.entity.ActivityEntity

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Upsert
    suspend fun insertActivity(activity: ActivityEntity)
}
