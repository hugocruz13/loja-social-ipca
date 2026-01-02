package pt.ipca.lojasocial.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Colaborador
import pt.ipca.lojasocial.domain.repository.StaffRepository
import javax.inject.Inject

class StaffRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : StaffRepository {

    private val collection = firestore.collection("colaboradores")

    override fun getStaff(): Flow<List<Colaborador>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Colaborador::class.java)?.copy(uid = doc.id)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createStaffMember(email: String, dados: Map<String, Any>): String {
        // Cria no Auth (Password padrão)
        val authResult = auth.createUserWithEmailAndPassword(email, "123456").await()
        val uid = authResult.user?.uid ?: throw Exception("Falha ao obter UID")

        // Salva no Firestore
        collection.document(uid).set(dados).await()
        return uid
    }

    override suspend fun updateStaffStatus(uid: String, status: Boolean) {
        collection.document(uid).update("ativo", status).await()
    }
}