package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import pt.ipca.lojasocial.domain.models.AppLog
import pt.ipca.lojasocial.domain.repository.LogRepository
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LogRepository {

    override fun getLogs(): Flow<List<AppLog>> = callbackFlow {
        val listener = firestore.collection("logs")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Garante a ordem cronológica
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppLog::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}