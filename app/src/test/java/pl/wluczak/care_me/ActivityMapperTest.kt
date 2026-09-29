package pl.wluczak.care_me

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.wluczak.care_me.data.local.entity.ActivityEntity
import pl.wluczak.care_me.data.local.entity.ReminderEntity
import pl.wluczak.care_me.data.local.entity.StepEntity
import pl.wluczak.care_me.data.mapper.toDomain
import pl.wluczak.care_me.data.mapper.toEntity
import pl.wluczak.care_me.domain.model.RichTextContent

class ActivityMapperTest {

    @Test
    fun activityEntity_toDomainAndBack_isSymmetric() {
        val entity = ActivityEntity(
            id = 1,
            name = "Morning Routine",
            description = "Start the day fresh",
            iconResId = 123,
        )

        val domain = entity.toDomain()
        val mappedEntity = domain.toEntity()

        assertEquals(entity, mappedEntity)
    }

    @Test
    fun stepEntity_toDomainAndBack_isSymmetric() {
        val entity = StepEntity(
            id = 10,
            activityId = 1,
            orderIndex = 0,
            content = RichTextContent(rawText = "Wash face"),
        )

        val domain = entity.toDomain()
        val mappedEntity = domain.toEntity()

        assertEquals(entity, mappedEntity)
    }

    @Test
    fun reminderEntity_toDomainAndBack_isSymmetric() {
        val entity = ReminderEntity(
            id = 100,
            activityId = 1,
            timeInMillis = 1700000000000L,
            isRepeating = true,
        )

        val domain = entity.toDomain()
        val mappedEntity = domain.toEntity()

        assertEquals(entity, mappedEntity)
    }
}
