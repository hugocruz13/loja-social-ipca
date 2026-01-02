package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.CampaignDto
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.models.CampaignType

fun CampaignDto.toDomain(): Campaign {
    return Campaign(
        id = id,
        title = nome,
        description = descricao,
        startDate = dataInicio,
        endDate = dataFim,
        imageUrl = imagemUrl,
        type = if (tipo == "Interno") CampaignType.INTERNAL else CampaignType.EXTERNAL,
        status = when (estado) {
            "Ativa" -> CampaignStatus.ACTIVE
            "Agendada" -> CampaignStatus.PLANNED
            "Completa" -> CampaignStatus.INACTIVE
            else -> CampaignStatus.PLANNED
        }
    )
}

fun Campaign.toDto(): CampaignDto {
    return CampaignDto(
        id = id,
        nome = title,
        descricao = description,
        dataInicio = startDate,
        dataFim = endDate,
        imagemUrl = imageUrl,
        tipo = if (type == CampaignType.INTERNAL) "Interno" else "Externo",
        estado = when (status) {
            CampaignStatus.ACTIVE -> "Ativa"
            CampaignStatus.PLANNED -> "Agendada"
            CampaignStatus.INACTIVE -> "Completa"
        }
    )
}