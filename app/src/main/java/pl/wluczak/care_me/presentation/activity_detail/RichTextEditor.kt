package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.RichTextSpan
import pl.wluczak.care_me.domain.model.SpanType

@Composable
fun RichTextEditor(
    value: RichTextContent,
    onValueChange: (RichTextContent) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(RichTextMapper.toAnnotatedString(value)))
    }

    LaunchedEffect(value) {
        val mappedString = RichTextMapper.toAnnotatedString(value)
        if (textFieldValue.annotatedString.text != mappedString.text ||
            textFieldValue.annotatedString.spanStyles != mappedString.spanStyles
        ) {
            textFieldValue = textFieldValue.copy(annotatedString = mappedString)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
        ) {
            ToolbarButton("Bold") {
                applyToggle(textFieldValue, value, SpanType.BOLD) { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Italic") {
                applyToggle(textFieldValue, value, SpanType.ITALIC) { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Underline") {
                applyToggle(textFieldValue, value, SpanType.UNDERLINE) { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Strike") {
                applyToggle(textFieldValue, value, SpanType.STRIKETHROUGH) { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Red") {
                applyToggle(textFieldValue, value, SpanType.COLOR, "#FFFF0000") { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Blue") {
                applyToggle(textFieldValue, value, SpanType.COLOR, "#FF0000FF") { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Bullet") {
                applyToggle(textFieldValue, value, SpanType.BULLET_LIST) { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
            ToolbarButton("Numbered") {
                applyToggle(textFieldValue, value, SpanType.NUMBERED_LIST) { newValue, newContent ->
                    textFieldValue = newValue
                    onValueChange(newContent)
                }
            }
        }

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                val updatedContent = RichTextMapper.updateTextAndShiftSpans(value, newValue.text)
                val newAnnotatedString = RichTextMapper.toAnnotatedString(updatedContent)
                textFieldValue = newValue.copy(annotatedString = newAnnotatedString)
                onValueChange(updatedContent)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ToolbarButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(end = 4.dp),
        contentPadding = ButtonDefaults.TextButtonContentPadding
    ) {
        Text(text)
    }
}

private fun applyToggle(
    textFieldValue: TextFieldValue,
    currentContent: RichTextContent,
    spanType: SpanType,
    spanValue: String? = null,
    onResult: (TextFieldValue, RichTextContent) -> Unit
) {
    val selection = textFieldValue.selection
    val min = selection.min.coerceIn(0, currentContent.rawText.length)
    val max = selection.max.coerceIn(0, currentContent.rawText.length)

    if (min == max) return

    val existing = currentContent.spans.find {
        it.type == spanType && it.start <= min && it.end >= max
    }

    val newSpans = if (existing != null && (spanType != SpanType.COLOR || existing.value == spanValue)) {
        // Remove or split span
        val result = currentContent.spans.toMutableList()
        result.remove(existing)
        if (existing.start < min) {
            result.add(RichTextSpan(existing.start, min, spanType, existing.value))
        }
        if (existing.end > max) {
            result.add(RichTextSpan(max, existing.end, spanType, existing.value))
        }
        result
    } else {
        // Adding new span
        var result = currentContent.spans.toMutableList()

        if (spanType == SpanType.COLOR) {
            // Remove/trim any existing COLOR spans that overlap with min, max
            result = result.filterNot { it.type == SpanType.COLOR && it.start >= min && it.end <= max }.toMutableList()
        }

        result.add(RichTextSpan(min, max, spanType, spanValue))
        result
    }

    val newContent = currentContent.copy(spans = newSpans)
    val newAnnotatedString = RichTextMapper.toAnnotatedString(newContent)

    onResult(textFieldValue.copy(annotatedString = newAnnotatedString), newContent)
}
