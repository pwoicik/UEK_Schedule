package com.github.pwoicik.uekschedule.features.search.presentation.screens.allGroups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pwoicik.uekschedule.common.domain.ScheduleRepository
import com.github.pwoicik.uekschedule.domain.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class AllGroupsViewModel @Inject constructor(
    private val repo: ScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AllGroupsState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>(replay = 1)
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchGroups()
    }

    private var fetchJob: Job? = null
    private fun fetchGroups() {
        if (fetchJob?.isActive == true) return
        fetchJob = viewModelScope.launch {
            _state.update { state ->
                state.copy(isLoading = true)
            }
            _eventFlow.emit(UiEvent.HideSnackbar)

            val result = repo.getAllGroups().onFailure {
                _eventFlow.emit(
                    UiEvent.ShowErrorSnackbar(
                        AllGroupsEvent.RetryGroupsFetch
                    )
                )
            }
            _state.update { state ->
                val groups = result.getOrDefault(state.groups ?: emptyList())
                state.copy(
                    didTry = true,
                    isLoading = false,
                    groups = groups,
                    filteredGroups = groups.filter {
                        it.name.contains(state.searchValue.text, ignoreCase = true)
                    }
                )
            }
        }
    }

    private var filterJob: Job? = null
    fun emit(event: AllGroupsEvent) {
        when (event) {
            is AllGroupsEvent.SearchTextChanged -> {
                _state.update { state ->
                    state.copy(
                        searchValue = event.newValue
                    )
                }

                filterJob?.cancel()
                filterJob = viewModelScope.launch {
                    delay(0.5.seconds)
                    Timber.d("xyz")
                    _state.update { state ->
                        state.copy(
                            filteredGroups = state.groups?.filter {
                                it.name.contains(
                                    state.searchValue.text,
                                    ignoreCase = true
                                )
                            } ?: emptyList()
                        )
                    }
                }
            }
            is AllGroupsEvent.GroupSaveButtonClicked -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.HideSnackbar)

                    _state.update { state ->
                        state.copy(isSaving = true)
                    }
                    _eventFlow.emit(UiEvent.ShowSavingGroupSnackbar(event.group))

                    val result = repo.saveGroup(event.group)
                    _state.update { state ->
                        state.copy(isSaving = false)
                    }
                    _eventFlow.emit(
                        if (result.isSuccess) {
                            UiEvent.ShowSavedGroupSnackbar(event.group)
                        } else {
                            UiEvent.ShowErrorSnackbar(
                                AllGroupsEvent.GroupSaveButtonClicked(event.group)
                            )
                        }
                    )
                }
            }
            AllGroupsEvent.RetryGroupsFetch -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.HideSnackbar)
                    fetchGroups()
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowErrorSnackbar(val eventToRepeat: AllGroupsEvent) : UiEvent()
        object HideSnackbar : UiEvent()
        data class ShowSavingGroupSnackbar(val group: Group) : UiEvent()
        data class ShowSavedGroupSnackbar(val group: Group) : UiEvent()
    }
}
