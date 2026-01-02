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
import pt.ipca.lojasocial.presentation.models.DeliveryUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class EntregasViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val beneficiaryRepository: BeneficiaryRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _allDeliveries = MutableStateFlow<List<DeliveryUiModel>>(emptyList())

    val deliveries: StateFlow<List<DeliveryUiModel>> =
        combine(_allDeliveries, _searchQuery, _selectedFilter) { deliveries, query, filter ->
            val filteredList = if (filter == "All") {
                deliveries
            } else {
                val statusToFilter = when (filter) {
                    "Agendada" -> DeliveryStatus.SCHEDULED
                    "Entregue" -> DeliveryStatus.DELIVERED
                    "Cancelada" -> DeliveryStatus.CANCELLED
                    else -> null
                }
                deliveries.filter { it.delivery.status == statusToFilter }
            }

            if (query.isBlank()) {
                filteredList
            } else {
                val lowerCaseQuery = query.lowercase(Locale.getDefault())
                filteredList.filter { uiModel ->
                    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(uiModel.delivery.scheduledDate))
                    uiModel.delivery.id.contains(lowerCaseQuery, ignoreCase = true) ||
                            uiModel.beneficiaryName.contains(lowerCaseQuery, ignoreCase = true) ||
                            formattedDate.contains(lowerCaseQuery, ignoreCase = true) ||
                            uiModel.delivery.createdBy.contains(lowerCaseQuery, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        loadDeliveries()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    private fun loadDeliveries() {
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

                val rawDeliveries = when (currentUser.role) {
                    UserRole.STAFF -> deliveryRepository.getDeliveries()
                    UserRole.BENEFICIARY -> deliveryRepository.getDeliveriesByBeneficiary(currentUser.id)
                }

                val uiModels = rawDeliveries.map { delivery ->
                    val beneficiary = beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                    DeliveryUiModel(
                        delivery = delivery,
                        beneficiaryName = beneficiary?.name ?: "Beneficiário Desconhecido"
                    )
                }
                _allDeliveries.value = uiModels
            } catch (e: Exception) {
                _error.value = "Falha ao carregar as entregas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}