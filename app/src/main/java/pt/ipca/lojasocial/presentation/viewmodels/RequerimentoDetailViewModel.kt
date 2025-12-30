package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import pt.ipca.lojasocial.domain.use_cases.request.GetRequestByIdUseCase
import pt.ipca.lojasocial.presentation.components.StatusType
import pt.ipca.lojasocial.presentation.models.RequestDetailUiModel
import javax.inject.Inject

@HiltViewModel
class RequerimentoDetailViewModel @Inject constructor(
    private val getRequestByIdUseCase: GetRequestByIdUseCase,
    private val requestRepository: RequestRepository,
    private val storageRepository: StorageRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    savedStateHandle: SavedStateHandle // Para pegar o ID da navegação
) : ViewModel() {

    private val _uiState = MutableStateFlow<RequestDetailUiModel?>(null)
    val uiState: StateFlow<RequestDetailUiModel?> = _uiState

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Pega o ID passado na rota "requerimentodetails?id={id}"
    private val requestId: String? = savedStateHandle["id"]

    init {
        loadRequest()
    }

    private fun loadRequest() {
        if (requestId == null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val detail = getRequestByIdUseCase(requestId)
                _uiState.value = detail
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- AÇÕES ---

    // Mapa auxiliar para apresentar nomes bonitos na UI
    val docLabels = mapOf(
        "identificacao" to "Doc. Identificação",
        "agregado" to "Doc. Agregado Familiar",
        "morada" to "Comprovativo de Morada",
        "rendimento" to "Comprovativo de Rendimentos",
        "matricula" to "Comprovativo de Matrícula"
    )

    // --- AÇÃO: APROVAR ---
    fun approveRequest() {
        val requestId = _uiState.value?.id ?: return
        val beneficiaryId = _uiState.value?.beneficiaryId ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Atualizar Requerimento para APROVADA
                requestRepository.updateStatusAndObservation(requestId, StatusType.APROVADA,"")

                // 2. Atualizar Beneficiário para ATIVO
                beneficiaryRepository.updateStatus(beneficiaryId, BeneficiaryStatus.ATIVO)

                loadRequest()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- AÇÃO: REJEITAR ---
    fun rejectRequest(justificacao: String) {
        val requestId = _uiState.value?.id ?: return
        val beneficiaryId = _uiState.value?.beneficiaryId ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Atualizar Requerimento para REJEITADA e gravar justificação
                requestRepository.updateStatusAndObservation(
                    id = requestId,
                    status = StatusType.REJEITADA,
                    observation = justificacao
                )

                // 2. Atualizar Beneficiário para INATIVO
                beneficiaryRepository.updateStatus(beneficiaryId, BeneficiaryStatus.INATIVO)

                loadRequest()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- AÇÃO: DOCUMENTOS INCORRETOS ---
    fun markDocumentsIncorrect(requestId: String, selectedDocKeys: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true

            val currentRequest = requestRepository.getRequestById(requestId)
            if (currentRequest != null) {
                // Cria um novo mapa mutável baseado no atual
                val updatedDocs = currentRequest.documents.toMutableMap()

                // Para cada documento selecionado:
                selectedDocKeys.forEach { key ->
                    val url = updatedDocs[key]
                    if (url != null) {
                        // 1. Apagar do Storage
                        storageRepository.deleteFile(url)
                        // 2. Colocar a null no Mapa (mantendo a chave)
                        updatedDocs[key] = null
                    }
                }

                // 3. Atualizar na BD (Mapa novo + Estado novo)
                requestRepository.updateRequestDocsAndStatus(
                    id = requestId,
                    documents = updatedDocs,
                    status = StatusType.DOCS_INCORRETOS
                )

                // 4. Recarregar UI
                loadRequest()
            }
            _isLoading.value = false
        }
    }

    private fun updateStatus(status: StatusType) {
        val id = _uiState.value?.id ?: return
        viewModelScope.launch {
            requestRepository.updateStatusAndObservation(id, status, "") // Tens de adaptar para usar StatusType
            loadRequest()
        }
    }
}