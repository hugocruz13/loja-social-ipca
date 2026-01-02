package pt.ipca.lojasocial.domain.models

import pt.ipca.lojasocial.domain.models.UserRole.BENEFICIARY
import pt.ipca.lojasocial.domain.models.UserRole.STAFF


/**
 * Tipos de utilizador no sistema.
 *
 * - [STAFF]: Funcionários com acesso á gestão da loja social
 * - [BENEFICIARY]: Beneficiários com acesso limitado
 */
enum class UserRole {
    STAFF,
    BENEFICIARY
}

/**
 * Representa um utilizador do sistema.
 *
 * **Invariantes garantidas:**
 * - ID nunca é vazio
 * - Email sempre válido
 * - Nome nunca é vazio
 * - Role sempre definido
 *
 * @property id Identificador único do utilizador
 * @property name Nome completo do utilizador
 * @property email Email único do utilizador (já validado)
 * @property role Tipo de utilizador no sistema
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
)
