package pt.ipca.lojasocial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.UserRole // Importante: Importar o Enum
import pt.ipca.lojasocial.domain.use_cases.beneficiary.AddBeneficiaryUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.GetBeneficiariesUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.UpdateBeneficiaryUseCase // <-- IMPORT NOVO
import javax.inject.Inject

@HiltViewModel
class BeneficiariesViewModel @Inject constructor(
    private val getBeneficiariesUseCase: GetBeneficiariesUseCase,
    private val addBeneficiaryUseCase: AddBeneficiaryUseCase,
    // --- NOVO: Injetamos o UseCase de atualização do Passo 1 ---
    private val updateBeneficiaryUseCase: UpdateBeneficiaryUseCase
) : ViewModel() {

    // --- ESTADOS DA UI (Mantive igual) ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Sucesso da operação (útil para fechar o ecrã/dialog depois de guardar)
    private val _isUpdateSuccess = MutableStateFlow(false)
    val isUpdateSuccess: StateFlow<Boolean> = _isUpdateSuccess.asStateFlow()

    // --- ESTADOS DE DADOS (Mantive igual) ---
    private val _beneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())

    // --- FILTROS (Mantive igual) ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedYear = MutableStateFlow("2024_2025")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _selectedStatus = MutableStateFlow("")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    // Lógica da Lista Filtrada (Mantive igual)
    val filteredBeneficiaries: StateFlow<List<Beneficiary>> = combine(
        _beneficiaries, _searchQuery, _selectedYear, _selectedStatus
    ) { list, query, year, status ->
        list.filter { ben ->
            val matchesQuery = query.isEmpty() || ben.name.contains(query, ignoreCase = true) || ben.email.contains(query, ignoreCase = true)
            val matchesYear = year.isEmpty() || ben.schoolYearId == year
            val matchesStatus = status.isEmpty() || ben.status.name.equals(status, ignoreCase = true)
            matchesQuery && matchesYear && matchesStatus
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    init {
        loadBeneficiaries()
    }

    // --- FUNÇÕES DE CARREGAMENTO (Mantive igual) ---
    fun loadBeneficiaries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getBeneficiariesUseCase()
                _beneficiaries.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addBeneficiaryUseCase(beneficiary)
                loadBeneficiaries()
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao adicionar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- NOVA FUNÇÃO: ATUALIZAR PERFIL (PASSO 2) ---
    /**
     * Esta função conecta a UI ao UseCase que criaste.
     * Ela recebe os dados e deixa o UseCase decidir o que pode ser guardado com base na Role.
     */
    fun updateBeneficiaryProfile(
        role: UserRole,          // Quem está a tentar editar? (STAFF ou BENEFICIARY)
        original: Beneficiary,   // O beneficiário como está na DB antes de editar
        modified: Beneficiary    // Os novos dados vindos do formulário
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isUpdateSuccess.value = false // Reset

            // Chama o UseCase do Passo 1
            val result = updateBeneficiaryUseCase(
                role = role,
                original = original,
                modified = modified
            )

            result.onSuccess {
                // Se correu bem, recarrega a lista para mostrar os dados novos
                loadBeneficiaries()
                _isUpdateSuccess.value = true
            }.onFailure { e ->
                _errorMessage.value = "Erro ao atualizar: ${e.message}"
            }

            _isLoading.value = false
        }
    }

    // Função auxiliar para limpar o estado de sucesso (ex: depois de navegar para trás)
    fun resetUpdateStatus() {
        _isUpdateSuccess.value = false
        _errorMessage.value = null
    }

    // --- EVENTOS DA UI (Mantive igual) ---
    fun onSearchQueryChange(newQuery: String) { _searchQuery.value = newQuery }
    fun onYearSelected(year: String) { _selectedYear.value = year }
    fun onStatusSelected(status: String) { _selectedStatus.value = status }
    fun clearError() { _errorMessage.value = null }
}