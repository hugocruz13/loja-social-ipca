package pt.ipca.lojasocial.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull // Importante!
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.EmailRequest
import pt.ipca.lojasocial.domain.models.NotificationRequest
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import pt.ipca.lojasocial.domain.repository.StockRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

private const val TAG = "AddEditEntregaVM"

data class AddEditEntregaUiState(
    val deliveryId: String? = null,
    val beneficiaryQuery: String = "",
    val searchedBeneficiaries: List<Beneficiary> = emptyList(),
    val selectedBeneficiary: Beneficiary? = null,
    val date: String = "",
    val time: String = "",
    val repetition: String = "Não repetir",
    val observations: String = "",
    val availableProducts: List<Product> = emptyList(),
    val availableStockItems: List<Stock> = emptyList(),
    val productStockLimits: Map<String, Int> = emptyMap(),
    val selectedProducts: Map<String, Int> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isProductPickerDialogVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,
    val isTimePickerDialogVisible: Boolean = false
)

@HiltViewModel
class AddEditEntregaViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val schoolYearRepository: SchoolYearRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val communicationRepository: CommunicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditEntregaUiState())
    val uiState: StateFlow<AddEditEntregaUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    init {
        loadAvailableProductsAndStock()
        checkUserRoleAndSetup()
    }

    // --- CARREGAMENTO DE DADOS ---

    private fun loadAvailableProductsAndStock() {
        viewModelScope.launch {
            try {
                // 1. Carregar Stock
                val stockItems = stockRepository.getStockItems().filter { it.quantity > 0 }

                // 2. Preparar limites
                val stockLimits = stockItems
                    .groupBy { it.productId }
                    .mapValues { (_, items) -> items.sumOf { it.quantity } }

                val availableProductIds = stockItems.map { it.productId }.toSet()

                // 3. Carregar Produtos (Realtime Flow -> Collect)
                productRepository.getProducts().collect { allProductsList ->
                    val filteredProducts = allProductsList.filter { product ->
                        product.id in availableProductIds
                    }

                    _uiState.update {
                        it.copy(
                            availableStockItems = stockItems,
                            availableProducts = filteredProducts,
                            productStockLimits = stockLimits
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading data", e)
            }
        }
    }

    private fun checkUserRoleAndSetup() {
        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser?.role == UserRole.BENEFICIARY) {
                    val beneficiary = beneficiaryRepository.getBeneficiaryById(currentUser.id)
                    if (beneficiary != null) {
                        _uiState.update {
                            it.copy(
                                selectedBeneficiary = beneficiary,
                                beneficiaryQuery = beneficiary.name
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user role", e)
            }
        }
    }

    // AQUI ESTAVA O ERRO
    fun loadDelivery(deliveryId: String) {
        viewModelScope.launch {
            try {
                // CORREÇÃO: Adicionado .firstOrNull() para tirar o objeto do Flow
                val delivery = deliveryRepository.getDeliveryById(deliveryId).firstOrNull()

                if (delivery != null) {
                    val beneficiary =
                        beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                    val dateObj = Date(delivery.scheduledDate)

                    _uiState.update {
                        it.copy(
                            deliveryId = deliveryId,
                            selectedBeneficiary = beneficiary,
                            beneficiaryQuery = beneficiary?.name ?: "",
                            date = dateFormat.format(dateObj),
                            time = timeFormat.format(dateObj),
                            repetition = "Não repetir",
                            observations = delivery.observations ?: "",
                            selectedProducts = delivery.items
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading delivery", e)
            }
        }
    }

    // --- INTERAÇÕES --- (Igual ao anterior)

    fun onBeneficiaryQueryChange(query: String) {
        _uiState.update { it.copy(beneficiaryQuery = query) }
        if (query.length > 2) {
            viewModelScope.launch {
                try {
                    val beneficiaries = beneficiaryRepository.getBeneficiaries()
                        .filter { it.name.contains(query, ignoreCase = true) }
                    _uiState.update { it.copy(searchedBeneficiaries = beneficiaries) }
                } catch (e: Exception) {
                    Log.e(TAG, "Error searching", e)
                }
            }
        } else {
            _uiState.update { it.copy(searchedBeneficiaries = emptyList()) }
        }
    }

    fun onBeneficiarySelected(beneficiary: Beneficiary) {
        _uiState.update {
            it.copy(
                selectedBeneficiary = beneficiary,
                beneficiaryQuery = beneficiary.name,
                searchedBeneficiaries = emptyList()
            )
        }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun onTimeChange(time: String) {
        _uiState.update { it.copy(time = time) }
    }

    fun onRepetitionChange(repetition: String) {
        _uiState.update { it.copy(repetition = repetition) }
    }

    fun onObservationsChange(observations: String) {
        _uiState.update { it.copy(observations = observations) }
    }

    fun onProductQuantityChange(productId: String, quantity: Int) {
        val updatedProducts = _uiState.value.selectedProducts.toMutableMap()
        if (quantity > 0) updatedProducts[productId] = quantity else updatedProducts.remove(
            productId
        )
        _uiState.update { it.copy(selectedProducts = updatedProducts) }
    }

    // --- DIALOGS ---
    fun showProductPickerDialog() {
        _uiState.update { it.copy(isProductPickerDialogVisible = true) }
    }

    fun hideProductPickerDialog() {
        _uiState.update { it.copy(isProductPickerDialogVisible = false) }
    }

    fun showDatePickerDialog() {
        _uiState.update { it.copy(isDatePickerDialogVisible = true) }
    }

    fun hideDatePickerDialog() {
        _uiState.update { it.copy(isDatePickerDialogVisible = false) }
    }

    fun showTimePickerDialog() {
        _uiState.update { it.copy(isTimePickerDialogVisible = true) }
    }

    fun hideTimePickerDialog() {
        _uiState.update { it.copy(isTimePickerDialogVisible = false) }
    }

    // --- SALVAR ---

    fun saveDelivery() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value

            if (state.selectedBeneficiary == null || state.selectedProducts.isEmpty() || state.date.isBlank() || state.time.isBlank()) {
                _uiState.update { it.copy(isSaving = false) }
                return@launch
            }

            val initialDate = try {
                dateTimeFormat.parse("${state.date} ${state.time}")
            } catch (e: Exception) {
                null
            }
            if (initialDate == null) {
                _uiState.update { it.copy(isSaving = false) }
                return@launch
            }

            val calendar = Calendar.getInstance().apply { time = initialDate }
            val currentUser = getCurrentUserUseCase() ?: return@launch

            try {
                if (state.deliveryId != null) {
                    handleUpdateDelivery(state.deliveryId, state, calendar, currentUser)
                } else {
                    handleCreateDelivery(state, calendar, currentUser)
                }
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save delivery", e)
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private suspend fun handleUpdateDelivery(
        id: String,
        state: AddEditEntregaUiState,
        cal: Calendar,
        user: pt.ipca.lojasocial.domain.models.User
    ) {
        deliveryRepository.updateDeliveryDate(id, cal.timeInMillis)
        deliveryRepository.updateDeliveryObservations(id, state.observations)
        deliveryRepository.updateDeliveryItems(id, state.selectedProducts)

        if (user.role != UserRole.BENEFICIARY && state.selectedBeneficiary != null) {
            sendNotification(state.selectedBeneficiary, cal, isUpdate = true)
        }
    }

    private suspend fun handleCreateDelivery(
        state: AddEditEntregaUiState,
        cal: Calendar,
        user: pt.ipca.lojasocial.domain.models.User
    ) {
        val initialStatus =
            if (user.role == UserRole.BENEFICIARY) DeliveryStatus.UNDER_ANALYSIS else DeliveryStatus.SCHEDULED

        val baseDelivery = Delivery(
            id = "",
            beneficiaryId = state.selectedBeneficiary!!.id,
            date = System.currentTimeMillis(),
            scheduledDate = 0,
            status = initialStatus,
            items = state.selectedProducts,
            observations = state.observations,
            createdBy = user.id
        )

        val deliveries = generateRecurringDeliveries(baseDelivery, cal, state.repetition)
        deliveries.forEach { deliveryRepository.addDelivery(it) }

        if (user.role != UserRole.BENEFICIARY) {
            sendNotification(state.selectedBeneficiary, cal, isUpdate = false)
        }
    }

    private suspend fun generateRecurringDeliveries(
        base: Delivery,
        startCal: Calendar,
        repetition: String
    ): List<Delivery> {
        val list = mutableListOf<Delivery>()
        list.add(
            base.copy(
                id = UUID.randomUUID().toString(),
                scheduledDate = startCal.timeInMillis
            )
        )

        if (repetition == "Não repetir") return list

        val allSchoolYears = schoolYearRepository.getSchoolYears().firstOrNull() ?: emptyList()
        val currentYear =
            allSchoolYears.find { System.currentTimeMillis() in it.startDate..it.endDate }
                ?: return list

        val iterCal = startCal.clone() as Calendar
        while (true) {
            when (repetition) {
                "Mensalmente" -> iterCal.add(Calendar.MONTH, 1)
                "Bimensal" -> iterCal.add(Calendar.MONTH, 2)
                "Semestral" -> iterCal.add(Calendar.MONTH, 6)
                else -> break
            }
            if (iterCal.timeInMillis > currentYear.endDate) break
            list.add(
                base.copy(
                    id = UUID.randomUUID().toString(),
                    scheduledDate = iterCal.timeInMillis
                )
            )
        }
        return list
    }

    private suspend fun sendNotification(
        beneficiary: Beneficiary,
        cal: Calendar,
        isUpdate: Boolean
    ) {
        val dateStr = dateTimeFormat.format(cal.time)
        val title = if (isUpdate) "Entrega Atualizada 📝" else "Nova Entrega Agendada! 📦"
        val msg =
            if (isUpdate) "A tua entrega foi alterada para: $dateStr" else "Tens uma recolha marcada para $dateStr."

        try {
            communicationRepository.sendNotification(
                NotificationRequest(
                    userId = beneficiary.id,
                    title = title,
                    message = msg,
                    data = mapOf("screen" to "entregas")
                )
            )
            communicationRepository.sendEmail(
                EmailRequest(
                    to = beneficiary.email,
                    subject = "Loja Social - $title",
                    body = "<p>Olá ${beneficiary.name},</p><p>$msg</p>",
                    isHtml = true,
                    senderName = "Loja Social IPCA"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification", e)
        }
    }
}