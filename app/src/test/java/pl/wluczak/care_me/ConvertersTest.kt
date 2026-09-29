package pl.wluczak.care_me

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.wluczak.care_me.data.local.Converters
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.RichTextSpan
import pl.wluczak.care_me.domain.model.SpanType

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun richTextContent_serializationAndDeserialization_isCorrect() {
        val original = RichTextContent(
            rawText = "Hello World",
            spans = listOf(
                RichTextSpan(0, 5, SpanType.BOLD),
                RichTextSpan(6, 11, SpanType.COLOR, value = "#FF0000")
            )
        )

        val jsonString = converters.fromRichTextContent(original)
        val deserialized = converters.toRichTextContent(jsonString)

        assertEquals(original, deserialized)
    }

    @Test
    fun listString_serializationAndDeserialization_isCorrect() {
        val list = listOf("apple", "banana", "cherry")
        val jsonString = converters.fromList(list)
        val deserialized = converters.toList(jsonString)

        assertEquals(list, deserialized)
    }
}
