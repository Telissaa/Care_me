package pl.wluczak.care_me.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data class ActivityDetail(val activityId: Long) : Screen
}
