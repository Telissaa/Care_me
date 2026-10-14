package pl.wluczak.care_me.data.converter

import pl.wluczak.care_me.domain.model.LegacyRichTextContent
import pl.wluczak.care_me.domain.model.LegacySpanType

@Suppress("DEPRECATION")
object LegacyToHtmlConverter {
    private data class Insertion(val index: Int, val tag: String, val isClose: Boolean, val start: Int, val spanLength: Int)

    fun convert(content: LegacyRichTextContent): String {
        val sb = StringBuilder(content.rawText)
        
        val insertions = mutableListOf<Insertion>()
        
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
            val spanLength = span.end - span.start
            insertions.add(Insertion(span.end, closeTag, isClose = true, start = span.start, spanLength = spanLength))
            insertions.add(Insertion(span.start, openTag, isClose = false, start = span.start, spanLength = spanLength))
        }
        
        // Sort descending by index
        insertions.sortWith(Comparator { a, b ->
            if (a.index != b.index) {
                b.index.compareTo(a.index)
            } else {
                when {
                    // Close tag should be inserted before Open tag at same index
                    a.isClose && !b.isClose -> -1
                    !a.isClose && b.isClose -> 1
                    // For two Close tags at same index: outer span (larger length / smaller start) should be inserted FIRST,
                    // so the inner span's close tag is inserted second (at the same index), making it appear BEFORE the outer close tag.
                    a.isClose && b.isClose -> b.spanLength.compareTo(a.spanLength)
                    // For two Open tags at same index: outer span (larger length) should be inserted SECOND,
                    // so it appears BEFORE the inner open tag in string.
                    else -> a.spanLength.compareTo(b.spanLength)
                }
            }
        })
        
        for (ins in insertions) {
            if (ins.index in 0..sb.length) {
                sb.insert(ins.index, ins.tag)
            }
        }
        
        return sb.toString().replace("\n", "<br>")
    }
}
