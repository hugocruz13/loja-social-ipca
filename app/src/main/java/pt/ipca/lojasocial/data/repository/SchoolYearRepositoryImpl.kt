package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import javax.inject.Inject

class SchoolYearRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SchoolYearRepository {

    private val collection = firestore.collection("anos_letivos")

    override fun getSchoolYears(): Flow<List<SchoolYear>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }

            val list = snapshot?.documents?.mapNotNull { doc ->
                SchoolYear(
                    id = doc.id,
                    label = doc.id.replace("_", "/"),
                    startDate = doc.getLong("dataInicio") ?: 0L,
                    endDate = doc.getLong("dataFim") ?: 0L
                )
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getSchoolYearById(id: String): SchoolYear? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                SchoolYear(
                    id = doc.id,
                    label = doc.id.replace("_", "/"),
                    startDate = doc.getLong("dataInicio") ?: 0L,
                    endDate = doc.getLong("dataFim") ?: 0L
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveSchoolYear(schoolYear: SchoolYear) {
        val data = hashMapOf(
            "dataInicio" to schoolYear.startDate,
            "dataFim" to schoolYear.endDate
        )
        collection.document(schoolYear.id).set(data).await()
    }
}