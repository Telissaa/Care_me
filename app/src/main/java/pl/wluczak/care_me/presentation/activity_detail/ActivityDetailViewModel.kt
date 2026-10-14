package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.repository.ActivityRepository

data class ActivityDetailUiState(
    val activity: Activity? = null,
    val descriptionAnnotatedString: AnnotatedString? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false
)

class ActivityDetailViewModel(
    activityId: Long,
    private val activityRepository: ActivityRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailUiState())
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()

    private var currentActivityId: Long = activityId
    private var updateDescriptionJob: Job? = null

    init {
        loadActivityDetails()
    }

    private suspend fun parseHtml(html: String): AnnotatedString {
        return withContext(dispatcherProvider.default) {
            val state = RichTextState()
            state.setHtml(html)
            state.annotatedString
        }
    }

    private fun loadActivityDetails() {
        if (currentActivityId > 0) {
            activityRepository.getActivityById(currentActivityId)
                .onEach { activity ->
                    val annotatedString = activity?.descriptionHtml?.let { parseHtml(it) }
                    _uiState.update { 
                        it.copy(
                            activity = activity, 
                            descriptionAnnotatedString = annotatedString,
                            isLoading = false
                        ) 
                    }
                }
                .flowOn(dispatcherProvider.io)
                .launchIn(viewModelScope)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun updateActivityDescription(contentHtml: String) {
        val currentActivity = _uiState.value.activity
        if (currentActivity == null) return

        val updatedActivity = currentActivity.copy(descriptionHtml = contentHtml)
        
        _uiState.update { state ->
            state.copy(activity = updatedActivity)
        }

        viewModelScope.launch(dispatcherProvider.default) {
            val annotatedString = parseHtml(contentHtml)
            _uiState.update { state ->
                // Ensure we only update if the activity is still the same
                if (state.activity?.id == updatedActivity.id) {
                    state.copy(descriptionAnnotatedString = annotatedString)
                } else state
            }
        }

        updateDescriptionJob?.cancel()
        updateDescriptionJob = viewModelScope.launch(dispatcherProvider.io) {
            delay(300)
            activityRepository.updateActivity(updatedActivity)
        }
    }
}
