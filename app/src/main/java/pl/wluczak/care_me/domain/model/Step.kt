package pl.wluczak.care_me.domain.model

/**
 * Represents a step within a care activity.
 */
data class Step(
    val id: Long = 0,
    val activityId: Long,
    val orderIndex: Int,
    val content: RichTextContent
)
