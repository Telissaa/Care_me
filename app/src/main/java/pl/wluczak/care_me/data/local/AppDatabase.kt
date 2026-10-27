package pl.wluczak.care_me.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.wluczak.care_me.data.local.entity.ActivityEntity
import pl.wluczak.care_me.data.local.entity.ReminderEntity

/**
 * Room AppDatabase declaration for Care me app.
 */
@Database(
    entities = [ActivityEntity::class, ReminderEntity::class],
    version = 8,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "care_me_db"

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE steps ADD COLUMN contentHtml TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reminders ADD COLUMN title TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE reminders ADD COLUMN descriptionHtml TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE reminders ADD COLUMN timeRange TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE reminders ADD COLUMN daysOfWeek TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reminders ADD COLUMN colorHex TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new table matching the updated ReminderEntity (removed timeRange, daysOfWeek)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `reminders_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `activityId` INTEGER NOT NULL, 
                        `timeInMillis` INTEGER NOT NULL, 
                        `isRepeating` INTEGER NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `descriptionHtml` TEXT NOT NULL, 
                        `startTimeHour` INTEGER NOT NULL, 
                        `startTimeMinute` INTEGER NOT NULL, 
                        `endTimeHour` INTEGER NOT NULL, 
                        `endTimeMinute` INTEGER NOT NULL, 
                        `repeatType` TEXT NOT NULL, 
                        `repeatDaysOfWeek` TEXT NOT NULL, 
                        `repeatInterval` INTEGER NOT NULL, 
                        `colorHex` TEXT NOT NULL, 
                        FOREIGN KEY(`activityId`) REFERENCES `activities`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Copy existing data with default values for new columns
                db.execSQL("""
                    INSERT INTO reminders_new (
                        id, activityId, timeInMillis, isRepeating, title, descriptionHtml, colorHex,
                        startTimeHour, startTimeMinute, endTimeHour, endTimeMinute, repeatType, repeatDaysOfWeek, repeatInterval
                    )
                    SELECT 
                        id, activityId, timeInMillis, isRepeating, title, descriptionHtml, colorHex,
                        8, 0, 9, 0, 'DAYS_OF_WEEK', '', 1
                    FROM reminders
                """.trimIndent())
                
                // Remove the old table
                db.execSQL("DROP TABLE reminders")
                
                // Rename the new table
                db.execSQL("ALTER TABLE reminders_new RENAME TO reminders")
                
                // Re-create indexes
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reminders_activityId` ON `reminders` (`activityId`)")
            }
        }
    }
}
