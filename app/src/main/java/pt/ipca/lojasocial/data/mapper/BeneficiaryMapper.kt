package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.BeneficiaryDto
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus

/**
 * Converte o DTO do Firebase para o Modelo de Domínio.
 * @param documentId O ID do documento (que vem separado do corpo do JSON no Firestore).
 */
fun BeneficiaryDto.toDomain(documentId: String): Beneficiary {
    return Beneficiary(
        id = documentId,
        name = this.name,
        email = this.email,
        birthDate = this.birthDate.toInt(), // Conversão de Long para Int conforme o teu domínio
        schoolYearId = this.schoolYearId,
        phoneNumber = this.phoneNumber,
        ccNumber = this.ccNumber,
        status = try {
            BeneficiaryStatus.valueOf(this.status.uppercase())
        } catch (e: Exception) {
            BeneficiaryStatus.INATIVO
        }
    )
}

/**
 * Converte o Modelo de Domínio para DTO para enviar para o Firebase.
 */
fun Beneficiary.toDto(): BeneficiaryDto {
    return BeneficiaryDto(
        name = this.name,
        email = this.email,
        birthDate = this.birthDate.toLong(),
        schoolYearId = this.schoolYearId,
        phoneNumber = this.phoneNumber,
        ccNumber = this.ccNumber,
        // Grava na base de dados como string (ex: "ATIVO")
        status = this.status.name
    )
}