package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.RequestDto
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.models.RequestStatus
import pt.ipca.lojasocial.domain.models.RequestType

fun RequestDto.toDomain(documentId: String): Request {
    return Request(
        id = documentId,
        beneficiaryId = this.beneficiaryId,
        schoolYearId = this.schoolYearId,
        submissionDate = this.submissionDate,
        documentUrls = this.documentUrls,
        // Conversão Segura de Status
        status = try {
            RequestStatus.valueOf(this.status)
        } catch (e: Exception) {
            RequestStatus.SUBMITTED // Default se der erro
        },
        // Conversão Segura de Tipo
        type = try {
            RequestType.valueOf(this.type)
        } catch (e: Exception) {
            RequestType.FOOD // Default se der erro
        }
    )
}

fun Request.toDto(): RequestDto {
    return RequestDto(
        beneficiaryId = this.beneficiaryId,
        schoolYearId = this.schoolYearId,
        submissionDate = this.submissionDate,
        status = this.status.name, // Guarda "SUBMITTED", "APPROVED", etc.
        type = this.type.name,     // Guarda "FOOD", "HYGIENE", etc.
        documentUrls = this.documentUrls
    )
}