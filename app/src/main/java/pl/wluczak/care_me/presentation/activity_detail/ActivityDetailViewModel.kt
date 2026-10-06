package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import pl.wluczak.care_me.domain.model.Step
import pl.wluczak.care_me.domain.repository.ActivityRepository
import java.util.concurrent.ConcurrentHashMap

data class StepUI(
    val id: Long,
    val activityId: Long,
    val orderIndex: Int,
    val contentHtml: String,
    val cachedAnnotatedString: AnnotatedString? = null
)

data class ActivityDetailUiState(
    val activity: Activity? = null,
    val steps: List<StepUI> = emptyList(),
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
                    _uiState.update { it.copy(activity = activity, isLoading = false) }
                }
                .flowOn(dispatcherProvider.io)
                .launchIn(viewModelScope)

            activityRepository.getStepsForActivity(currentActivityId)
                .onEach { steps ->
                    val sortedSteps = steps.sortedBy { step -> step.orderIndex }
                    
                    val stepUIs = withContext(dispatcherProvider.default) {
                        sortedSteps.map { step ->
                            async {
                                StepUI(
                                    id = step.id,
                                    activityId = step.activityId,
                                    orderIndex = step.orderIndex,
                                    contentHtml = step.contentHtml,
                                    cachedAnnotatedString = parseHtml(step.contentHtml)
                                )
                            }
                        }.awaitAll()
                    }

                    _uiState.update { currentState ->
                        currentState.copy(steps = stepUIs)
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
                contentHtml = ""
            )
            activityRepository.insertStep(newStep)
        }
    }

    fun updateStepContent(stepId: Long, contentHtml: String) {
        _uiState.update { state ->
            val updatedSteps = state.steps.map { step ->
                if (step.id == stepId) step.copy(contentHtml = contentHtml) else step
            }
            state.copy(steps = updatedSteps)
        }

        viewModelScope.launch(dispatcherProvider.default) {
            val annotatedString = parseHtml(contentHtml)
            _uiState.update { state ->
                val updatedSteps = state.steps.map { step ->
                    if (step.id == stepId && step.contentHtml == contentHtml) {
                        step.copy(cachedAnnotatedString = annotatedString)
                    } else step
                }
                state.copy(steps = updatedSteps)
            }
        }

        updateStepJobs[stepId]?.cancel()
        updateStepJobs[stepId] = viewModelScope.launch(dispatcherProvider.io) {
            delay(300)
            val stepUi = _uiState.value.steps.find { it.id == stepId }
            if (stepUi != null) {
                val step = Step(
                    id = stepUi.id,
                    activityId = stepUi.activityId,
                    orderIndex = stepUi.orderIndex,
                    contentHtml = contentHtml
                )
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
            
            val domainSteps = reorderedSteps.map {
                Step(
                    id = it.id,
                    activityId = it.activityId,
                    orderIndex = it.orderIndex,
                    contentHtml = it.contentHtml
                )
            }
            activityRepository.updateSteps(domainSteps)
        }
    }

    fun deleteStep(stepId: Long) {
        viewModelScope.launch(dispatcherProvider.io) {
            updateStepJobs.remove(stepId)?.cancel()
            activityRepository.deleteStepById(stepId)
        }
    }
}
