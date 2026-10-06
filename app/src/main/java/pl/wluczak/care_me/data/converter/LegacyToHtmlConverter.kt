package pl.wluczak.care_me.data.converter

import pl.wluczak.care_me.domain.model.LegacyRichTextContent
import pl.wluczak.care_me.domain.model.LegacySpanType

@Suppress("DEPRECATION")
object LegacyToHtmlConverter {
    fun convert(content: LegacyRichTextContent): String {
        val sb = StringBuilder(content.rawText)
        
        // Very basic best-effort parsing: insert closing tags, then opening tags, from end to start
        // to not mess up indices.
        val insertions = mutableListOf<Pair<Int, String>>()
        
        for (span in content.spans) {
            val openTag = when (span.type) {
                LegacySpanType.BOLD -> "<b>"
                LegacySpanType.ITALIC -> "<i>"
                LegacySpanType.UNDERLINE -> "<u>"
                LegacySpanType.STRIKETHROUGH -> "<s>"
                LegacySpanType.COLOR -> "<span style=\"color:${span.value ?: "black"}\">"
                LegacySpanType.BULLET_LIST -> "<ul><li>"
                LegacySpanType.NUMBERED_LIST -> "<ol><li>"
                LegacySpanType.ALIGN_LEFT -> "<div style=\"text-align: left;\">"
                LegacySpanType.ALIGN_RIGHT -> "<div style=\"text-align: right;\">"
                LegacySpanType.ALIGN_CENTER -> "<div style=\"text-align: center;\">"
            }
            val closeTag = when (span.type) {
                LegacySpanType.BOLD -> "</b>"
                LegacySpanType.ITALIC -> "</i>"
                LegacySpanType.UNDERLINE -> "</u>"
                LegacySpanType.STRIKETHROUGH -> "</s>"
                LegacySpanType.COLOR -> "</span>"
                LegacySpanType.BULLET_LIST -> "</li></ul>"
                LegacySpanType.NUMBERED_LIST -> "</li></ol>"
                LegacySpanType.ALIGN_LEFT -> "</div>"
                LegacySpanType.ALIGN_RIGHT -> "</div>"
                LegacySpanType.ALIGN_CENTER -> "</div>"
            }
            insertions.add(Pair(span.end, closeTag))
            insertions.add(Pair(span.start, openTag))
        }
        
        // Sort by index descending. To avoid nested tag issues like <b><i>Text</b></i>
        // we should sort closing tags first if they are at the same index
        insertions.sortWith(Comparator { a, b -> 
            if (a.first != b.first) {
                b.first.compareTo(a.first) // descending order
            } else {
                // If same index, insert closing tag BEFORE opening tag
                if (a.second.startsWith("</") && !b.second.startsWith("</")) -1
                else if (!a.second.startsWith("</") && b.second.startsWith("</")) 1
                else 0
            }
        })
        
        for ((index, tag) in insertions) {
            if (index in 0..sb.length) {
                sb.insert(index, tag)
            }
        }
        
        return sb.toString().replace("\n", "<br>")
    }
}
