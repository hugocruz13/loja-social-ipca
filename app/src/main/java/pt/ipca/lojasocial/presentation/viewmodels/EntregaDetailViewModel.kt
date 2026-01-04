package pt.ipca.lojasocial.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.NotificationRequest
import pt.ipca.lojasocial.domain.models.NotificationType
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.use_cases.delivery.ConfirmDeliveryUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DeliveryProductDetailUi(
    val name: String,
    val quantity: Int,
    val photoUrl: String?
)

data class EntregaDetailUiState(
    val isLoading: Boolean = true,
    val beneficiaryName: String = "",
    val beneficiaryIdDisplay: String = "", // Ex: CC ou ID interno
    val date: String = "",
    val time: String = "",
    val status: StatusType = StatusType.PENDENTE,
    val items: List<DeliveryProductDetailUi> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class EntregaDetailViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val productRepository: ProductRepository,
    private val communicationRepository: CommunicationRepository,
    private val confirmDeliveryUseCase: ConfirmDeliveryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntregaDetailUiState())
    val uiState: StateFlow<EntregaDetailUiState> = _uiState.asStateFlow()

    fun loadDelivery(deliveryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val delivery = deliveryRepository.getDeliveryById(deliveryId)
                if (delivery == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Entrega não encontrada") }
                    return@launch
                }

                val beneficiary = beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                
                val productDetails = delivery.items.map { (productId, quantity) ->
                    val product = productRepository.getProductById(productId)
                    DeliveryProductDetailUi(
                        name = product?.name ?: "Produto Desconhecido",
                        quantity = quantity,
                        photoUrl = product?.photoUrl
                    )
                }

                val dateObj = Date(delivery.scheduledDate)
                val dateFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "PT"))
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val uiStatus = when(delivery.status) {
                    DeliveryStatus.DELIVERED -> StatusType.ENTREGUE
                    DeliveryStatus.SCHEDULED -> StatusType.PENDENTE 
                    DeliveryStatus.CANCELLED -> StatusType.NOT_ENTREGUE
                    DeliveryStatus.REJECTED -> StatusType.NOT_ENTREGUE
                    DeliveryStatus.UNDER_ANALYSIS -> StatusType.ANALISE
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        beneficiaryName = beneficiary?.name ?: "Desconhecido",
                        beneficiaryIdDisplay = "NIF/CC: ${beneficiary?.id ?: "N/A"}",
                        date = dateFormat.format(dateObj),
                        time = timeFormat.format(dateObj),
                        status = uiStatus,
                        items = productDetails
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateStatus(deliveryId: String, delivered: Boolean) {
        viewModelScope.launch {
            try {
                if (delivered) {
                    confirmDeliveryUseCase(deliveryId)
                    // Opcional: Notificar entrega bem sucedida
                } else {
                    // Cancelar Entrega
                    deliveryRepository.updateDeliveryStatus(deliveryId, DeliveryStatus.CANCELLED)
                    
                    // Notificar Cancelamento
                    val delivery = deliveryRepository.getDeliveryById(deliveryId)
                    if (delivery != null) {
                        val notif = NotificationRequest(
                            userId = delivery.beneficiaryId,
                            title = "Entrega Cancelada",
                            message = "A sua entrega agendada foi cancelada. Contacte os serviços para mais informações.",
                            type = NotificationType.WARNING
                        )
                        communicationRepository.sendNotification(notif)
                    }
                }
                loadDelivery(deliveryId)
            } catch (e: Exception) {
                Log.e("EntregaDetailVM", "Erro ao atualizar estado: ${e.message}", e)
            }
        }
    }

    fun approveDelivery(deliveryId: String) {
        viewModelScope.launch {
            try {
                // 1. Update status to SCHEDULED
                deliveryRepository.updateDeliveryStatus(deliveryId, DeliveryStatus.SCHEDULED)

                // 2. Notify Beneficiary
                val delivery = deliveryRepository.getDeliveryById(deliveryId)
                if (delivery != null) {
                    val notif = NotificationRequest(
                        userId = delivery.beneficiaryId,
                        title = "Pedido Aprovado",
                        message = "O seu pedido de entrega foi aprovado e agendado.",
                        type = NotificationType.SUCCESS
                    )
                    communicationRepository.sendNotification(notif)
                }

                // 3. Reload
                loadDelivery(deliveryId)
            } catch (e: Exception) {
                Log.e("EntregaDetailVM", "Error approving delivery", e)
            }
        }
    }

    fun rejectDelivery(deliveryId: String) {
        viewModelScope.launch {
            try {
                // 1. Update status to REJECTED
                deliveryRepository.updateDeliveryStatus(deliveryId, DeliveryStatus.REJECTED)

                // 2. Notify Beneficiary
                val delivery = deliveryRepository.getDeliveryById(deliveryId)
                if (delivery != null) {
                    val notif = NotificationRequest(
                        userId = delivery.beneficiaryId,
                        title = "Pedido Rejeitado",
                        message = "O seu pedido de entrega não pôde ser aceite neste momento.",
                        type = NotificationType.ERROR
                    )
                    communicationRepository.sendNotification(notif)
                }

                // 3. Reload
                loadDelivery(deliveryId)
            } catch (e: Exception) {
                Log.e("EntregaDetailVM", "Error rejecting delivery", e)
            }
        }
    }
}
