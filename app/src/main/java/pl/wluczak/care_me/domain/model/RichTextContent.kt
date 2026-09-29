package pl.wluczak.care_me.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents formatted text with spans and raw text content.
 */
@Serializable
data class RichTextContent(
    val rawText: String = "",
    val spans: List<RichTextSpan> = emptyList()
)

/**
 * Represents a span within rich text for formatting.
 */
@Serializable
data class RichTextSpan(
    val start: Int,
    val end: Int,
    val type: SpanType,
    val value: String? = null
)

/**
 * Supported text formatting span types.
 */
@Serializable
enum class SpanType {
    BOLD,
    ITALIC,
    COLOR,
    BULLET_LIST
}
