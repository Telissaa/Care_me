package pl.wluczak.care_me.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.wluczak.care_me.domain.model.RichTextContent

/**
 * Room type converters using kotlinx.serialization.
 */
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromRichTextContent(content: RichTextContent): String {
        return json.encodeToString(content)
    }

    @TypeConverter
    fun toRichTextContent(value: String): RichTextContent {
        return if (value.isBlank()) {
            RichTextContent()
        } else {
            try {
                json.decodeFromString(value)
            } catch (e: Exception) {
                RichTextContent(rawText = value)
            }
        }
    }
}
