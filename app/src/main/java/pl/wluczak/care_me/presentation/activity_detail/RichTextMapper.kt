package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.RichTextSpan
import pl.wluczak.care_me.domain.model.SpanType

object RichTextMapper {
    fun toAnnotatedString(content: RichTextContent): AnnotatedString {
        val spanStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
        val paragraphStyles = mutableListOf<AnnotatedString.Range<ParagraphStyle>>()

        val textLength = content.rawText.length

        content.spans.forEach { span ->
            val start = span.start.coerceIn(0, textLength)
            val end = span.end.coerceIn(0, textLength)

            if (start < end) {
                val spanStyle = when (span.type) {
                    SpanType.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                    SpanType.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                    SpanType.UNDERLINE -> SpanStyle(textDecoration = TextDecoration.Underline)
                    SpanType.STRIKETHROUGH -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                    SpanType.COLOR -> {
                        span.value?.let { hex ->
                            try {
                                val cleanHex = hex.removePrefix("#")
                                val colorLong = when (cleanHex.length) {
                                    6 -> cleanHex.toLong(16) or 0xFF000000
                                    8 -> cleanHex.toLong(16)
                                    else -> throw IllegalArgumentException("Invalid color hex")
                                }
                                SpanStyle(color = Color(colorLong.toInt()))
                            } catch (_: Exception) {
                                null
                            }
                        }
                    }
                    else -> null
                }

                if (spanStyle != null) {
                    spanStyles.add(AnnotatedString.Range(spanStyle, start, end))
                }

                val paragraphStyle = when (span.type) {
                    SpanType.BULLET_LIST, SpanType.NUMBERED_LIST -> {
                        ParagraphStyle(textIndent = TextIndent(firstLine = 20.sp, restLine = 20.sp))
                    }
                    else -> null
                }

                if (paragraphStyle != null) {
                    paragraphStyles.add(AnnotatedString.Range(paragraphStyle, start, end))
                }
            }
        }

        return AnnotatedString(
            text = content.rawText,
            spanStyles = spanStyles,
            paragraphStyles = paragraphStyles
        )
    }

    fun toRichTextContent(annotatedString: AnnotatedString): RichTextContent {
        val spans = mutableListOf<RichTextSpan>()
        val textLength = annotatedString.text.length

        annotatedString.spanStyles.forEach { range ->
            val start = range.start.coerceIn(0, textLength)
            val end = range.end.coerceIn(0, textLength)
            if (start < end) {
                val style = range.item
                if (style.fontWeight == FontWeight.Bold) {
                    spans.add(RichTextSpan(start, end, SpanType.BOLD))
                }
                if (style.fontStyle == FontStyle.Italic) {
                    spans.add(RichTextSpan(start, end, SpanType.ITALIC))
                }
                if (style.textDecoration?.contains(TextDecoration.Underline) == true) {
                    spans.add(RichTextSpan(start, end, SpanType.UNDERLINE))
                }
                if (style.textDecoration?.contains(TextDecoration.LineThrough) == true) {
                    spans.add(RichTextSpan(start, end, SpanType.STRIKETHROUGH))
                }
                if (style.color != Color.Unspecified) {
                    val hex = String.format("#%08X", style.color.toArgb())
                    spans.add(RichTextSpan(start, end, SpanType.COLOR, hex))
                }
            }
        }

        annotatedString.paragraphStyles.forEach { range ->
            val start = range.start.coerceIn(0, textLength)
            val end = range.end.coerceIn(0, textLength)
            if (start < end) {
                if (range.item.textIndent != null) {
                    val substring = annotatedString.text.substring(start, end).trimStart()
                    if (substring.isNotEmpty() && substring.first().isDigit()) {
                        spans.add(RichTextSpan(start, end, SpanType.NUMBERED_LIST))
                    } else {
                        spans.add(RichTextSpan(start, end, SpanType.BULLET_LIST))
                    }
                }
            }
        }

        return RichTextContent(
            rawText = annotatedString.text,
            spans = spans
        )
    }

    /**
     * Shifts existing spans in content when rawText is modified from oldText to newText.
     */
    fun updateTextAndShiftSpans(
        content: RichTextContent,
        newText: String
    ): RichTextContent {
        val oldText = content.rawText
        if (oldText == newText) return content

        var prefixLen = 0
        while (prefixLen < oldText.length && prefixLen < newText.length && oldText[prefixLen] == newText[prefixLen]) {
            prefixLen++
        }

        var suffixLen = 0
        while (suffixLen < (oldText.length - prefixLen) &&
            suffixLen < (newText.length - prefixLen) &&
            oldText[oldText.length - 1 - suffixLen] == newText[newText.length - 1 - suffixLen]
        ) {
            suffixLen++
        }

        val deletedStart = prefixLen
        val deletedEnd = oldText.length - suffixLen
        val insertedLength = newText.length - prefixLen - suffixLen
        val deletedLength = deletedEnd - deletedStart

        val updatedSpans = content.spans.mapNotNull { span ->
            var start = span.start
            var end = span.end

            if (end <= deletedStart) {
                if (end == deletedStart && deletedLength == 0) {
                    end += insertedLength
                }
            } else if (start >= deletedEnd) {
                start += insertedLength - deletedLength
                end += insertedLength - deletedLength
            } else {
                if (start > deletedStart) {
                    start = deletedStart + insertedLength
                }
                if (end < deletedEnd) {
                    end = deletedStart
                } else {
                    end += insertedLength - deletedLength
                }
            }

            start = start.coerceIn(0, newText.length)
            end = end.coerceIn(0, newText.length)

            if (start < end) {
                span.copy(start = start, end = end)
            } else {
                null
            }
        }

        return RichTextContent(
            rawText = newText,
            spans = updatedSpans
        )
    }
}
