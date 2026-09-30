package pl.wluczak.care_me.presentation.activity_detail

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.RichTextSpan
import pl.wluczak.care_me.domain.model.SpanType

class RichTextMapperTest {

    @Test
    fun `test mapping RichTextContent to AnnotatedString and back preserves text and spans`() {
        val originalContent = RichTextContent(
            rawText = "Hello World",
            spans = listOf(
                RichTextSpan(start = 0, end = 5, type = SpanType.BOLD),
                RichTextSpan(start = 6, end = 11, type = SpanType.ITALIC),
                RichTextSpan(start = 0, end = 11, type = SpanType.COLOR, value = "#FFFF0000")
            )
        )

        val annotatedString = RichTextMapper.toAnnotatedString(originalContent)
        
        // Assert the AnnotatedString has the correct text
        assertEquals("Hello World", annotatedString.text)
        
        val mappedBackContent = RichTextMapper.toRichTextContent(annotatedString)
        
        // Assert the text matches
        assertEquals("Hello World", mappedBackContent.rawText)
        
        // Assert all original spans are present in the mapped back content
        assertEquals(3, mappedBackContent.spans.size)
        
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.BOLD && it.start == 0 && it.end == 5 })
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.ITALIC && it.start == 6 && it.end == 11 })
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.COLOR && it.start == 0 && it.end == 11 && it.value == "#FFFF0000" })
    }

    @Test
    fun `test paragraph styles mapping for lists`() {
        val originalContent = RichTextContent(
            rawText = "1. First item\n• Second item",
            spans = listOf(
                RichTextSpan(start = 0, end = 13, type = SpanType.NUMBERED_LIST),
                RichTextSpan(start = 14, end = 27, type = SpanType.BULLET_LIST)
            )
        )

        val annotatedString = RichTextMapper.toAnnotatedString(originalContent)
        val mappedBackContent = RichTextMapper.toRichTextContent(annotatedString)

        assertEquals("1. First item\n• Second item", mappedBackContent.rawText)
        
        assertEquals(2, mappedBackContent.spans.size)
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.NUMBERED_LIST && it.start == 0 && it.end == 13 })
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.BULLET_LIST && it.start == 14 && it.end == 27 })
    }

    @Test
    fun `test text decoration mapping for underline and strikethrough`() {
        val originalContent = RichTextContent(
            rawText = "Underline Strikethrough",
            spans = listOf(
                RichTextSpan(start = 0, end = 9, type = SpanType.UNDERLINE),
                RichTextSpan(start = 10, end = 23, type = SpanType.STRIKETHROUGH)
            )
        )

        val annotatedString = RichTextMapper.toAnnotatedString(originalContent)
        val mappedBackContent = RichTextMapper.toRichTextContent(annotatedString)

        assertEquals(2, mappedBackContent.spans.size)
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.UNDERLINE && it.start == 0 && it.end == 9 })
        assertTrue(mappedBackContent.spans.any { it.type == SpanType.STRIKETHROUGH && it.start == 10 && it.end == 23 })
    }

    @Test
    fun `test updateTextAndShiftSpans shifts spans when inserting text at beginning`() {
        val content = RichTextContent(
            rawText = "World",
            spans = listOf(RichTextSpan(start = 0, end = 5, type = SpanType.BOLD))
        )

        // Prepend "Hello "
        val updated = RichTextMapper.updateTextAndShiftSpans(content, "Hello World")

        assertEquals("Hello World", updated.rawText)
        assertEquals(1, updated.spans.size)
        assertEquals(6, updated.spans[0].start)
        assertEquals(11, updated.spans[0].end)
    }

    @Test
    fun `test updateTextAndShiftSpans expands span when typing inside span`() {
        val content = RichTextContent(
            rawText = "Bold",
            spans = listOf(RichTextSpan(start = 0, end = 4, type = SpanType.BOLD))
        )

        // Insert " Text" inside "Bold" -> "Bold Text"
        val updated = RichTextMapper.updateTextAndShiftSpans(content, "Bold Text")

        assertEquals("Bold Text", updated.rawText)
        assertEquals(1, updated.spans.size)
        assertEquals(0, updated.spans[0].start)
        assertEquals(9, updated.spans[0].end)
    }
}
