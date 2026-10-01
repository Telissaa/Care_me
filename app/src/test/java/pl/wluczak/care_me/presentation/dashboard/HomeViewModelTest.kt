package pl.wluczak.care_me.presentation.dashboard

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
import pl.wluczak.care_me.domain.model.Step
import pl.wluczak.care_me.domain.repository.ActivityRepository

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var fakeRepository: FakeActivityRepository
    private lateinit var viewModel: HomeViewModel

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
        viewModel = HomeViewModel(
            activityRepository = fakeRepository,
            dispatcherProvider = testDispatcherProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads activities from repository`() = runBlocking {
        fakeRepository.insertActivity(Activity(id = 1L, name = "Skincare Routine", iconResId = 0))

        val state = viewModel.state.first()
        assertFalse(state.isLoading)
        assertEquals(1, state.activities.size)
        assertEquals("Skincare Routine", state.activities[0].name)
    }

    @Test
    fun `addActivity inserts activity into repository and calls callback with generated id`() = runBlocking {
        var createdId: Long? = null

        viewModel.addActivity("Night Routine") { newId ->
            createdId = newId
        }

        assertEquals(1L, createdId)

        val state = viewModel.state.first()
        assertEquals(1, state.activities.size)
        assertEquals("Night Routine", state.activities[0].name)
    }

    private class FakeActivityRepository : ActivityRepository {
        private val activities = mutableListOf<Activity>()
        private val activitiesFlow = MutableStateFlow<List<Activity>>(emptyList())

        override fun getAllActivities(): Flow<List<Activity>> = activitiesFlow

        override fun getActivityById(id: Long): Flow<Activity?> {
            return MutableStateFlow(activities.find { it.id == id })
        }

        override suspend fun insertActivity(activity: Activity): Long {
            val generatedId = if (activity.id == 0L) (activities.maxOfOrNull { it.id } ?: 0L) + 1L else activity.id
            val newActivity = activity.copy(id = generatedId)
            activities.add(newActivity)
            activitiesFlow.value = activities.toList()
            return generatedId
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

        override fun getStepsForActivity(activityId: Long): Flow<List<Step>> = throw NotImplementedError()
        override suspend fun insertStep(step: Step): Long = throw NotImplementedError()
        override suspend fun insertSteps(steps: List<Step>): List<Long> = throw NotImplementedError()
        override suspend fun updateStep(step: Step) = throw NotImplementedError()
        override suspend fun updateSteps(steps: List<Step>) = throw NotImplementedError()
        override suspend fun deleteStep(step: Step) = throw NotImplementedError()
        override suspend fun deleteStepById(id: Long) = throw NotImplementedError()
        private val remindersFlow = MutableStateFlow<List<Reminder>>(emptyList())

        override fun getRemindersForActivity(activityId: Long): Flow<List<Reminder>> = throw NotImplementedError()
        override fun getAllReminders(): Flow<List<Reminder>> = remindersFlow
        override suspend fun insertReminder(reminder: Reminder): Long = throw NotImplementedError()
        override suspend fun updateReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun deleteReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun deleteReminderById(id: Long) = throw NotImplementedError()
    }
}
