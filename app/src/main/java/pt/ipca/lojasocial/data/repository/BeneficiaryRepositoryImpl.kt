package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.BeneficiaryDto
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeneficiaryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BeneficiaryRepository {

    // Referência direta à coleção criada na consola
    private val collection = firestore.collection("beneficiarios")

    /**
     * Suporta: GetBeneficiariesUseCase
     * Obtém tudo e converte. A ordenação é feita depois no UseCase.
     */
    override suspend fun getBeneficiaries(): List<Beneficiary> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(BeneficiaryDto::class.java)?.toDomain(doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Suporta: GetBeneficiaryByIdUseCase
     */
    override suspend fun getBeneficiaryById(id: String): Beneficiary? {
        return try {
            val doc = collection.document(id).get().await()

            if (doc.exists()) {
                doc.toObject(BeneficiaryDto::class.java)?.toDomain(doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getBeneficiaryByUid(uid: String): Beneficiary {
        val snapshot = collection.whereEqualTo("userId", uid).limit(1).get().await()
        if (snapshot.isEmpty) {
            throw Exception("Beneficiary profile not found for UID: $uid")
        }
        val doc = snapshot.documents.first()
        return doc.toObject(BeneficiaryDto::class.java)?.toDomain(doc.id)
            ?: throw Exception("Failed to parse beneficiary data")
    }

    /**
     * Suporta: AddBeneficiaryUseCase
     * Usa .set() com o ID fornecido pelo domínio (ex: número de aluno).
     */
    override suspend fun addBeneficiary(beneficiary: Beneficiary) {
        try {
            val dto = beneficiary.toDto()
            // Como o UseCase valida que o ID existe, usamos esse ID como chave do documento
            collection.document(beneficiary.id).set(dto).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Suporta: UpdateBeneficiaryUseCase
     * Usa SetOptions.merge() para ser mais seguro, embora o set simples também funcionasse.
     */
    override suspend fun updateBeneficiary(beneficiary: Beneficiary) {
        try {
            val dto = beneficiary.toDto()
            collection.document(beneficiary.id).set(dto, SetOptions.merge()).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Suporta: GetBeneficiariesByYearUseCase
     * Faz a query filtering pelo campo "idAnoLetivo"
     */
    override suspend fun getBeneficiariesBySchoolYear(schoolYear: String): List<Beneficiary> {
        return try {
            val snapshot = collection
                .whereEqualTo("idAnoLetivo", schoolYear)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(BeneficiaryDto::class.java)?.toDomain(doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateStatus(id: String, status: BeneficiaryStatus) {
        collection.document(id)
            .update("estado", status.name)
            .await()
    }
}