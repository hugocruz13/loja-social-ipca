package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
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
    private val productRepository: ProductRepository
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

                // Carregar Beneficiário
                val beneficiary = beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                
                // Carregar Detalhes dos Produtos (Nome e Imagem)
                val productDetails = delivery.items.map { (productId, quantity) ->
                    val product = productRepository.getProductById(productId)
                    DeliveryProductDetailUi(
                        name = product?.name ?: "Produto Desconhecido",
                        quantity = quantity,
                        photoUrl = product?.photoUrl
                    )
                }

                // Formatar Data e Hora
                val dateObj = Date(delivery.scheduledDate)
                val dateFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "PT"))
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                // Mapear Estado
                val uiStatus = when(delivery.status) {
                    DeliveryStatus.DELIVERED -> StatusType.ENTREGUE
                    DeliveryStatus.SCHEDULED -> StatusType.PENDENTE
                    DeliveryStatus.CANCELLED -> StatusType.NOT_ENTREGUE
                    DeliveryStatus.REJECTED -> StatusType.NOT_ENTREGUE
                    DeliveryStatus.UNDER_ANALYSIS -> StatusType.PENDENTE
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        beneficiaryName = beneficiary?.name ?: "Desconhecido",
                        beneficiaryIdDisplay = "NIF/CC: ${beneficiary?.id ?: "N/A"}", // Ajuste conforme o modelo Beneficiary
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
                val newStatus = if (delivered) DeliveryStatus.DELIVERED else DeliveryStatus.CANCELLED // Ou REJECTED/NOT_DELIVERED
                deliveryRepository.updateDeliveryStatus(deliveryId, newStatus)
                // Recarregar para atualizar a UI
                loadDelivery(deliveryId)
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }
}
