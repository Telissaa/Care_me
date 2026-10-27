package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.wluczak.care_me.R
import pl.wluczak.care_me.presentation.dashboard.components.AVAILABLE_COLORS
import pl.wluczak.care_me.ui.theme.ThistleDark
import pl.wluczak.care_me.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    modifier: Modifier = Modifier.background(White, RoundedCornerShape(12.dp)),
    editorBackgroundColor: Color = Color.Transparent
) {
    val state = rememberRichTextState()
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (state.toHtml() != value) {
            state.setHtml(value)
        }
    }

    LaunchedEffect(state, isEditing) {
        snapshotFlow { state.annotatedString }
            .collectLatest {
                if (isEditing) {
                    onValueChange(state.toHtml())
                }
            }
    }

    LaunchedEffect(state.selection) {
        if (showColorPicker) {
            showColorPicker = false
        }
    }

    val currentSpanStyle = state.currentSpanStyle
    val isBold = currentSpanStyle.fontWeight == FontWeight.Bold
    val isItalic = currentSpanStyle.fontStyle == FontStyle.Italic
    val isUnderlined = currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true
    val isStrikethrough = currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true
    val isUnorderedList = state.isUnorderedList
    val isOrderedList = state.isOrderedList

    Column(modifier = modifier) {
        if (isEditing) {
            val scrollState = rememberScrollState()
            val scope = rememberCoroutineScope()
            val showLeftArrow by remember { derivedStateOf { scrollState.value > 0 } }
            val showRightArrow by remember { derivedStateOf { scrollState.value < scrollState.maxValue } }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    if (showLeftArrow) {
                        IconButton(onClick = { scope.launch { scrollState.animateScrollBy(-300f) } }) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft, 
                                contentDescription = stringResource(id = R.string.cd_scroll_left), 
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FormatToggleButton(
                        icon = Icons.Default.FormatBold,
                        description = stringResource(id = R.string.editor_bold),
                        isActive = isBold,
                        onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) }
                    )
                    FormatToggleButton(
                        icon = Icons.Default.FormatItalic,
                        description = stringResource(id = R.string.editor_italic),
                        isActive = isItalic,
                        onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) }
                    )
                    FormatToggleButton(
                        icon = Icons.Default.FormatUnderlined,
                        description = stringResource(id = R.string.editor_underline),
                        isActive = isUnderlined,
                        onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) }
                    )
                    FormatToggleButton(
                        icon = Icons.Default.StrikethroughS,
                        description = stringResource(id = R.string.editor_strikethrough),
                        isActive = isStrikethrough,
                        onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) }
                    )
                    FormatToggleButton(
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        description = stringResource(id = R.string.editor_bullet_list),
                        isActive = isUnorderedList,
                        onClick = { state.toggleUnorderedList() }
                    )
                    FormatToggleButton(
                        icon = Icons.Default.FormatListNumbered,
                        description = stringResource(id = R.string.editor_numbered_list),
                        isActive = isOrderedList,
                        onClick = { state.toggleOrderedList() }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)
                    IconButton(
                        onClick = { showColorPicker = !showColorPicker },
                        modifier = Modifier
                            .background(if (showColorPicker) Color.LightGray else Color.Transparent, CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Brush.sweepGradient(rainbowColors))
                                .border(1.dp, Color.Gray, CircleShape)
                        )
                    }
                }

                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    if (showRightArrow) {
                        IconButton(onClick = { scope.launch { scrollState.animateScrollBy(300f) } }) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight, 
                                contentDescription = stringResource(id = R.string.cd_scroll_right), 
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                // By applying fillMaxWidth and NOT setting a fixed height limit initially without scroll on the whole view, 
                // the Editor takes up space. If it still squishes text at the bottom, we ensure we use a column that scrolls
                // inside if needed. Wait, OutlinedRichTextEditor is internally a TextField.
                // We add a tiny bit of bottom padding to ensure the selection is not covered by the rounded corners/outline.
                OutlinedRichTextEditor(
                    state = state,
                    colors = RichTextEditorDefaults.outlinedRichTextEditorColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = ThistleDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(editorBackgroundColor, RoundedCornerShape(12.dp))
                        .padding(bottom = 4.dp)
                )
                
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    AnimatedVisibility(
                        visible = showColorPicker,
                        enter = fadeIn() + slideInVertically { -it / 3 },
                        exit = fadeOut() + slideOutVertically { -it / 3 }
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                                .width(220.dp) // Limits width to approx 5 items max in a row
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 5
                            ) {
                                AVAILABLE_COLORS.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (currentSpanStyle.color == color) 2.dp else 1.dp,
                                                color = if (currentSpanStyle.color == color) Color.Black else Color.Gray,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                state.toggleSpanStyle(SpanStyle(color = color))
                                                showColorPicker = false
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            RichText(
                state = state,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FormatToggleButton(
    icon: ImageVector,
    description: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) Color.LightGray else Color.Transparent
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(backgroundColor, CircleShape)
    ) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
