package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.ColaboradorDto
import pt.ipca.lojasocial.domain.models.Colaborador

fun ColaboradorDto.toDomain(uid: String): Colaborador {
    return Colaborador(
        uid = uid,
        nome = nome,
        email = email,
        cargo = cargo,
        permissao = permissao,
        ativo = ativo
    )
}

fun Colaborador.toDto(): ColaboradorDto {
    return ColaboradorDto(
        nome = nome,
        email = email,
        cargo = cargo,
        permissao = permissao,
        ativo = ativo
    )
}