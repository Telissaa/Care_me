package pl.wluczak.care_me

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import pl.wluczak.care_me.data.local.ActivityDao
import pl.wluczak.care_me.data.local.ReminderDao
import pl.wluczak.care_me.data.local.StepDao
import pl.wluczak.care_me.data.local.entity.ActivityEntity
import pl.wluczak.care_me.data.local.entity.ReminderEntity
import pl.wluczak.care_me.data.local.entity.StepEntity
import pl.wluczak.care_me.data.repository.ActivityRepositoryImpl
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.domain.model.RichTextContent
import pl.wluczak.care_me.domain.model.Step

class ActivityRepositoryImplTest {

    private lateinit var fakeActivityDao: FakeActivityDao
    private lateinit var fakeStepDao: FakeStepDao
    private lateinit var fakeReminderDao: FakeReminderDao
    private lateinit var repository: ActivityRepositoryImpl

    @Before
    fun setUp() {
        fakeActivityDao = FakeActivityDao()
        fakeStepDao = FakeStepDao()
        fakeReminderDao = FakeReminderDao()
        repository = ActivityRepositoryImpl(fakeActivityDao, fakeStepDao, fakeReminderDao)
    }

    @Test
    fun getAllActivities_returnsDomainActivities() = runBlocking {
        val entity = ActivityEntity(id = 1, name = "Skincare", description = "Daily skin routine", iconResId = 1)
        fakeActivityDao.insertActivity(entity)

        val activities = repository.getAllActivities().first()

        assertEquals(1, activities.size)
        assertEquals("Skincare", activities[0].name)
    }

    @Test
    fun getActivityById_returnsCorrectActivity() = runBlocking {
        val entity = ActivityEntity(id = 5, name = "Hydration", description = "Drink water", iconResId = 2)
        fakeActivityDao.insertActivity(entity)

        val result = repository.getActivityById(5).first()
        val nonExistent = repository.getActivityById(99).first()

        assertEquals("Hydration", result?.name)
        assertNull(nonExistent)
    }

    @Test
    fun updateAndDeleteActivity_worksCorrectly() = runBlocking {
        val initialActivity = Activity(id = 1, name = "Initial", description = "Desc", iconResId = 1)
        repository.insertActivity(initialActivity)

        val updatedActivity = initialActivity.copy(name = "Updated Name")
        repository.updateActivity(updatedActivity)

        val fetchedAfterUpdate = repository.getActivityById(1).first()
        assertEquals("Updated Name", fetchedAfterUpdate?.name)

        repository.deleteActivity(updatedActivity)
        val fetchedAfterDelete = repository.getAllActivities().first()
        assertEquals(0, fetchedAfterDelete.size)
    }

    @Test
    fun stepOperations_insertBatchAndGet_worksCorrectly() = runBlocking {
        val steps = listOf(
            Step(id = 1, activityId = 1, orderIndex = 0, content = RichTextContent("Step 1")),
            Step(id = 2, activityId = 1, orderIndex = 1, content = RichTextContent("Step 2")),
        )

        repository.insertSteps(steps)

        val fetchedSteps = repository.getStepsForActivity(1).first()

        assertEquals(2, fetchedSteps.size)
        assertEquals("Step 1", fetchedSteps[0].content.rawText)
        assertEquals("Step 2", fetchedSteps[1].content.rawText)
    }

    @Test
    fun reminderOperations_insertAndDeleteById_worksCorrectly() = runBlocking {
        val reminder = Reminder(id = 10, activityId = 1, timeInMillis = 1000L, isRepeating = false)
        repository.insertReminder(reminder)

        val remindersBefore = repository.getRemindersForActivity(1).first()
        assertEquals(1, remindersBefore.size)

        repository.deleteReminderById(10)

        val remindersAfter = repository.getRemindersForActivity(1).first()
        assertEquals(0, remindersAfter.size)
    }

    // --- Fakes for Testing ---

