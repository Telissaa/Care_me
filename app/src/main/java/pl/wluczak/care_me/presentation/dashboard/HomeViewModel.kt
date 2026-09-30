package pl.wluczak.care_me.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.domain.repository.ActivityRepository

class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        activityRepository.getAllActivities()
            .onEach { activities ->
                _state.update { 
                    it.copy(
                        activities = activities, 
                        isLoading = false
                    ) 
                }
            }
            .flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope)
    }
}
