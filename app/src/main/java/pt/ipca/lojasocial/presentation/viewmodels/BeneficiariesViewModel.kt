package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.use_cases.beneficiary.AddBeneficiaryUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.GetBeneficiariesUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.UpdateBeneficiaryUseCase
import javax.inject.Inject

@HiltViewModel
class BeneficiariesViewModel @Inject constructor(
    private val getBeneficiariesUseCase: GetBeneficiariesUseCase,
    private val addBeneficiaryUseCase: AddBeneficiaryUseCase,
    private val updateBeneficiaryUseCase: UpdateBeneficiaryUseCase,
    private val requestRepository: RequestRepository
) : ViewModel() {

    // --- ESTADOS DA UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isUpdateSuccess = MutableStateFlow(false)
    val isUpdateSuccess: StateFlow<Boolean> = _isUpdateSuccess.asStateFlow()

    // --- ESTADOS DE DADOS ---
    private val _beneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())

    // ADICIONADO: Estado para o beneficiário selecionado (Perfil ou Detalhe)
    private val _selectedBeneficiary = MutableStateFlow<Beneficiary?>(null)
    val selectedBeneficiary: StateFlow<Beneficiary?> = _selectedBeneficiary.asStateFlow()

    // ADICIONADO: Estado para o requerimento do beneficiário selecionado
    private val _selectedRequest = MutableStateFlow<Request?>(null)
    val selectedRequest: StateFlow<Request?> = _selectedRequest.asStateFlow()

    // --- FILTROS ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedYear = MutableStateFlow("2024_2025")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _selectedStatus = MutableStateFlow("")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    val filteredBeneficiaries: StateFlow<List<Beneficiary>> = combine(
        _beneficiaries, _searchQuery, _selectedStatus
    ) { list, query, status ->
        list.filter { ben ->
            val matchesQuery = query.isEmpty() || ben.name.contains(query, ignoreCase = true) ||
                    ben.email.contains(query, ignoreCase = true)

            val matchesStatus =
                status.isEmpty() || ben.status.name.equals(status, ignoreCase = true)

            matchesQuery && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadBeneficiaries()
    }

    // --- FUNÇÕES DE CARREGAMENTO ---
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

    // ADICIONADO: Função crucial para o Perfil e Detalhes
    fun loadBeneficiaryDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Tenta encontrar na lista local
                var ben = _beneficiaries.value.find { it.id == id }

                // 2. Se a lista estiver vazia (ex: reload da página), forçamos um fetch
                if (ben == null) {
                    val freshList = getBeneficiariesUseCase()
                    _beneficiaries.value = freshList
                    ben = freshList.find { it.id == id }
                }

                // Atualiza o estado que o ProfileScreen está a escutar
                _selectedBeneficiary.value = ben

                // 3. Carrega o Requerimento associado (em tempo real)
                requestRepository.getRequestsByBeneficiary(id).collectLatest { requests ->
                    // Pega no mais recente (ordenado por data se necessário)
                    _selectedRequest.value = requests.maxByOrNull { it.submissionDate }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar detalhe: ${e.message}"
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

    // --- ATUALIZAR PERFIL ---
    fun updateBeneficiaryProfile(
        role: UserRole,
        original: Beneficiary,
        modified: Beneficiary
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isUpdateSuccess.value = false

            // Chama o UseCase do Passo 1
            val result = updateBeneficiaryUseCase(
                role = role,
                original = original,
                modified = modified
            )

            result.onSuccess {
                // Atualiza a lista geral
                loadBeneficiaries()
                // Atualiza também o selecionado para refletir a mudança no ecrã de Perfil imediatamente
                _selectedBeneficiary.value = modified
                _isUpdateSuccess.value = true
            }.onFailure { e ->
                _errorMessage.value = "Erro ao atualizar: ${e.message}"
            }

            _isLoading.value = false
        }
    }

    fun resetUpdateStatus() {
        _isUpdateSuccess.value = false
        _errorMessage.value = null
    }

    // --- EVENTOS DA UI ---
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