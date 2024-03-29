package com.github.pwoicik.uekschedule.features.activities.presentation.screens.createActivity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pwoicik.uekschedule.common.domain.ScheduleRepository
import com.github.pwoicik.uekschedule.features.activities.presentation.screens.destinations.CreateActivityScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CreateActivityViewModel @Inject constructor(
    private val repo: ScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs = CreateActivityScreenDestination.argsFrom(savedStateHandle)

    private val _state: MutableState<CreateActivityState?> = mutableStateOf(null)
    val state: State<CreateActivityState?>
        get() = _state

    init {
        val activityId = navArgs.activityId
        if (activityId == 0L) {
            _state.value = CreateActivityState()
        } else {
            viewModelScope.launch {
                _state.value = CreateActivityState(repo.getActivity(activityId))
            }
        }
    }

    private val _eventFlow = MutableSharedFlow<UiEvent>(replay = Int.MAX_VALUE)
    val eventFlow: SharedFlow<UiEvent>
        get() = _eventFlow

    fun emit(event: CreateActivityEvent) {
        val state = _state.value!!
        when (event) {
            is CreateActivityEvent.NameChanged ->
                _state.value = state.copy(
                    name = event.value.trimStart()
                )
            is CreateActivityEvent.DurationMinutesChanged -> {
                if (event.value.isDigitsOnly()) {
                    _state.value = state.copy(
                        durationMinutes = event.value
                    )
                }
            }
            is CreateActivityEvent.LocationChanged ->
                _state.value = state.copy(
                    location = event.value.trimStart()
                )
            is CreateActivityEvent.RepeatOnDaysOfWeekChanged ->
                _state.value = state.copy(
                    repeatOnDaysOfWeek = event.value
                )
            is CreateActivityEvent.TeacherChanged ->
                _state.value = state.copy(
                    teacher = event.value.trimStart()
                )
            is CreateActivityEvent.TypeChanged ->
                _state.value = state.copy(
                    type = event.value.trimStart()
                )
            is CreateActivityEvent.RepeatActivityChanged ->
                _state.value = state.copy(
                    repeatActivity = !state.repeatActivity
                )
            is CreateActivityEvent.AddDayOfWeekToRepeat ->
                _state.value = state.copy(
                    repeatOnDaysOfWeek = state.repeatOnDaysOfWeek + event.value
                )
            is CreateActivityEvent.StartTimeChanged ->
                _state.value = state.copy(
                    startTime = event.value
                )
            is CreateActivityEvent.RemoveDayOfWeekToRepeat ->
                _state.value = state.copy(
                    repeatOnDaysOfWeek = state.repeatOnDaysOfWeek - event.value
                )
            is CreateActivityEvent.StartDateChanged ->
                _state.value = state.copy(
                    startDate = event.value
                )
            is CreateActivityEvent.SaveActivity -> {
                viewModelScope.launch {
                    if (!state.isInputValid) {
                        _eventFlow.emit(UiEvent.ShowError)
                        return@launch
                    }
                    repo.saveActivity(state.toActivity(navArgs.activityId))
                    _eventFlow.emit(UiEvent.ActivitySaved)
                }
            }
        }
    }

    sealed class UiEvent {
        data object ShowError : UiEvent()
        data object ActivitySaved : UiEvent()
    }
}
