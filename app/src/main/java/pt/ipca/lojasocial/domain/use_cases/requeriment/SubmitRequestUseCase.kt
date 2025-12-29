package pt.ipca.lojasocial.domain.use_cases.request

import android.net.Uri
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import java.util.UUID
import javax.inject.Inject

class SubmitRequestUseCase @Inject constructor(
    private val requestRepository: RequestRepository,
    private val storageRepository: StorageRepository
) {
    /**
     * Submete um pedido. Se houver ficheiro, faz upload primeiro.
     * @param request O objeto Request base.
     * @param fileUri O caminho do ficheiro no telemóvel (opcional).
     */
    suspend operator fun invoke(request: Request, fileUri: Uri?) {

        var requestFinal = request

        // 1. Lógica de Upload (se houver ficheiro)
        if (fileUri != null) {
            // Cria um nome seguro: "requerimentos/ID_DO_BENEFICIARIO/NOME_UNICO.pdf"
            val fileName = "requerimentos/${request.beneficiaryId}/${UUID.randomUUID()}"

            // Faz upload e obtém o link
            val downloadUrl = storageRepository.uploadFile(fileUri, fileName)

            // Adiciona o link à lista de documentos do pedido
            val novaLista = request.documentUrls + downloadUrl
            requestFinal = request.copy(documentUrls = novaLista)
        }

        // 2. Gravar no Firestore
        requestRepository.addRequest(requestFinal)
    }
}