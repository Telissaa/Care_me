package pl.wluczak.care_me.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Activity.
 */
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val iconResId: Int = 0,
    val iconName: String = "face",
    val colorHex: String = "#D4F5FF",
)
