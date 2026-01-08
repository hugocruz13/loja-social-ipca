package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.RequestDto
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.models.RequestType
import pt.ipca.lojasocial.domain.models.StatusType

fun RequestDto.toDomain(documentId: String): Request {
    return Request(
        id = documentId,
        beneficiaryId = this.beneficiaryId,
        schoolYearId = this.schoolYearId,
        submissionDate = this.submissionDate,
        // Passamos o mapa diretamente. Se vier null do DTO, usamos emptyMap
        documents = this.documentUrls,
        observations = this.observations,
        // Conversão Segura de Status
        status = try {
            StatusType.valueOf(this.status)
        } catch (e: Exception) {
            StatusType.ANALISE
        },
        // Conversão Segura de Tipo
        type = try {
            RequestType.valueOf(this.type)
        } catch (e: Exception) {
            RequestType.FOOD
        }
    )
}

fun Request.toDto(): RequestDto {
    return RequestDto(
        beneficiaryId = this.beneficiaryId,
        schoolYearId = this.schoolYearId,
        submissionDate = this.submissionDate,
        status = this.status.name,
        type = this.type?.name ?: "ALL",
        documentUrls = this.documents,
        observations = this.observations
    )
}