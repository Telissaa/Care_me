package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberRichTextState()

    LaunchedEffect(value) {
        if (state.toHtml() != value) {
            state.setHtml(value)
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.annotatedString }
            .collectLatest {
                onValueChange(state.toHtml())
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
                state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }
            ToolbarButton("Italic") {
                state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }
            ToolbarButton("Underline") {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }
            ToolbarButton("Strike") {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
            }
            ToolbarButton("Red") {
                state.toggleSpanStyle(SpanStyle(color = Color.Red))
            }
            ToolbarButton("Blue") {
                state.toggleSpanStyle(SpanStyle(color = Color.Blue))
            }
            ToolbarButton("Bullet") {
                state.toggleUnorderedList()
            }
            ToolbarButton("Numbered") {
                state.toggleOrderedList()
            }
        }

        OutlinedRichTextEditor(
            state = state,
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
