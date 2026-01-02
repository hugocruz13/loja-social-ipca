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
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.use_cases.request.GetRequestsWithDetailsUseCase
import pt.ipca.lojasocial.presentation.models.RequestUiModel
import javax.inject.Inject

@HiltViewModel
class RequerimentosViewModel @Inject constructor(
    private val getRequestsWithDetailsUseCase: GetRequestsWithDetailsUseCase
) : ViewModel() {

    private val _allRequests = MutableStateFlow<List<RequestUiModel>>(emptyList())

    // Estado da Pesquisa (Texto)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Estado do Filtro (Enum StatusType ou null se estiver limpo)
    private val _selectedStatusFilter = MutableStateFlow<StatusType?>(null)
    val selectedStatusFilter: StateFlow<StatusType?> = _selectedStatusFilter

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- LÓGICA DE FILTRAGEM COMBINADA ---
    val filteredRequests: StateFlow<List<RequestUiModel>> = combine(
        _allRequests,
        _searchQuery,
        _selectedStatusFilter
    ) { list, query, statusFilter ->
        var result = list

        // 1. Filtrar por Estado (Dropdown)
        if (statusFilter != null) {
            result = result.filter { it.status == statusFilter }
        }

        // 2. Filtrar por Texto (Barra de Pesquisa)
        if (query.isNotBlank()) {
            result = result.filter {
                it.beneficiaryName.contains(query, ignoreCase = true)
            }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Carrega dados iniciais
    fun loadRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aqui defines o ano letivo (pode ser dinâmico futuramente)
                val result = getRequestsWithDetailsUseCase("2024_2025")
                _allRequests.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // Chamado pelo Dropdown para mudar o estado
    fun onFilterChange(status: StatusType?) {
        _selectedStatusFilter.value = status
    }
}