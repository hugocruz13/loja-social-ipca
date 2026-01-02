package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.SchoolYearDto
import pt.ipca.lojasocial.domain.models.SchoolYear

fun SchoolYearDto.toDomain(): SchoolYear {
    return SchoolYear(
        id = id,
        label = id.replace("_", "/"),
        startDate = dataInicio,
        endDate = dataFim
    )
}

fun SchoolYear.toDto(): SchoolYearDto {
    return SchoolYearDto(
        id = id,
        dataInicio = startDate,
        dataFim = endDate
    )
}