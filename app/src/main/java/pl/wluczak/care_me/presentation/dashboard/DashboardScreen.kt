package pl.wluczak.care_me.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.wluczak.care_me.R
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.ui.theme.FrostedBlueContainer
import pl.wluczak.care_me.ui.theme.LightMintContainer
import pl.wluczak.care_me.ui.theme.SoftLavenderContainer
import pl.wluczak.care_me.ui.theme.SoftPinkContainer
import pl.wluczak.care_me.ui.theme.SoftRedContainer
import pl.wluczak.care_me.ui.theme.SoftYellowContainer
import pl.wluczak.care_me.ui.theme.TeaGreenContainer
import pl.wluczak.care_me.ui.theme.ThistleContainer
import pl.wluczak.care_me.ui.theme.ThistleDark
import pl.wluczak.care_me.ui.theme.WarmPeachContainer
import pl.wluczak.care_me.ui.theme.WarmWhite

private val AVAILABLE_COLORS = listOf(
    FrostedBlueContainer,
    TeaGreenContainer,
    ThistleContainer,
    WarmPeachContainer,
    SoftPinkContainer,
    SoftLavenderContainer,
    LightMintContainer,
    SoftYellowContainer,
    SoftRedContainer
)

private val AVAILABLE_ICONS = listOf(
    "face" to Icons.Default.Face,
    "cut" to Icons.Default.ContentCut,
    "spa" to Icons.Default.Spa,
    "favorite" to Icons.Default.Favorite,
    "water" to Icons.Default.WaterDrop,
    "sunny" to Icons.Default.WbSunny,
    "star" to Icons.Default.Star,
    "fitness" to Icons.Default.FitnessCenter
)

fun getIconByName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "face" -> Icons.Default.Face
        "cut" -> Icons.Default.ContentCut
        "spa" -> Icons.Default.Spa
        "favorite" -> Icons.Default.Favorite
        "water" -> Icons.Default.WaterDrop
        "sunny" -> Icons.Default.WbSunny
        "star" -> Icons.Default.Star
        "fitness" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Face
    }
}

fun parseHexColor(colorHex: String): Color {
    return try {
        val cleaned = colorHex.removePrefix("#")
        val colorLong = cleaned.toLong(16)
        if (cleaned.length == 6) {
            Color(0xFF000000 or colorLong)
        } else {
            Color(colorLong)
        }
    } catch (_: Exception) {
        FrostedBlueContainer
    }
}

fun Color.toHex(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}

@Composable
fun DashboardScreen(
    onOpenActivity: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TeaGreenContainer,
                contentColor = ThistleDark,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_activity),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Notification Section Header
            Text(
                text = stringResource(id = R.string.current_notification),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification Slider / "Brak" text
            if (state.notifications.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_notifications),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xFFC8A5C5),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            } else {
                NotificationSlider(notifications = state.notifications)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Activities Grid Section
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ThistleDark)
                }
            } else if (state.activities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_activities_msg),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(state.activities, key = { it.id }) { activity ->
                        ActivityCircleCard(
                            activity = activity,
                            onClick = { onOpenActivity(activity.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddActivityDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description, colorHex, iconName ->
                    showAddDialog = false
                    viewModel.addActivity(
                        name = name,
                        description = description,
                        colorHex = colorHex,
                        iconName = iconName
                    ) { newId ->
                        onOpenActivity(newId)
                    }
                }
            )
        }
    }
}

@Composable
fun NotificationSlider(
    notifications: List<NotificationItem>,
    modifier: Modifier = Modifier
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

            Card(
                modifier = Modifier
                    .width(320.dp)
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(2.dp, Color(0xFF3BA2FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.activityName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Text(
                            text = item.timeRange,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp
                            ),
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = Color.Black.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityCircleCard(
    activity: Activity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = remember(activity.colorHex) { parseHexColor(activity.colorHex) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconByName(activity.iconName),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = Color.Black.copy(alpha = 0.85f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = activity.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            ),
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, colorHex: String, iconName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(AVAILABLE_COLORS.first()) }
    var selectedIconName by rememberSaveable { mutableStateOf(AVAILABLE_ICONS.first().first) }
    var isNameError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.add_activity))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (isNameError && it.isNotBlank()) {
                            isNameError = false
                        }
                    },
                    label = { Text(stringResource(id = R.string.activity_name)) },
                    singleLine = true,
                    isError = isNameError,
                    supportingText = if (isNameError) {
                        { Text(stringResource(id = R.string.error_name_required)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.activity_description)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(id = R.string.select_circle_color),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AVAILABLE_COLORS, key = { it.toHex() }) { color ->
                        val isSelected = color == selectedColor
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(id = R.string.select_icon),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AVAILABLE_ICONS, key = { it.first }) { (iconName, imageVector) ->
                        val isSelected = iconName == selectedIconName
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) TeaGreenContainer else Color.LightGray.copy(alpha = 0.2f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedIconName = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = imageVector,
                                contentDescription = iconName,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        onConfirm(
                            name.trim(),
                            description.trim(),
                            selectedColor.toHex(),
                            selectedIconName
                        )
                    }
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}