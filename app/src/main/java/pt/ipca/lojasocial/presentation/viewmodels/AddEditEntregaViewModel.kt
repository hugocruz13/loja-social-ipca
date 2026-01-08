package pt.ipca.lojasocial.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.EmailRequest
import pt.ipca.lojasocial.domain.models.NotificationRequest
import pt.ipca.lojasocial.domain.models.NotificationType
import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import pt.ipca.lojasocial.domain.repository.StockRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import pt.ipca.lojasocial.presentation.models.AddEditEntregaUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

private const val TAG = "AddEditEntregaVM"

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

    private fun loadAvailableProductsAndStock() {
        viewModelScope.launch {
            try {
                val stockItems = stockRepository.getStockItems().filter { it.quantity > 0 }
                val stockLimits = stockItems.groupBy { it.productId }
                    .mapValues { (_, items) -> items.sumOf { it.quantity } }

                val availableProductIds = stockItems.map { it.productId }.toSet()

                productRepository.getProducts().collect { allProductsList ->
                    val filteredProducts = allProductsList.filter { it.id in availableProductIds }
                    _uiState.update {
                        it.copy(
                            availableStockItems = stockItems,
                            availableProducts = filteredProducts,
                            productStockLimits = stockLimits
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading stock/products", e)
            }
        }
    }

    private fun checkUserRoleAndSetup() {
        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser?.role == UserRole.BENEFICIARY) {
                    val beneficiary = beneficiaryRepository.getBeneficiaryById(currentUser.id)
                    beneficiary?.let { b ->
                        _uiState.update {
                            it.copy(
                                selectedBeneficiary = b,
                                beneficiaryQuery = b.name
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user role", e)
            }
        }
    }

    fun loadDelivery(deliveryId: String) {
        viewModelScope.launch {
            try {
                val delivery = deliveryRepository.getDeliveryById(deliveryId).firstOrNull()
                if (delivery != null) {
                    val beneficiary =
                        beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                    val cal = Calendar.getInstance().apply { timeInMillis = delivery.scheduledDate }
                    val dateStr = dateFormat.format(cal.time)

                    _uiState.update {
                        it.copy(
                            deliveryId = deliveryId,
                            selectedBeneficiary = beneficiary,
                            beneficiaryQuery = beneficiary?.name ?: "",
                            date = dateStr,
                            time = timeFormat.format(cal.time),
                            observations = delivery.observations ?: "",
                            selectedProducts = delivery.items,
                            isImmediateDelivery = isDateToday(dateStr)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading delivery", e)
            }
        }
    }

    // --- INTERAÇÕES ---

    private fun validateForm() {
        _uiState.update { state ->
            val dateErr = if (state.date.isBlank()) "Selecione uma data" else null
            val prodErr =
                if (state.selectedProducts.isEmpty()) "Adicione pelo menos um produto" else null
            val beneErr =
                if (state.selectedBeneficiary == null) "Selecione um beneficiário" else null

            state.copy(
                dateError = dateErr,
                productsError = prodErr,
                beneficiaryError = beneErr,
                isFormValid = dateErr == null && prodErr == null && beneErr == null
            )
        }
    }
    
    fun onBeneficiaryQueryChange(query: String) {
        _uiState.update { it.copy(beneficiaryQuery = query) }
        if (query.length > 2) {
            viewModelScope.launch {
                val beneficiaries = beneficiaryRepository.getBeneficiaries()
                    .filter { it.name.contains(query, ignoreCase = true) }
                _uiState.update { it.copy(searchedBeneficiaries = beneficiaries) }
            }
        }
        validateForm()
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
        _uiState.update {
            it.copy(
                date = date,
                isImmediateDelivery = isDateToday(date),
                dateError = null
            )
        }
        validateForm()
    }

    fun onTimeChange(time: String) {
        _uiState.update { it.copy(time = time) }
    }

    fun onRepetitionChange(rep: String) {
        _uiState.update { it.copy(repetition = rep) }
    }

    fun onObservationsChange(obs: String) {
        _uiState.update { it.copy(observations = obs) }
    }

    fun onProductQuantityChange(productId: String, quantity: Int) {
        val updated = _uiState.value.selectedProducts.toMutableMap()
        if (quantity > 0) updated[productId] = quantity else updated.remove(productId)

        _uiState.update {
            it.copy(
                selectedProducts = updated,
                productsError = if (updated.isEmpty()) "Adicione pelo menos um produto" else null
            )
        }
        validateForm()
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

    private fun isDateToday(dateString: String): Boolean {
        val today = dateFormat.format(Date())
        return dateString == today
    }

    // --- SALVAGUARDA E LÓGICA DE NEGÓCIO ---

    fun saveDelivery() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.selectedBeneficiary == null || state.selectedProducts.isEmpty() || state.date.isBlank() || state.time.isBlank()) return@launch

            _uiState.update { it.copy(isSaving = true) }

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
        user: User
    ) {
        deliveryRepository.updateDeliveryDate(id, cal.timeInMillis)
        deliveryRepository.updateDeliveryObservations(id, state.observations)
        deliveryRepository.updateDeliveryItems(id, state.selectedProducts)
        if (user.role != UserRole.BENEFICIARY) sendNotification(
            state.selectedBeneficiary!!,
            cal,
            true
        )
    }

    private suspend fun handleCreateDelivery(
        state: AddEditEntregaUiState,
        cal: Calendar,
        user: User
    ) {
        val isStaff = user.role != UserRole.BENEFICIARY
        val isImmediate = isStaff && state.isImmediateDelivery

        // Status inicial: Se for Staff e for hoje -> ENTREGUE. Se for Beneficiário -> ANÁLISE. Outros -> AGENDADA.
        val firstStatus = when {
            user.role == UserRole.BENEFICIARY -> DeliveryStatus.UNDER_ANALYSIS
            isImmediate -> DeliveryStatus.DELIVERED
            else -> DeliveryStatus.SCHEDULED
        }

        val baseDelivery = Delivery(
            id = "",
            beneficiaryId = state.selectedBeneficiary!!.id,
            date = System.currentTimeMillis(),
            scheduledDate = 0,
            status = firstStatus,
            items = state.selectedProducts,
            observations = state.observations,
            createdBy = user.id
        )

        // Gerar a lista (Primeira entrega + Recorrências)
        val deliveries = generateRecurringDeliveries(baseDelivery, cal, state.repetition, user.role)

        deliveries.forEach { deliveryRepository.addDelivery(it) }

        // Se foi uma entrega imediata (Staff a entregar hoje), abate o stock do primeiro lote
        if (isImmediate) {
            deductStock(state.selectedProducts)
        }

        if (isStaff) sendNotification(state.selectedBeneficiary, cal, false)
    }

    private suspend fun generateRecurringDeliveries(
        base: Delivery,
        startCal: Calendar,
        repetition: String,
        role: UserRole
    ): List<Delivery> {
        val list = mutableListOf<Delivery>()

        // Adiciona a primeira
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
        val recurrentStatus =
            if (role == UserRole.BENEFICIARY) DeliveryStatus.UNDER_ANALYSIS else DeliveryStatus.SCHEDULED

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
                    scheduledDate = iterCal.timeInMillis,
                    status = recurrentStatus // Recorrências nunca nascem como DELIVERED
                )
            )
        }
        return list
    }

    private suspend fun deductStock(items: Map<String, Int>) {
        val allStock = stockRepository.getStockItems()
        items.forEach { (productId, qtyToDeduct) ->
            var remaining = qtyToDeduct
            val relevantStock = allStock.filter { it.productId == productId && it.quantity > 0 }
                .sortedBy { it.expiryDate }

            for (stock in relevantStock) {
                if (remaining <= 0) break
                val take = kotlin.math.min(remaining, stock.quantity)
                stockRepository.updateStockQuantity(stock.id, stock.quantity - take)
                remaining -= take
            }
        }
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
                    beneficiary.id,
                    title,
                    msg,
                    NotificationType.INFO,
                    mapOf("screen" to "entregas")
                )
            )
            communicationRepository.sendEmail(
                EmailRequest(
                    beneficiary.email,
                    "Loja Social - $title",
                    "<p>$msg</p>",
                    true,
                    "Loja Social IPCA"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Notification error", e)
        }
    }
}