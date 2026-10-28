package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.presentation.dashboard.components.AVAILABLE_COLORS
import pl.wluczak.care_me.presentation.dashboard.components.parseHexColor
import pl.wluczak.care_me.presentation.dashboard.components.toHex
import pl.wluczak.care_me.ui.theme.White

import java.util.Locale

@Composable
fun NotificationDrawerContent(
    reminders: List<Reminder>,
    onAddReminder: () -> Unit,
    onUpdateReminder: (Reminder) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onAddReminder()
            }
        }
    )

    fun checkAndAddReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                onAddReminder()
            } else {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onAddReminder()
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(top = 90.dp, bottom = 90.dp)
            .width(320.dp)
            .background(
                color = Color(0xFFF9D6EC),
                shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
            )
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
            .consumeWindowInsets(PaddingValues(bottom = 106.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .imePadding(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reminders) { reminder ->
                    NotificationCard(
                        reminder = reminder,
                        onUpdateReminder = onUpdateReminder,
                        onDeleteReminder = onDeleteReminder
                    )
                }
            }
        }
        
        FloatingActionButton(
            onClick = { checkAndAddReminder() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFC0F0C0),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Dodaj powiadomienie")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCard(
    reminder: Reminder,
    onUpdateReminder: (Reminder) -> Unit,
    onDeleteReminder: (Reminder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    
    // Edit states
    var editTitle by remember(reminder.title) { mutableStateOf(reminder.title) }
    var editDescription by remember(reminder.descriptionHtml) { mutableStateOf(reminder.descriptionHtml) }
    var editColorHex by remember(reminder.colorHex) { mutableStateOf(reminder.colorHex) }
    
    var editStartTimeHour by remember(reminder.startTimeHour) { mutableIntStateOf(reminder.startTimeHour) }
    var editStartTimeMinute by remember(reminder.startTimeMinute) { mutableIntStateOf(reminder.startTimeMinute) }
    var editEndTimeHour by remember(reminder.endTimeHour) { mutableIntStateOf(reminder.endTimeHour) }
    var editEndTimeMinute by remember(reminder.endTimeMinute) { mutableIntStateOf(reminder.endTimeMinute) }

    var editRepeatType by remember(reminder.repeatType) { mutableStateOf(reminder.repeatType) }
    var editRepeatDaysOfWeek by remember(reminder.repeatDaysOfWeek) { mutableStateOf(reminder.repeatDaysOfWeek) }
    var editRepeatInterval by remember(reminder.repeatInterval) { mutableIntStateOf(reminder.repeatInterval) }

    var showColorPicker by remember { mutableStateOf(false) }
    
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showRecurrenceDialog by remember { mutableStateOf(false) }

    val fallbackColors = listOf(Color(0xFFE6FFEA), Color(0xFFEADDF9), Color(0xFFFCE1E4))
    val backgroundColor = if (editColorHex.isNotEmpty()) {
        parseHexColor(editColorHex)
    } else {
        fallbackColors[(reminder.id % 3).toInt().let { if(it<0) 0 else it }]
    }

    if (showStartTimePicker) {
        val state = rememberTimePickerState(initialHour = editStartTimeHour, initialMinute = editStartTimeMinute, is24Hour = true)
        TimePickerDialog(
            onCancel = { showStartTimePicker = false },
            onConfirm = {
                editStartTimeHour = state.hour
                editStartTimeMinute = state.minute
                showStartTimePicker = false
            }
        ) {
            TimePicker(state = state)
        }
    }
    if (showEndTimePicker) {
        val state = rememberTimePickerState(initialHour = editEndTimeHour, initialMinute = editEndTimeMinute, is24Hour = true)
        TimePickerDialog(
            onCancel = { showEndTimePicker = false },
            onConfirm = {
                editEndTimeHour = state.hour
                editEndTimeMinute = state.minute
                showEndTimePicker = false
            }
        ) {
            TimePicker(state = state)
        }
    }
    
    if (showRecurrenceDialog) {
        RecurrenceDialog(
            initialType = editRepeatType,
            initialDays = editRepeatDaysOfWeek,
            initialInterval = editRepeatInterval,
            onDismiss = { showRecurrenceDialog = false },
            onSave = { type, days, interval ->
                editRepeatType = type
                editRepeatDaysOfWeek = days
                editRepeatInterval = interval
                showRecurrenceDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                if (!isEditing) {
                    expanded = !expanded
                }
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        placeholder = { Text("Tytuł", fontSize = 18.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(130.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d", editStartTimeHour, editStartTimeMinute),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .clickable { showStartTimePicker = true }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                            Text(text = "-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d", editEndTimeHour, editEndTimeMinute),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .clickable { showEndTimePicker = true }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                        val daysPreview = if (editRepeatType == "INTERVAL") "co $editRepeatInterval dni" else {
                            if (editRepeatDaysOfWeek.isEmpty()) "brak"
                            else editRepeatDaysOfWeek.split(",").mapNotNull { mapOf("1" to "pon.", "2" to "wt.", "3" to "śr.", "4" to "czw.", "5" to "pt.", "6" to "sob.", "7" to "niedz.")[it] }.joinToString(", ")
                        }
                        Text(
                            text = daysPreview,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .clickable { showRecurrenceDialog = true }
                                .padding(4.dp)
                                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(4.dp)
                        )
                    }
                } else {
                    Text(
                        text = if (reminder.title.isEmpty()) "Nazwa" else reminder.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(top = 4.dp)
                    )
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(top = 4.dp)) {
                        Text(
                            text = reminder.formattedTimeRange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = reminder.formattedDays,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditing) {
                // Editing mode
                RichTextEditor(
                    value = editDescription,
                    onValueChange = { newDesc -> editDescription = newDesc },
                    isEditing = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(White, RoundedCornerShape(12.dp))
                )
                
                if (showColorPicker) {
                    Spacer(modifier = Modifier.height(8.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .border(1.dp, Color.Gray, CircleShape)
                                    .clickable {
                                        editColorHex = color.toHex()
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onDeleteReminder(reminder) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Usuń", tint = Color.Red.copy(alpha = 0.6f))
                    }
                    IconButton(onClick = { showColorPicker = !showColorPicker }) {
                        Icon(Icons.Filled.ColorLens, contentDescription = "Zmień kolor", tint = Color.Gray)
                    }
                    Button(
                        onClick = { 
                            isEditing = false 
                            showColorPicker = false
                            onUpdateReminder(
                                reminder.copy(
                                    title = editTitle.trim(),
                                    isRepeating = !(editRepeatType == "DAYS_OF_WEEK" && editRepeatDaysOfWeek.isEmpty()),
                                    startTimeHour = editStartTimeHour,
                                    startTimeMinute = editStartTimeMinute,
                                    endTimeHour = editEndTimeHour,
                                    endTimeMinute = editEndTimeMinute,
                                    repeatType = editRepeatType,
                                    repeatDaysOfWeek = editRepeatDaysOfWeek,
                                    repeatInterval = editRepeatInterval,
                                    descriptionHtml = editDescription,
                                    colorHex = editColorHex
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text("Zapisz")
                    }
                }
            } else {
                // View mode
                if (expanded) {
                    if (reminder.descriptionHtml.isNotBlank()) {
                        RichTextEditor(
                            value = reminder.descriptionHtml,
                            onValueChange = {},
                            isEditing = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { onDeleteReminder(reminder) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Usuń", tint = Color.Red.copy(alpha = 0.6f))
                        }
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edytuj", tint = Color.Gray)
                        }
                    }
                } else {
                    // Short snippet logic - strip simple HTML tags
                    val strippedDesc = reminder.descriptionHtml.replace(Regex("<[^>]*>"), "").trim()
                    if (strippedDesc.isNotEmpty()) {
                        Text(
                            text = strippedDesc,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        // Empty if nothing
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Anuluj") }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        text = { content() }
    )
}

@Composable
fun RecurrenceDialog(
    initialType: String,
    initialDays: String,
    initialInterval: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var interval by remember { mutableIntStateOf(initialInterval) }
    
    val selectedDays = remember { mutableStateListOf<String>() }
    LaunchedEffect(Unit) {
        if (initialDays.isNotEmpty()) {
            selectedDays.addAll(initialDays.split(","))
        }
    }
    
    val dayMap = mapOf(
        "1" to "Pn", "2" to "Wt", "3" to "Śr", "4" to "Cz", "5" to "Pt", "6" to "Sb", "7" to "Nd"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Powtarzanie") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "DAYS_OF_WEEK", onClick = { type = "DAYS_OF_WEEK" })
                    Text("Dni tygodnia", modifier = Modifier.clickable { type = "DAYS_OF_WEEK" })
                }
                if (type == "DAYS_OF_WEEK") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dayMap.forEach { (key, label) ->
                            val isSelected = selectedDays.contains(key)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
                                    .clickable {
                                        if (isSelected) selectedDays.remove(key) else selectedDays.add(key)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "INTERVAL", onClick = { type = "INTERVAL" })
                    Text("Co X dni", modifier = Modifier.clickable { type = "INTERVAL" })
                }
                if (type == "INTERVAL") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    ) {
                        IconButton(onClick = { if (interval > 1) interval-- }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Zmniejsz")
                        }
                        Text(interval.toString(), fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        IconButton(onClick = { interval++ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Zwiększ")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(type, selectedDays.joinToString(","), interval) }) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