    private class FakeActivityDao : ActivityDao {
        private val activities = mutableListOf<ActivityEntity>()
        private val flow = MutableStateFlow<List<ActivityEntity>>(emptyList())

        override fun getAllActivities(): Flow<List<ActivityEntity>> = flow

        override fun getActivityById(id: Long): Flow<ActivityEntity?> {
            val itemFlow = MutableStateFlow(activities.find { it.id == id })
            return itemFlow
        }

        override suspend fun insertActivity(activity: ActivityEntity): Long {
            activities.add(activity)
            flow.value = activities.toList()
            return activity.id
        }

        override suspend fun updateActivity(activity: ActivityEntity) {
            val index = activities.indexOfFirst { it.id == activity.id }
            if (index != -1) {
                activities[index] = activity
                flow.value = activities.toList()
            }
        }

        override suspend fun deleteActivity(activity: ActivityEntity) {
            activities.removeAll { it.id == activity.id }
            flow.value = activities.toList()
        }
    }

    private class FakeStepDao : StepDao {
        private val steps = mutableListOf<StepEntity>()
        private val flowMap = mutableMapOf<Long, MutableStateFlow<List<StepEntity>>>()

        private fun getFlowForActivity(activityId: Long): MutableStateFlow<List<StepEntity>> {
            return flowMap.getOrPut(activityId) {
                MutableStateFlow(steps.filter { it.activityId == activityId }.sortedBy { it.orderIndex })
            }
        }

        override fun getStepsForActivity(activityId: Long): Flow<List<StepEntity>> {
            return getFlowForActivity(activityId)
        }

        override suspend fun insertStep(step: StepEntity): Long {
            steps.add(step)
            getFlowForActivity(step.activityId).value = steps.filter { it.activityId == step.activityId }.sortedBy { it.orderIndex }
            return step.id
        }

        override suspend fun insertSteps(steps: List<StepEntity>): List<Long> {
            steps.forEach { insertStep(it) }
            return steps.map { it.id }
        }

        override suspend fun updateStep(step: StepEntity) {
            val index = this.steps.indexOfFirst { it.id == step.id }
            if (index != -1) {
                this.steps[index] = step
                getFlowForActivity(step.activityId).value = this.steps.filter { it.activityId == step.activityId }.sortedBy { it.orderIndex }
            }
        }

        override suspend fun updateSteps(steps: List<StepEntity>) {
            steps.forEach { updateStep(it) }
        }

        override suspend fun deleteStep(step: StepEntity) {
            deleteStepById(step.id)
        }

        override suspend fun deleteStepById(id: Long) {
            val step = steps.find { it.id == id }
            if (step != null) {
                steps.remove(step)
                getFlowForActivity(step.activityId).value = steps.filter { it.activityId == step.activityId }.sortedBy { it.orderIndex }
            }
        }
    }

    private class FakeReminderDao : ReminderDao {
        private val reminders = mutableListOf<ReminderEntity>()
        private val flowMap = mutableMapOf<Long, MutableStateFlow<List<ReminderEntity>>>()

        private fun getFlowForActivity(activityId: Long): MutableStateFlow<List<ReminderEntity>> {
            return flowMap.getOrPut(activityId) {
                MutableStateFlow(reminders.filter { it.activityId == activityId })
            }
        }

        override fun getRemindersForActivity(activityId: Long): Flow<List<ReminderEntity>> {
            return getFlowForActivity(activityId)
        }

        override suspend fun insertReminder(reminder: ReminderEntity): Long {
            reminders.add(reminder)
            getFlowForActivity(reminder.activityId).value = reminders.filter { it.activityId == reminder.activityId }
            return reminder.id
        }

        override suspend fun deleteReminder(reminder: ReminderEntity) {
            deleteReminderById(reminder.id)
        }

        override suspend fun deleteReminderById(id: Long) {
            val reminder = reminders.find { it.id == id }
            if (reminder != null) {
                reminders.remove(reminder)
                getFlowForActivity(reminder.activityId).value = reminders.filter { it.activityId == reminder.activityId }
            }
        }
    }
}
