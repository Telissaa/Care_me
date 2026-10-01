package pl.wluczak.care_me.domain.model

/**
 * Domain model representing a care activity.
 */
data class Activity(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val iconResId: Int = 0,
    val iconName: String = "face",
    val colorHex: String = "#D4F5FF"
)
