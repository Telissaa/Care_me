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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
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
            fakeRepository.insertActivity(Activity(id = 1L, name = "Skincare Routine", iconResId = 1))
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
    fun `initial state loads activity`() = runBlocking {
        val state = viewModel.uiState.first()
        
        assertFalse(state.isLoading)
        assertEquals("Skincare Routine", state.activity?.name)
        assertFalse(state.isEditing)
    }

    @Test
    fun `toggleEditMode toggles editing state`() = runBlocking {
        viewModel.toggleEditMode()
        
        var state = viewModel.uiState.first()
        assertTrue(state.isEditing)

        viewModel.toggleEditMode()
        state = viewModel.uiState.first()
        assertFalse(state.isEditing)
    }

    @Test
    fun `updateActivityDescription updates description and annotated string`() = runBlocking {
        val newContent = "Apply cleanser"
        viewModel.updateActivityDescription(newContent)

        val updatedState = viewModel.uiState.first()
        assertEquals("Apply cleanser", updatedState.activity?.descriptionHtml)
        assertEquals("Apply cleanser", updatedState.descriptionAnnotatedString?.text)
    }

    @Test
    fun `deleteActivity deletes activity and invokes onSuccess`() = runBlocking {
        var onSuccessCalled = false
        viewModel.deleteActivity {
            onSuccessCalled = true
        }

        assertTrue(onSuccessCalled)
        val activity = fakeRepository.getActivityById(1L).first()
        assertEquals(null, activity)
    }

    private class FakeActivityRepository : ActivityRepository {
        private val activities = mutableListOf<Activity>()
        private val activitiesFlow = MutableStateFlow<List<Activity>>(emptyList())

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

        override suspend fun deleteActivityById(id: Long) {
            activities.removeIf { it.id == id }
            activitiesFlow.value = activities.toList()
        }

        override fun getRemindersForActivity(activityId: Long) = throw NotImplementedError()
        override fun getAllReminders(): Flow<List<Reminder>> = throw NotImplementedError()
        override suspend fun insertReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun updateReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun deleteReminder(reminder: Reminder) = throw NotImplementedError()
        override suspend fun deleteReminderById(id: Long) = throw NotImplementedError()
    }
}
