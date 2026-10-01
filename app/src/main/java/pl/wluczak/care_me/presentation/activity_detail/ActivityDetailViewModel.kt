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
import java.util.concurrent.ConcurrentHashMap

data class ActivityDetailUiState(
    val activity: Activity? = null,
    val steps: List<Step> = emptyList(),
    val isLoading: Boolean = true
)

class ActivityDetailViewModel(
    activityId: Long,
    private val activityRepository: ActivityRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailUiState())
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()

    private var currentActivityId: Long = activityId
    private val updateStepJobs = ConcurrentHashMap<Long, Job>()

    init {
        loadActivityDetails()
    }

    private fun loadActivityDetails() {
        if (currentActivityId > 0) {
            activityRepository.getActivityById(currentActivityId)
                .onEach { activity ->
                    _uiState.update { it.copy(activity = activity, isLoading = false) }
                }
                .flowOn(dispatcherProvider.io)
                .launchIn(viewModelScope)

            activityRepository.getStepsForActivity(currentActivityId)
                .onEach { steps ->
                    _uiState.update { currentState ->
                        currentState.copy(steps = steps.sortedBy { step -> step.orderIndex })
                    }
                }
                .flowOn(dispatcherProvider.io)
                .launchIn(viewModelScope)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun addStep() {
        viewModelScope.launch(dispatcherProvider.io) {
            if (currentActivityId <= 0 || (_uiState.value.activity == null && !_uiState.value.isLoading)) {
                val newActivity = Activity(
                    name = "New Activity",
                    iconResId = 0
                )
                currentActivityId = activityRepository.insertActivity(newActivity)
                loadActivityDetails()
            }

            val currentSteps = _uiState.value.steps
            val newOrderIndex = if (currentSteps.isEmpty()) 0 else currentSteps.maxOf { it.orderIndex } + 1
            val newStep = Step(
                activityId = currentActivityId,
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

        updateStepJobs[stepId]?.cancel()
        updateStepJobs[stepId] = viewModelScope.launch(dispatcherProvider.io) {
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

            _uiState.update { it.copy(steps = reorderedSteps) }
            activityRepository.updateSteps(reorderedSteps)
        }
    }

    fun deleteStep(stepId: Long) {
        viewModelScope.launch(dispatcherProvider.io) {
            updateStepJobs.remove(stepId)?.cancel()
            activityRepository.deleteStepById(stepId)
        }
    }
}
