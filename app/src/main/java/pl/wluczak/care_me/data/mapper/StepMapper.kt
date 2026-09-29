package pl.wluczak.care_me.data.mapper

import pl.wluczak.care_me.data.local.entity.StepEntity
import pl.wluczak.care_me.domain.model.Step

/**
 * Mappers for converting between StepEntity and Step domain model.
 */
fun StepEntity.toDomain(): Step {
    return Step(
        id = id,
        activityId = activityId,
        orderIndex = orderIndex,
        content = content
    )
}

fun Step.toEntity(): StepEntity {
    return StepEntity(
        id = id,
        activityId = activityId,
        orderIndex = orderIndex,
        content = content
    )
}
