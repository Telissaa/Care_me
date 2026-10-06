package pl.wluczak.care_me.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.wluczak.care_me.domain.model.LegacyRichTextContent

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
        return if (value.isBlank()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(value)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    @TypeConverter
    fun fromRichTextContent(content: LegacyRichTextContent): String {
        return json.encodeToString(content)
    }

    @TypeConverter
    fun toRichTextContent(value: String): LegacyRichTextContent {
        return if (value.isBlank()) {
            LegacyRichTextContent()
        } else {
            try {
                json.decodeFromString(value)
            } catch (_: Exception) {
                LegacyRichTextContent(rawText = value)
            }
        }
    }
}
