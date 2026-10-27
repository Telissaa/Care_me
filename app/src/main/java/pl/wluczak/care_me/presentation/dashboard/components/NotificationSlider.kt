package pl.wluczak.care_me.presentation.dashboard.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.wluczak.care_me.R
import pl.wluczak.care_me.presentation.activity_detail.RichTextEditor
import pl.wluczak.care_me.presentation.dashboard.NotificationItem
import pl.wluczak.care_me.ui.theme.FrostedBlueContainer
import pl.wluczak.care_me.ui.theme.TeaGreenContainer
import pl.wluczak.care_me.ui.theme.ThistleContainer

@Composable
fun NotificationSlider(
    notifications: List<NotificationItem>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(notifications, key = { _, item -> item.id }) { index, item ->
            val cardColor = when (index % 3) {
                0 -> ThistleContainer
                1 -> TeaGreenContainer
                else -> FrostedBlueContainer
            }

            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .width(320.dp)
                    .animateContentSize()
                    .clickable { expanded = !expanded },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(2.dp, Color(0xFF3BA2FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = item.activityName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                ),
                                color = Color.Black.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.notificationTitle ?: stringResource(R.string.default_notification_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = Color.Black,
                                maxLines = if (expanded) Int.MAX_VALUE else 1,
                                overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = item.timeRange,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp
                            ),
                            color = Color.Black.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    if (expanded) {
                        if (item.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            RichTextEditor(
                                value = item.description,
                                onValueChange = {},
                                isEditing = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (item.repeatDays.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.repeatDays,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
