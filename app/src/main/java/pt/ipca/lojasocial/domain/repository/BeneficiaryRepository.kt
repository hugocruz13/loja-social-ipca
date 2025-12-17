package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Beneficiary

interface BeneficiaryRepository {
    // Suspend fun porque é uma operação assíncrona (IO)
    suspend fun getBeneficiaryList(): List<Beneficiary>
}