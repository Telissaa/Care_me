package pl.wluczak.care_me.presentation.activity_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.Step
import pl.wluczak.care_me.domain.repository.ActivityRepository

data class ActivityDetailUiState(
    val activity: Activity? = null,
    val steps: List<Step> = emptyList(),
    val isLoading: Boolean = true
)

class ActivityDetailViewModel(
    private val activityId: Long,
    private val activityRepository: ActivityRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailUiState())
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()

    private var updateStepJob: Job? = null

    init {
        loadActivityDetails()
    }

    private fun loadActivityDetails() {
        activityRepository.getActivityById(activityId)
            .onEach { activity ->
                _uiState.update { it.copy(activity = activity, isLoading = false) }
            }
            .flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope)

        activityRepository.getStepsForActivity(activityId)
            .onEach { steps ->
                _uiState.update { currentState ->
                    // Preserve local in-flight step edits if list size matches
                    if (currentState.steps.size == steps.size && currentState.steps.isNotEmpty()) {
                        currentState
                    } else {
                        currentState.copy(steps = steps.sortedBy { step -> step.orderIndex })
                    }
                }
            }
            .flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope)
    }

    fun addStep() {
        viewModelScope.launch(dispatcherProvider.io) {
            val currentSteps = _uiState.value.steps
            val newOrderIndex = if (currentSteps.isEmpty()) 0 else currentSteps.maxOf { it.orderIndex } + 1
            val newStep = Step(
                activityId = activityId,
                orderIndex = newOrderIndex,
                content = RichTextContent(rawText = "")
            )
            activityRepository.insertStep(newStep)
        }
    }

    fun updateStepContent(stepId: Long, content: RichTextContent) {
        _uiState.update { state ->
            val updatedSteps = state.steps.map { step ->
                if (step.id == stepId) step.copy(content = content) else step
            }
            state.copy(steps = updatedSteps)
        }

        updateStepJob?.cancel()
        updateStepJob = viewModelScope.launch(dispatcherProvider.io) {
            delay(300)
            val step = _uiState.value.steps.find { it.id == stepId }
            if (step != null) {
                activityRepository.updateStep(step)
            }
        }
    }

    fun moveStep(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch(dispatcherProvider.io) {
            val steps = _uiState.value.steps.toMutableList()
            if (fromIndex < 0 || fromIndex >= steps.size || toIndex < 0 || toIndex >= steps.size) return@launch

            val movedItem = steps.removeAt(fromIndex)
            steps.add(toIndex, movedItem)

            val reorderedSteps = steps.mapIndexed { index, step ->
                step.copy(orderIndex = index)
            }

            // Immediately update local UI state to reflect new order
            _uiState.update { it.copy(steps = reorderedSteps) }

            activityRepository.updateSteps(reorderedSteps)
        }
    }

    fun deleteStep(stepId: Long) {
        viewModelScope.launch(dispatcherProvider.io) {
            activityRepository.deleteStepById(stepId)
        }
    }
}
