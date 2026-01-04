package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.AppLogDto
import pt.ipca.lojasocial.domain.models.AppLog

fun AppLogDto.toDomain() = AppLog(
    id = id,
    acao = acao,
    detalhe = detalhe,
    utilizador = utilizador,
    timestamp = timestamp
)

fun AppLog.toDto() = AppLogDto(
    acao = acao,
    detalhe = detalhe,
    utilizador = utilizador,
    timestamp = timestamp
)