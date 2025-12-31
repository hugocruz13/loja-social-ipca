package pt.ipca.lojasocial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.use_cases.beneficiary.AddBeneficiaryUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.GetBeneficiariesUseCase
import javax.inject.Inject

@HiltViewModel
class BeneficiariesViewModel @Inject constructor(
    private val getBeneficiariesUseCase: GetBeneficiariesUseCase,
    private val addBeneficiaryUseCase: AddBeneficiaryUseCase // Injetamos também o Add para poder criar
) : ViewModel() {

    // --- ESTADOS DA UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- ESTADOS DE DADOS ---
    // A lista "bruta" que vem da Base de Dados
    private val _beneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())

    // --- FILTROS ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedYear = MutableStateFlow("2024_2025") // Default igual ao ID criado no Firebase
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _selectedStatus = MutableStateFlow("")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    /**
     * LISTA FILTRADA (A MAGIA ACONTECE AQUI)
     * Em vez de uma função, usamos um Flow que combina os 4 estados.
     * Sempre que (_beneficiaries, _searchQuery, _selectedYear ou _selectedStatus) mudar,
     * esta lista atualiza-se sozinha.
     */
    val filteredBeneficiaries: StateFlow<List<Beneficiary>> = combine(
        _beneficiaries,
        _searchQuery,
        _selectedYear,
        _selectedStatus
    ) { list, query, year, status ->
        list.filter { ben ->
            // 1. Filtro de Texto (Nome ou Email)
            val matchesQuery = query.isEmpty() ||
                    ben.name.contains(query, ignoreCase = true) ||
                    ben.email.contains(query, ignoreCase = true)

            // 2. Filtro de Ano (Compara com o schoolYearId do Firebase)
            val matchesYear = year.isEmpty() || ben.schoolYearId == year

            // 3. Filtro de Status
            val matchesStatus = status.isEmpty() ||
                    ben.status.name.equals(status, ignoreCase = true)

            matchesQuery && matchesYear && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Inicializa a carga de dados
    init {
        loadBeneficiaries()
    }

    /**
     * Vai ao Firebase buscar a lista completa (Use Case Get).
     */
    fun loadBeneficiaries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Chama o Use Case que conecta ao RepositoryImpl -> Firebase
                val result = getBeneficiariesUseCase()
                _beneficiaries.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Adiciona um novo beneficiário (Use Case Add).
     * Útil para chamar quando clicas no botão "Salvar" de um formulário.
     */
    fun addBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addBeneficiaryUseCase(beneficiary)
                // Após adicionar com sucesso, recarregamos a lista para aparecer o novo
                loadBeneficiaries()
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao adicionar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- EVENTOS DA UI (Setters) ---

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onYearSelected(year: String) {
        _selectedYear.value = year
    }

    fun onStatusSelected(status: String) {
        _selectedStatus.value = status
    }

    fun clearError() {
        _errorMessage.value = null
    }
}