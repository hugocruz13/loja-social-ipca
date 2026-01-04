package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.UserDto
import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.models.UserRole

/**
 ** Uma classe de instância uníca (singleton) que realiza o mapeamento entre o UserDto
 * definido na camada de dados para o User definido na camada de domínio
 */
object UserMapper {
    fun toDomain(dto: UserDto): User {
        return User(
            id = dto.id ?: throw IllegalArgumentException("User ID não pode ser null"),
            name = dto.name ?: throw IllegalArgumentException("User name não pode ser null"),
            email = dto.email ?: throw IllegalArgumentException("User email não pode ser null"),
            role = when (dto.role?.uppercase()) {
                "STAFF" -> UserRole.STAFF
                "BENEFICIARY" -> UserRole.BENEFICIARY
                else -> throw IllegalArgumentException("Role inválido ou não definido")
            }
        )
    }

    fun toDto(user: User): UserDto {
        return UserDto(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.role.name
        )
    }
}