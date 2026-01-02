package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.AppLogDto
import pt.ipca.lojasocial.domain.models.AppLog
import pt.ipca.lojasocial.domain.repository.LogRepository
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LogRepository {

    private val collection = firestore.collection("logs")

    override fun getLogs(): Flow<List<AppLog>> = callbackFlow {
        val listener = collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<AppLogDto>()?.toDomain()
                } ?: emptyList()

                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveLog(log: AppLog) {
        val dto = log.toDto()
        collection.add(dto).await()
    }



}