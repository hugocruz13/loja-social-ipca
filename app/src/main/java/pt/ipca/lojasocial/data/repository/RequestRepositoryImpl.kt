package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.RequestDto
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.presentation.components.StatusType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RequestRepository {

    // Aponta para a coleção 'requerimentos' (Português)
    private val collection = firestore.collection("requerimentos")

    override suspend fun addRequest(request: Request) {
        val dto = request.toDto()
        // Usa o ID gerado pelo domínio (UUID) como chave do documento
        collection.document(request.id).set(dto).await()
    }

    override suspend fun getRequestById(id: String): Request? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(RequestDto::class.java)?.toDomain(doc.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getRequestsByBeneficiary(beneficiaryId: String): List<Request> {
        return try {
            val snapshot = collection
                .whereEqualTo("idBeneficiario", beneficiaryId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(RequestDto::class.java)?.toDomain(it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRequestsByYear(schoolYearId: String, status: StatusType?): List<Request> {
        return try {
            var query = collection.whereEqualTo("idAnoLetivo", schoolYearId)

            if (status != null) {
                query = query.whereEqualTo("estado", status.name)
            }

            val snapshot = query.get().await()

            val lista = snapshot.documents.mapNotNull { doc ->

                // Tenta converter
                val dto = doc.toObject(RequestDto::class.java)

                dto?.toDomain(doc.id)
            }

            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateStatus(id: String, newStatus: StatusType) {
        // Atualiza apenas o campo 'estado' sem mexer no resto
        collection.document(id)
            .update("estado", newStatus.name)
            .await()
    }

    override suspend fun updateRequestDocsAndStatus(
        id: String,
        documents: Map<String, String?>,
        status: StatusType
    ) {
        val updates = mapOf(
            "documentosUrl" to documents,
            "estado" to status.name
        )

        collection.document(id)
            .update(updates)
            .await()
    }
}