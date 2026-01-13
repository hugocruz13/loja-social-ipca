package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import pt.ipca.lojasocial.domain.use_cases.delivery.GetPendingDeliveriesCountUseCase
import pt.ipca.lojasocial.presentation.models.DeliveryUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EntregasViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val getPendingDeliveriesCountUseCase: GetPendingDeliveriesCountUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Agendada")
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _allDeliveries = MutableStateFlow<List<DeliveryUiModel>>(emptyList())

    val allDeliveriesForDashboard: StateFlow<List<DeliveryUiModel>> = _allDeliveries.asStateFlow()
    private val _pendingCount = MutableStateFlow(0)
    val pendingCount = _pendingCount.asStateFlow()

    // Lógica de Filtro
    val deliveries: StateFlow<List<DeliveryUiModel>> =
        combine(_allDeliveries, _searchQuery, _selectedFilter) { deliveries, query, filter ->

            // 1. Filtrar por Estado
            val filteredByStatus = if (filter == "All") {
                deliveries
            } else {
                val statusToFilter = when (filter) {
                    "Agendada" -> DeliveryStatus.SCHEDULED
                    "Entregue" -> DeliveryStatus.DELIVERED
                    "Cancelada" -> DeliveryStatus.CANCELLED
                    else -> null
                }
                if (statusToFilter != null) {
                    deliveries.filter { it.delivery.status == statusToFilter }
                } else deliveries
            }

            // 2. Filtrar por Texto
            if (query.isBlank()) {
                filteredByStatus
            } else {
                val lowerCaseQuery = query.lowercase(Locale.getDefault())
                filteredByStatus.filter { uiModel ->
                    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(uiModel.delivery.scheduledDate))

                    uiModel.delivery.id.contains(lowerCaseQuery, ignoreCase = true) ||
                            uiModel.beneficiaryName.contains(lowerCaseQuery, ignoreCase = true) ||
                            formattedDate.contains(lowerCaseQuery, ignoreCase = true) ||
                            uiModel.delivery.createdBy.contains(lowerCaseQuery, ignoreCase = true)
                }
            }
        }.stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        loadDeliveries()
        loadPendingCount()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadPendingCount() {
        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()

                if (currentUser != null) {
                    val count = getPendingDeliveriesCountUseCase(currentUser.role, currentUser.id)
                    _pendingCount.value = count
                }
            } catch (e: Exception) {
                _pendingCount.value = 0
            }
        }
    }

    // Tornada pública para permitir refresh manual
    fun loadDeliveries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser == null) {
                    _error.value = "Utilizador não autenticado."
                    _isLoading.value = false
                    return@launch
                }

                // Decide qual o Flow a escutar
                val flowToCollect = when (currentUser.role) {
                    UserRole.STAFF -> deliveryRepository.getDeliveries()
                    UserRole.BENEFICIARY -> deliveryRepository.getDeliveriesByBeneficiary(
                        currentUser.id
                    )
                }

                // "Ligar o rádio"
                flowToCollect.collect { domainDeliveries ->

                    // Ordenação: Data mais próxima primeiro
                    val sortedDeliveries = domainDeliveries.sortedBy { it.scheduledDate }

                    // Mapeamento para UI Model (Buscar nomes dos beneficiários)
                    val uiModels = sortedDeliveries.map { delivery ->

                        val beneficiary =
                            beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)

                        DeliveryUiModel(
                            delivery = delivery,
                            beneficiaryName = beneficiary?.name ?: "A carregar..."
                        )
                    }

                    _allDeliveries.value = uiModels
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                _error.value = "Falha ao ligar stream de entregas: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}