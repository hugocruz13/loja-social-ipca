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
     * Submete um pedido. Se houver ficheiro, faz upload primeiro e adiciona ao mapa.
     * @param request O objeto Request base.
     * @param fileUri O caminho do ficheiro no telemóvel (opcional).
     * @param docKey O nome/chave do documento (ex: "comprovativo_medico"). Default é "anexo".
     */
    suspend operator fun invoke(request: Request, fileUri: Uri?, docKey: String = "anexo") {

        var requestFinal = request

        // 1. Lógica de Upload (se houver ficheiro)
        if (fileUri != null) {
            // Cria um nome seguro: "requerimentos/ID_DO_BENEFICIARIO/TIPO_UUID"
            val fileName = "requerimentos/${request.beneficiaryId}/${docKey}_${UUID.randomUUID()}"

            // Faz upload e obtém o link
            val downloadUrl = storageRepository.uploadFile(fileUri, fileName)

            // MUDANÇA: Trabalhar com MAPA em vez de LISTA
            // Copiamos o mapa atual para um mutável
            val novoMapa = request.documents.toMutableMap()

            // Adicionamos/Atualizamos a entrada com a chave e o novo URL
            novoMapa[docKey] = downloadUrl

            // Atualizamos o objeto request com o novo mapa
            requestFinal = request.copy(documents = novoMapa)
        }

        // 2. Gravar no Firestore
        requestRepository.addRequest(requestFinal)
    }
}