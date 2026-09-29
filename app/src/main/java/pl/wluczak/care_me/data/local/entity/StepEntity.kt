package pl.wluczak.care_me.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.wluczak.care_me.domain.model.RichTextContent

/**
 * Room entity for Step, linked to ActivityEntity with CASCADE deletion.
 */
@Entity(
    tableName = "steps",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["activityId"])]
)
data class StepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: Long,
    val orderIndex: Int,
    val content: RichTextContent
)
