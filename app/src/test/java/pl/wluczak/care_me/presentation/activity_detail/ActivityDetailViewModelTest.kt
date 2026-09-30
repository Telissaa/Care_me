package pl.wluczak.care_me.presentation.activity_detail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.Step
import pl.wluczak.care_me.domain.repository.ActivityRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityDetailViewModelTest {

    private lateinit var fakeRepository: FakeActivityRepository
    private lateinit var viewModel: ActivityDetailViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testDispatcherProvider = object : DispatcherProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
        override val unconfined = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeActivityRepository()
        
        // Setup initial activity
        runBlocking {
            fakeRepository.insertActivity(Activity(id = 1L, name = "Skincare Routine", description = "Morning skincare", iconResId = 1))
        }

        viewModel = ActivityDetailViewModel(
            activityId = 1L,
            activityRepository = fakeRepository,
            dispatcherProvider = testDispatcherProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads activity and steps`() = runBlocking {
        val state = viewModel.uiState.first()
        
        assertFalse(state.isLoading)
        assertEquals("Skincare Routine", state.activity?.name)
        assertEquals(0, state.steps.size)
    }

    @Test
    fun `addStep creates a new step with incremented order index`() = runBlocking {
        viewModel.addStep()
        
        val state = viewModel.uiState.first()
        assertEquals(1, state.steps.size)
        assertEquals(0, state.steps[0].orderIndex)

        viewModel.addStep()
        val updatedState = viewModel.uiState.first()
        assertEquals(2, updatedState.steps.size)
        assertEquals(1, updatedState.steps[1].orderIndex)
    }

    @Test
    fun `updateStepContent updates content of specified step`() = runBlocking {
        viewModel.addStep()
        val initialSteps = viewModel.uiState.first().steps
        val stepId = initialSteps[0].id

        val newContent = RichTextContent(rawText = "Apply cleanser")
        viewModel.updateStepContent(stepId, newContent)

        val updatedState = viewModel.uiState.first()
        assertEquals("Apply cleanser", updatedState.steps[0].content.rawText)
    }

    @Test
    fun `moveStep reorders steps correctly`() = runBlocking {
        viewModel.addStep()
        viewModel.addStep()
        
        val stepsAfterAdd = viewModel.uiState.first().steps
        val step0Id = stepsAfterAdd[0].id
        val step1Id = stepsAfterAdd[1].id

        viewModel.updateStepContent(step0Id, RichTextContent(rawText = "First"))
        viewModel.updateStepContent(step1Id, RichTextContent(rawText = "Second"))

        // Move step 0 to index 1
        viewModel.moveStep(0, 1)

        val reorderedState = viewModel.uiState.first()
        assertEquals("Second", reorderedState.steps[0].content.rawText)
        assertEquals("First", reorderedState.steps[1].content.rawText)
    }

    @Test
    fun `deleteStep removes step from repository`() = runBlocking {
        viewModel.addStep()
        val stepId = viewModel.uiState.first().steps[0].id

        viewModel.deleteStep(stepId)

        val finalState = viewModel.uiState.first()
        assertEquals(0, finalState.steps.size)
    }

    private class FakeActivityRepository : ActivityRepository {
        private val activities = mutableListOf<Activity>()
        private val steps = mutableListOf<Step>()

        private val activitiesFlow = MutableStateFlow<List<Activity>>(emptyList())
        private val stepsFlowMap = mutableMapOf<Long, MutableStateFlow<List<Step>>>()

        private fun getStepsFlow(activityId: Long): MutableStateFlow<List<Step>> {
            return stepsFlowMap.getOrPut(activityId) {
                MutableStateFlow(steps.filter { it.activityId == activityId }.sortedBy { it.orderIndex })
            }
        }

        override fun getAllActivities(): Flow<List<Activity>> = activitiesFlow

        override fun getActivityById(id: Long): Flow<Activity?> {
            return MutableStateFlow(activities.find { it.id == id })
        }

        override suspend fun insertActivity(activity: Activity): Long {
            activities.add(activity)
            activitiesFlow.value = activities.toList()
            return activity.id
        }

        override suspend fun updateActivity(activity: Activity) {
            val index = activities.indexOfFirst { it.id == activity.id }
            if (index != -1) {
                activities[index] = activity
                activitiesFlow.value = activities.toList()
            }
        }

        override suspend fun deleteActivity(activity: Activity) {
            activities.removeIf { it.id == activity.id }
            activitiesFlow.value = activities.toList()
        }

        override fun getStepsForActivity(activityId: Long): Flow<List<Step>> = getStepsFlow(activityId)

        override suspend fun insertStep(step: Step): Long {
            val generatedId = if (step.id == 0L) (steps.maxOfOrNull { it.id } ?: 0L) + 1L else step.id
            val newStep = step.copy(id = generatedId)
            steps.add(newStep)
            getStepsFlow(step.activityId).value = steps.filter { it.activityId == step.activityId }.sortedBy { it.orderIndex }
            return generatedId
        }

        override suspend fun insertSteps(steps: List<Step>): List<Long> {
            return steps.map { insertStep(it) }
        }

        override suspend fun updateStep(step: Step) {
            val index = steps.indexOfFirst { it.id == step.id }
            if (index != -1) {
                steps[index] = step
                getStepsFlow(step.activityId).value = steps.filter { it.activityId == step.activityId }.sortedBy { it.orderIndex }
            }
        }

        override suspend fun updateSteps(steps: List<Step>) {
            steps.forEach { updateStep(it) }
        }

        override suspend fun deleteStep(step: Step) {
            deleteStepById(step.id)
        }

        override suspend fun deleteStepById(id: Long) {
            val step = steps.find { it.id == id }
            if (step != null) {
                steps.remove(step)
                getStepsFlow(step.activityId).value = steps.filter { it.activityId == step.activityId }.sortedBy { it.orderIndex }
            }
        }

        override fun getRemindersForActivity(activityId: Long) = throw NotImplementedError()
        override suspend fun insertReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun updateReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun deleteReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun deleteReminderById(id: Long) = throw NotImplementedError()
    }
}
