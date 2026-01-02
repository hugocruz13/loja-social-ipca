package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import pt.ipca.lojasocial.domain.models.AppLog
import pt.ipca.lojasocial.domain.use_cases.log.GetLogsUseCase
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val getLogsUseCase: GetLogsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val logs: StateFlow<List<AppLog>> = getLogsUseCase()
        .onEach { _isLoading.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}