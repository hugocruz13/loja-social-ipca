package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.EmailRequest
import pt.ipca.lojasocial.domain.models.NotificationRequest
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import pt.ipca.lojasocial.domain.use_cases.request.GetRequestByIdUseCase
import pt.ipca.lojasocial.presentation.models.RequestDetailUiModel
import javax.inject.Inject

@HiltViewModel
class RequerimentoDetailViewModel @Inject constructor(
    private val getRequestByIdUseCase: GetRequestByIdUseCase,
    private val requestRepository: RequestRepository,
    private val storageRepository: StorageRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val communicationRepository: CommunicationRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<RequestDetailUiModel?>(null)
    val uiState: StateFlow<RequestDetailUiModel?> = _uiState

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val requestId: String? = savedStateHandle["id"]

    // Mapa auxiliar para apresentar nomes bonitos na UI e nos Emails
    val docLabels = mapOf(
        "identificacao" to "Doc. Identificação",
        "agregado" to "Doc. Agregado Familiar",
        "morada" to "Comprovativo de Morada",
        "rendimento" to "Comprovativo de Rendimentos",
        "matricula" to "Comprovativo de Matrícula"
    )

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

    // --- AÇÃO: APROVAR ---
    fun approveRequest() {
        val requestId = _uiState.value?.id ?: return
        val beneficiaryId = _uiState.value?.beneficiaryId ?: return
        val beneficiaryEmail = _uiState.value?.email ?: return
        // Assumindo que o UiModel tem o nome, senão usa um genérico
        val beneficiaryName = _uiState.value?.beneficiaryName ?: "Beneficiário"

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Atualizar BD
                requestRepository.updateStatusAndObservation(requestId, StatusType.APROVADA, "")
                beneficiaryRepository.updateStatus(beneficiaryId, BeneficiaryStatus.ATIVO)

                loadRequest()

                communicationRepository.sendNotification(
                    NotificationRequest(
                        userId = beneficiaryId,
                        title = "Requerimento Aprovado! 🎉",
                        message = "O seu pedido foi validado. Já pode aceder à aplicação.",
                        data = mapOf("screen" to "dashboard") // Para abrires o ecrã certo ao clicar
                    )
                )

                // 3. Email com HTML Bonito
                communicationRepository.sendEmail(
                    EmailRequest(
                        to = beneficiaryEmail,
                        subject = "Boas notícias! O seu pedido foi Aprovado ✅",
                        body = getHtmlAprovado(beneficiaryName),
                        isHtml = true,
                        senderName = "Loja Social IPCA"
                    )
                )
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
        val beneficiaryEmail = _uiState.value?.email ?: return
        val beneficiaryName = _uiState.value?.beneficiaryName ?: "Beneficiário"

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Atualizar BD
                requestRepository.updateStatusAndObservation(
                    id = requestId,
                    status = StatusType.REJEITADA,
                    observation = justificacao
                )
                beneficiaryRepository.updateStatus(beneficiaryId, BeneficiaryStatus.INATIVO)

                loadRequest()

                communicationRepository.sendNotification(
                    NotificationRequest(
                        userId = beneficiaryId,
                        title = "Pedido Rejeitado ❌",
                        message = "O seu pedido foi atualizado. Verifique o email para detalhes.",
                        data = mapOf("screen" to "request_status")
                    )
                )

                // 3. Email com HTML Bonito (Faltava isto)
                communicationRepository.sendEmail(
                    EmailRequest(
                        to = beneficiaryEmail,
                        subject = "Informação sobre o seu pedido ❌",
                        body = getHtmlRejeitado(beneficiaryName, justificacao), // <--- HTML AQUI
                        isHtml = true,
                        senderName = "Loja Social IPCA"
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- AÇÃO: DOCUMENTOS INCORRETOS ---
    fun markDocumentsIncorrect(
        requestId: String,
        selectedDocKeys: List<String>,
        observacaoExtra: String = ""
    ) {
        val beneficiaryId = _uiState.value?.beneficiaryId ?: return
        val beneficiaryEmail = _uiState.value?.email ?: return
        val beneficiaryName = _uiState.value?.beneficiaryName ?: "Beneficiário"

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentRequest = requestRepository.getRequestById(requestId)
                if (currentRequest != null) {
                    val updatedDocs = currentRequest.documents.toMutableMap()

                    // Traduzir chaves para nomes legíveis para o email (ex: "morada" -> "Comprovativo de Morada")
                    val docsNamesList = mutableListOf<String>()

                    selectedDocKeys.forEach { key ->
                        val url = updatedDocs[key]
                        if (url != null) {
                            storageRepository.deleteFile(url)
                            updatedDocs[key] = null
                        }
                        // Adiciona o nome bonito à lista
                        docsNamesList.add(docLabels[key] ?: key)
                    }

                    // 1. Atualizar BD
                    requestRepository.updateRequestDocsAndStatus(
                        id = requestId,
                        documents = updatedDocs,
                        status = StatusType.DOCS_INCORRETOS
                    )

                    // Se tiver observação extra, atualiza também
                    if (observacaoExtra.isNotEmpty()) {
                        requestRepository.updateStatusAndObservation(
                            requestId,
                            StatusType.DOCS_INCORRETOS,
                            observacaoExtra
                        )
                    }

                    loadRequest()

                    // 2. Notificação Push
                    communicationRepository.sendNotification(
                        NotificationRequest(
                            userId = beneficiaryId,
                            title = "Ação Necessária ⚠️",
                            message = "Há problemas com os seus documentos. Toque para corrigir.",
                            data = mapOf(
                                "screen" to "request_status",
                                "requestId" to requestId
                            )
                        )
                    )

                    // 3. Email com HTML Bonito
                    // Formata a lista de documentos para HTML
                    val docsHtmlList = docsNamesList.joinToString(separator = "") { "<li>$it</li>" }
                    val motivoTexto =
                        if (observacaoExtra.isNotEmpty()) "<br>Nota: $observacaoExtra" else ""

                    communicationRepository.sendEmail(
                        EmailRequest(
                            to = beneficiaryEmail,
                            subject = "Ação Necessária: Documentos do pedido ⚠️",
                            body = getHtmlIncorreto(
                                beneficiaryName,
                                "<ul>$docsHtmlList</ul>$motivoTexto"
                            ), // <--- HTML AQUI
                            isHtml = true,
                            senderName = "Loja Social IPCA"
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- TEMPLATES HTML (Strings Kotlin) ---

    private fun getHtmlAprovado(nome: String): String {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                <div style="text-align: center; margin-bottom: 20px;">
                    <h1 style="color: #2e7d32; margin: 0;">Parabéns! 🎉</h1>
                </div>
                <div style="background-color: #f1f8e9; padding: 15px; border-radius: 5px; border-left: 5px solid #2e7d32;">
                    <p style="font-size: 16px; color: #333;">Olá <strong>$nome</strong>,</p>
                    <p style="font-size: 16px; color: #333;">Temos o prazer de informar que o seu requerimento foi <strong>APROVADO</strong> com sucesso! ✅</p>
                </div>
                <p style="color: #555; line-height: 1.5;">
                    A sua situação foi validada pela equipa da Loja Social. Pode agora aceder à aplicação.
                </p>
                <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
                <p style="font-size: 12px; color: #999; text-align: center;">Loja Social IPCA © 2025</p>
            </div>
        """.trimIndent()
    }

    private fun getHtmlRejeitado(nome: String, motivo: String): String {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                <div style="text-align: center; margin-bottom: 20px;">
                    <h1 style="color: #c62828; margin: 0;">Atualização do Pedido</h1>
                </div>
                <div style="background-color: #ffebee; padding: 15px; border-radius: 5px; border-left: 5px solid #c62828;">
                    <p style="font-size: 16px; color: #333;">Olá <strong>$nome</strong>,</p>
                    <p style="font-size: 16px; color: #333;">Lamentamos informar que o seu requerimento foi <strong>REJEITADO</strong>. ❌</p>
                </div>
                <p style="color: #555; margin-top: 20px;"><strong>Motivo da rejeição:</strong></p>
                <p style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; color: #444; font-style: italic;">
                    "$motivo"
                </p>
                <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
                <p style="font-size: 12px; color: #999; text-align: center;">Loja Social IPCA © 2025</p>
            </div>
        """.trimIndent()
    }

    private fun getHtmlIncorreto(nome: String, listaErrosHtml: String): String {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                <div style="text-align: center; margin-bottom: 20px;">
                    <h1 style="color: #f9a825; margin: 0;">Ação Necessária ⚠️</h1>
                </div>
                <div style="background-color: #fffde7; padding: 15px; border-radius: 5px; border-left: 5px solid #f9a825;">
                    <p style="font-size: 16px; color: #333;">Olá <strong>$nome</strong>,</p>
                    <p style="font-size: 16px; color: #333;">O seu requerimento está pendente porque detetámos <strong>problemas nos documentos</strong>.</p>
                </div>
                <p style="color: #555; margin-top: 20px;"><strong>O que precisa de reenviar:</strong></p>
                <div style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; color: #444;">
                    $listaErrosHtml
                </div>
                <p style="color: #555; line-height: 1.5; margin-top: 20px;">
                    Por favor aceda à aplicação para submeter os documentos corretos.
                </p>
                <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
                <p style="font-size: 12px; color: #999; text-align: center;">Loja Social IPCA © 2025</p>
            </div>
        """.trimIndent()
    }
}