package pl.wluczak.care_me.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.wluczak.care_me.data.local.entity.ActivityEntity
import pl.wluczak.care_me.data.local.entity.ReminderEntity
import pl.wluczak.care_me.data.local.entity.StepEntity

/**
 * Room AppDatabase declaration for Care me app.
 */
@Database(
    entities = [ActivityEntity::class, StepEntity::class, ReminderEntity::class],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun stepDao(): StepDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "care_me_db"
    }
}
