package pl.wluczak.care_me.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents formatted text with spans and raw text content.
 */
@Deprecated("Used only for local DB migration")
@Serializable
data class LegacyRichTextContent(
    val rawText: String = "",
    val spans: List<LegacyRichTextSpan> = emptyList(),
)

/**
 * Represents a span within rich text for formatting.
 */
@Deprecated("Used only for local DB migration")
@Serializable
data class LegacyRichTextSpan(
    val start: Int,
    val end: Int,
    val type: LegacySpanType,
    val value: String? = null,
)

/**
 * Supported text formatting span types.
 */
@Deprecated("Used only for local DB migration")
@Serializable
enum class LegacySpanType {
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH,
    COLOR,
    BULLET_LIST,
    NUMBERED_LIST,
    ALIGN_LEFT,
    ALIGN_RIGHT,
    ALIGN_CENTER,
}
