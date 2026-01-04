package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.SchoolYearDto
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
                // Converte para DTO e depois para Domain via Mapper
                doc.toObject<SchoolYearDto>()?.toDomain()
            } ?: emptyList()

            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getSchoolYearById(id: String): SchoolYear? {
        val doc = collection.document(id).get().await()
        return doc.toObject<SchoolYearDto>()?.toDomain()
    }

    override suspend fun saveSchoolYear(schoolYear: SchoolYear) {

        val dto = schoolYear.toDto()

        collection.document(schoolYear.id).set(dto).await()
    }
}