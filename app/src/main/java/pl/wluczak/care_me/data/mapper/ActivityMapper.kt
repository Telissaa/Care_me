package pl.wluczak.care_me.data.mapper

import pl.wluczak.care_me.data.local.entity.ActivityEntity
import pl.wluczak.care_me.domain.model.Activity

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        id = id,
        name = name,
        iconResId = iconResId,
        iconName = iconName,
        colorHex = colorHex,
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        name = name,
        iconResId = iconResId,
        iconName = iconName,
        colorHex = colorHex,
    )
}
