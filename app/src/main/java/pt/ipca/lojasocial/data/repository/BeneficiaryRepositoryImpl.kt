package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.BeneficiaryMapper
import pt.ipca.lojasocial.data.remote.dto.BeneficiaryDto
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository

class BeneficiaryRepositoryImpl(
    private val firestore: FirebaseFirestore // Injeção do Firebase
) : BeneficiaryRepository {

    override suspend fun getBeneficiaryList(): List<Beneficiary> {
        return try {
            // 1. Ir à coleção "beneficiarios" no Firebase
            val snapshot = firestore.collection("beneficiarios")
                .get()
                .await() // Espera pela resposta sem bloquear a thread principal

            // 2. Mapear cada documento encontrado
            snapshot.documents.mapNotNull { doc ->
                // Converte o JSON do Firebase para o teu DTO
                val dto = doc.toObject(BeneficiaryDto::class.java)

                // Se o DTO for válido, converte para Domain usando o Mapper
                dto?.let {
                    it.id = doc.id
                    BeneficiaryMapper.toDomain(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}