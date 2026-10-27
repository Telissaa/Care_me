package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.wluczak.care_me.R
import pl.wluczak.care_me.presentation.dashboard.components.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    activityId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityDetailViewModel = koinViewModel(parameters = { parametersOf(activityId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val backgroundColor = remember(uiState.activity?.colorHex) {
        uiState.activity?.colorHex?.let { parseHexColor(it) } ?: Color(0xFFE3F2FD)
    }
    
    var isDrawerOpen by remember { mutableStateOf(false) }
    
    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var dragOffset = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragOffset = 0f },
                    onDragEnd = { dragOffset = 0f },
                    onDragCancel = { dragOffset = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        dragOffset += dragAmount
                        if (dragOffset < -50f && !isDrawerOpen) {
                            isDrawerOpen = true
                            change.consume()
                            dragOffset = 0f // Reset after triggering
                        } else if (dragOffset > 50f && isDrawerOpen) {
                            isDrawerOpen = false
                            change.consume()
                            dragOffset = 0f // Reset after triggering
                        }
                    }
                )
            }
    ) {
        if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = stringResource(id = R.string.delete_activity_title))
            },
            text = {
                Text(text = stringResource(id = R.string.delete_activity_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteActivity(onSuccess = onBackClick)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = Color.DarkGray
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.activity?.name ?: stringResource(id = R.string.activity_detail_title))
                },
                actions = {
                    IconButton(onClick = { isDrawerOpen = !isDrawerOpen }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading) {
                if (uiState.isEditing) {
                    FloatingActionButton(
                        onClick = { viewModel.toggleEditMode() },
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(id = R.string.save),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                } else {
                    Row {
                        FloatingActionButton(
                            onClick = { showDeleteDialog = true },
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(id = R.string.delete),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = { viewModel.toggleEditMode() },
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(id = R.string.edit),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .imePadding()
            ) {
                if (uiState.isEditing) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Takes the rest of the height down to bottom
                            .padding(bottom = 16.dp), // Some padding from bottom
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            RichTextEditor(
                                value = uiState.activity?.descriptionHtml ?: "",
                                onValueChange = { newContent ->
                                    viewModel.updateActivityDescription(newContent)
                                },
                                isEditing = true,
                                editorBackgroundColor = backgroundColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 80.dp)
                    ) {
                        RichTextEditor(
                            value = uiState.activity?.descriptionHtml ?: "",
                            onValueChange = {},
                            isEditing = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
        
        // Notification Drawer Scrim
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { isDrawerOpen = false }
            )
        }

        // Notification Drawer Overlay
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            NotificationDrawerContent(
                reminders = uiState.reminders,
                onAddReminder = { viewModel.addEmptyReminder() },
                onUpdateReminder = { viewModel.updateReminder(it) },
                onDeleteReminder = { viewModel.deleteReminder(it) }
            )
        }
    }
}
