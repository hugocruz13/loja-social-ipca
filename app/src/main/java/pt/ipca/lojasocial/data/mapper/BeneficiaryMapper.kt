package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.BeneficiaryDto
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus

/**
 * Uma classe de instância única (singleton) que realiza o mapeamento entre o BeneficiaryDto
 * definido na camada de dados para o Beneficiary definido na camada de domínio.
 */
object BeneficiaryMapper {

    fun toDomain(dto: BeneficiaryDto): Beneficiary {
        return Beneficiary(
            id = dto.id ?: throw IllegalArgumentException("Beneficiary ID cannot be null"),
            name = dto.nome ?: "Sem Nome", // Ou lançar erro
            email = dto.email ?: "",
            birthDate = dto.dataNascimento ?: 0,
            schoolYearId = dto.idAnoLetivo ?: "",
            phoneNumber = dto.telemovel ?: 0,
            status = when (dto.estado?.uppercase()) {
                "ATIVO" -> BeneficiaryStatus.ATIVO
                "INATIVO" -> BeneficiaryStatus.INATIVO
                else -> BeneficiaryStatus.INATIVO // Default case safe
            }
        )
    }

    fun toDto(domain: Beneficiary): BeneficiaryDto {
        return BeneficiaryDto(
            id = domain.id,
            nome = domain.name,
            email = domain.email,
            dataNascimento = domain.birthDate,
            idAnoLetivo = domain.schoolYearId,
            telemovel = domain.phoneNumber,
            estado = domain.status.name // Converte ENUM para String (ex: "ATIVO")
        )
    }
}