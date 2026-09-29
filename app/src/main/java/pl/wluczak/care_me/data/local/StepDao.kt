package pl.wluczak.care_me.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.data.local.entity.StepEntity

/**
 * DAO interface for Step table operations.
 */
@Dao
interface StepDao {
    @Query("SELECT * FROM steps WHERE activityId = :activityId ORDER BY orderIndex ASC")
    fun getStepsForActivity(activityId: Long): Flow<List<StepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: StepEntity): Long

    @Update
    suspend fun updateStep(step: StepEntity)

    @Delete
    suspend fun deleteStep(step: StepEntity)
}
