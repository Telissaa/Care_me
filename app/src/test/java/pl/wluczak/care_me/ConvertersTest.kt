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
                RichTextSpan(6, 11, SpanType.COLOR, value = "#FF0000"),
            ),
        )

        val jsonString = converters.fromRichTextContent(original)
        val deserialized = converters.toRichTextContent(jsonString)

        assertEquals(original, deserialized)
    }

    @Test
    fun richTextContent_deserializationWhenEmptyOrBlank_returnsDefaultRichTextContent() {
        val emptyResult = converters.toRichTextContent("")
        val blankResult = converters.toRichTextContent("   ")

        assertEquals(RichTextContent(), emptyResult)
        assertEquals(RichTextContent(), blankResult)
    }

    @Test
    fun richTextContent_deserializationWhenMalformedJson_returnsRawTextFallback() {
        val malformedString = "Plain unformatted string"
        val result = converters.toRichTextContent(malformedString)

        assertEquals(RichTextContent(rawText = malformedString), result)
    }

    @Test
    fun listString_serializationAndDeserialization_isCorrect() {
        val list = listOf("apple", "banana", "cherry")
        val jsonString = converters.fromList(list)
        val deserialized = converters.toList(jsonString)

        assertEquals(list, deserialized)
    }

    @Test
    fun listString_deserializationWhenEmptyOrMalformed_returnsEmptyList() {
        assertEquals(emptyList<String>(), converters.toList(""))
        assertEquals(emptyList<String>(), converters.toList("  "))
        assertEquals(emptyList<String>(), converters.toList("not a json array"))
    }
}
