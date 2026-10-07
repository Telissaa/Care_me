package pl.wluczak.care_me.data.converter

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.wluczak.care_me.domain.model.LegacyRichTextContent
import pl.wluczak.care_me.domain.model.LegacyRichTextSpan
import pl.wluczak.care_me.domain.model.LegacySpanType

@Suppress("DEPRECATION")
class LegacyToHtmlConverterTest {

    @Test
    fun `test mapping LegacyRichTextContent to HTML string preserves text and basic spans`() {
        val originalContent = LegacyRichTextContent(
            rawText = "Hello World",
            spans = listOf(
                LegacyRichTextSpan(start = 0, end = 5, type = LegacySpanType.BOLD),
                LegacyRichTextSpan(start = 6, end = 11, type = LegacySpanType.ITALIC),
                LegacyRichTextSpan(start = 0, end = 11, type = LegacySpanType.COLOR, value = "#FFFF0000")
            )
        )

        val htmlString = LegacyToHtmlConverter.convert(originalContent)
        
        // Assert the HTML string is formed correctly
        assertEquals("<span style=\"color:#FFFF0000\"><b>Hello</b> <i>World</i></span>", htmlString)
    }

    @Test
    fun `test paragraph styles mapping for lists`() {
        val originalContent = LegacyRichTextContent(
            rawText = "1. First item\n• Second item",
            spans = listOf(
                LegacyRichTextSpan(start = 0, end = 13, type = LegacySpanType.NUMBERED_LIST),
                LegacyRichTextSpan(start = 14, end = 27, type = LegacySpanType.BULLET_LIST)
            )
        )

        val htmlString = LegacyToHtmlConverter.convert(originalContent)
        
        // Expected string format based on the LegacyToHtmlConverter logic
        // Original logic inserts opening and closing tags at exact indices.
        // start = 0, end = 13: "1. First item" -> <ol><li>1. First item</li></ol>
        // \n -> <br>
        // start = 14, end = 27: "• Second item" -> <ul><li>• Second item</li></ul>
        // Let's adjust expectation based on simple LegacyToHtmlConverter replacement.
        assertEquals("<ol><li>1. First item</li></ol><br><ul><li>• Second item</li></ul>", htmlString)
    }

    @Test
    fun `test text decoration mapping for underline and strikethrough`() {
        val originalContent = LegacyRichTextContent(
            rawText = "Underline Strikethrough",
            spans = listOf(
                LegacyRichTextSpan(start = 0, end = 9, type = LegacySpanType.UNDERLINE),
                LegacyRichTextSpan(start = 10, end = 23, type = LegacySpanType.STRIKETHROUGH)
            )
        )

        val htmlString = LegacyToHtmlConverter.convert(originalContent)

        assertEquals("<u>Underline</u> <s>Strikethrough</s>", htmlString)
    }
}
