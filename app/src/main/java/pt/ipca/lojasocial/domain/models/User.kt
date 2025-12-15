package pt.ipca.lojasocial.domain.models

enum class UserRole {
    STAFF,
    BENEFICIARY
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
)
