package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.use_cases.request.GetRequestsWithDetailsUseCase
import pt.ipca.lojasocial.presentation.models.RequestUiModel
import javax.inject.Inject

@HiltViewModel
class RequerimentosViewModel @Inject constructor(
    getRequestsWithDetailsUseCase: GetRequestsWithDetailsUseCase
) : ViewModel() {

    // 1. Estados de UI (Pesquisa e Filtros)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<StatusType?>(null)
    val selectedStatusFilter = _selectedStatusFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // 2. O FLUXO DE DADOS (Aqui está a correção)
    // Em vez de usares _allRequests, usas o UseCase diretamente aqui.
    private val requestsFlow = getRequestsWithDetailsUseCase("2024_2025")

    // 3. COMBINAR TUDO (DB + Filtros)
    val filteredRequests: StateFlow<List<RequestUiModel>> = combine(
        requestsFlow, // <--- O Flow entra aqui diretamente
        _searchQuery,
        _selectedStatusFilter
    ) { requests, query, statusFilter ->

        // Assim que recebemos dados, desligamos o loading
        _isLoading.value = false

        var result = requests

        // Aplicar Filtros
        if (statusFilter != null) {
            result = result.filter { it.status == statusFilter }
        }

        if (query.isNotBlank()) {
            result = result.filter {
                it.beneficiaryName.contains(query, ignoreCase = true)
            }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Funções de interação
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterChange(status: StatusType?) {
        _selectedStatusFilter.value = status
    }
}