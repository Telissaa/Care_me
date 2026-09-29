package pl.wluczak.care_me.domain.model

data class Activity(
    val id: Long = 0,
    val name: String,
    val description: String,
    val iconResId: Int
)
