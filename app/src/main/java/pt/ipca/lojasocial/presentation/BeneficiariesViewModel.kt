package pt.ipca.lojasocial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.use_cases.GetBeneficiariesUseCase
import javax.inject.Inject

@HiltViewModel
class BeneficiariesViewModel @Inject constructor(
    private val getBeneficiariesUseCase: GetBeneficiariesUseCase
) : ViewModel() {

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Lista vinda da Base de Dados (Fonte da verdade)
    private val _beneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())

    // Estados dos Filtros
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedYear = MutableStateFlow("2024-2025")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _selectedStatus = MutableStateFlow("")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    init {
        loadBeneficiaries()
    }

    fun loadBeneficiaries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Alteração Principal: Chamamos o UseCase
                val result = getBeneficiariesUseCase()
                _beneficiaries.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar dados: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Lógica de Filtragem (Mantida no ViewModel pois é lógica de apresentação/UI)
    fun getFilteredList(): List<Beneficiary> {
        val query = _searchQuery.value.lowercase()
        val year = _selectedYear.value
        val statusFilter = _selectedStatus.value

        return _beneficiaries.value.filter { ben ->
            // 1. Filtro de Texto
            val matchesQuery = ben.name.lowercase().contains(query) ||
                    ben.id.lowercase().contains(query)

            // 2. Filtro de Ano
            val matchesYear = year.isEmpty() || ben.schoolYearId == year

            // 3. Filtro de Status
            val matchesStatus = statusFilter.isEmpty() ||
                    ben.status.name.equals(statusFilter, ignoreCase = true)

            matchesQuery && matchesYear && matchesStatus
        }
    }

    // --- Setters para a UI ---
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onYearSelected(year: String) {
        _selectedYear.value = year
    }

    fun onStatusSelected(status: String) {
        _selectedStatus.value = status
    }
}