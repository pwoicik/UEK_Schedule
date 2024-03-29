package com.github.pwoicik.uekschedule.features.schedule.presentation.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pwoicik.uekschedule.common.domain.ScheduleRepository
import com.github.pwoicik.uekschedule.common.util.timeFlow
import com.github.pwoicik.uekschedule.features.schedule.presentation.components.filterEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class ScheduleViewModel @Inject constructor(
    private val repo: ScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>(replay = 1)
    val eventFlow = _eventFlow.asSharedFlow()

    val timeFlow = timeFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalDateTime.now())

    init {
        combine(
            repo.getSavedGroupsCount(),
            repo.getAllScheduleEntries()
        ) { count, entries ->
            _state.update { state ->
                val hasGroups = count > 0
                state.copy(
                    hasSavedGroups = hasGroups,
                    entries = entries,
                    filteredEntries = entries
                        .filterEntries(state.searchValue.text)
                )
            }
        }
            .conflate()
            .launchIn(viewModelScope)

        refreshData()
    }

    private var refreshJob: Job? = null
    private fun refreshData() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            _eventFlow.emit(UiEvent.HideSnackbar)
            _state.update { state ->
                state.copy(isRefreshing = true)
            }

            repo.updateSchedules().onFailure {
                _eventFlow.emit(UiEvent.ShowErrorSnackbar)
            }
            _state.update { state ->
                state.copy(isRefreshing = false)
            }
        }
    }

    private var filterJob: Job? = null
    fun emit(event: ScheduleEvent) {
        when (event) {
            ScheduleEvent.FabClicked -> {
                viewModelScope.launch { _eventFlow.emit(UiEvent.ScrollToToday) }
            }
            is ScheduleEvent.SearchTextChanged -> {
                _state.update { state ->
                    state.copy(
                        searchValue = event.newValue.copy()
                    )
                }

                filterJob?.cancel()
                filterJob = viewModelScope.launch {
                    delay(0.5.seconds)
                    _state.update {
                        it.copy(
                            filteredEntries = it.entries
                                ?.filterEntries(it.searchValue.text)
                                ?: emptyMap()
                        )
                    }
                }
            }
            ScheduleEvent.RefreshButtonClicked -> {
                refreshData()
            }
        }
    }

    sealed class UiEvent {
        data object ShowErrorSnackbar : UiEvent()
        data object HideSnackbar : UiEvent()
        data object ScrollToToday : UiEvent()
    }
}
