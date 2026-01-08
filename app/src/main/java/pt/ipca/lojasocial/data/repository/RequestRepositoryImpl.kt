package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.RequestDto
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.RequestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RequestRepository {

    // Aponta para a coleção 'requerimentos' (Português)
    private val collection = firestore.collection("requerimentos")

    // --- MÉTODOS DE LEITURA ---

    override fun getRequestsByYear(
        schoolYearId: String,
        status: StatusType?
    ): Flow<List<Request>> = callbackFlow {
        // 1. Construir a Query
        var query: Query = collection.whereEqualTo("idAnoLetivo", schoolYearId)

        if (status != null) {
            query = query.whereEqualTo("estado", status.name)
        }

        // 2. Registar o Listener (Tempo Real)
        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Em caso de erro, fecha o flow com a exceção
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Mapear documentos Firestore (DTO) -> Domínio
                val requests = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(RequestDto::class.java)?.toDomain(doc.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                // Emitir a nova lista para o ViewModel
                trySend(requests)
            }
        }

        // 3. Callback de limpeza (Executado quando sais do ecrã / cancelas o Flow)
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override fun getRequestsByBeneficiary(beneficiaryId: String): Flow<List<Request>> =
        callbackFlow {
            val query = collection.whereEqualTo("idBeneficiario", beneficiaryId)

            val listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(RequestDto::class.java)?.toDomain(doc.id)
                    }
                    trySend(requests)
                }
            }

            awaitClose {
                listenerRegistration.remove()
            }
        }

    // --- MÉTODOS DE ESCRITA / LEITURA ÚNICA (MANTÊM-SE SUSPEND) ---

    override suspend fun addRequest(request: Request) {
        val dto = request.toDto()
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

    override suspend fun updateStatusAndObservation(
        id: String,
        status: StatusType,
        observation: String
    ) {
        collection.document(id)
            .update(
                mapOf(
                    "estado" to status.name,
                    "observacoes" to observation
                )
            )
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