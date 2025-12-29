package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.use_cases.request.GetRequestsWithDetailsUseCase
import pt.ipca.lojasocial.presentation.models.RequestUiModel
import javax.inject.Inject

@HiltViewModel
class RequerimentosViewModel @Inject constructor(
    private val getRequestsWithDetailsUseCase: GetRequestsWithDetailsUseCase
) : ViewModel() {

    private val _allRequests = MutableStateFlow<List<RequestUiModel>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Filtra apenas pelo NOME
    val filteredRequests: StateFlow<List<RequestUiModel>> = combine(_allRequests, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.beneficiaryName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadRequests()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Podes tornar este ano dinâmico futuramente
                val result = getRequestsWithDetailsUseCase("2024_2025")
                _allRequests.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}