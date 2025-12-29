package pt.ipca.lojasocial.domain.models

/**
 * Estados de um beneficiário no sistema.
 *
 * - [ATIVO]: Beneficiário ativo e a receber apoio da loja social
 * - [INATIVO]: Beneficiário registado mas atualmente a não receber apoio
 */
enum class BeneficiaryStatus {
    ATIVO,
    INATIVO
}

/**
 * Representa um beneficiário registado na Loja Social.
 * Este modelo é puro e independente de frameworks (usado na camada de Domínio).
 *
 * **Invariantes garantidas:**
 * - ID nunca é nulo ou vazio
 * - Nome é obrigatório
 * - Estado (Status) é sempre um valor válido do enum (nunca null)
 * - Ano Letivo está sempre associado
 *
 * @property id Identificador único do beneficiário (gerado pelo Firebase)
 * @property name Nome completo do beneficiário
 * @property email Email de contacto principal
 * @property birthDate Data de nascimento (representação numérica/timestamp)
 * @property schoolYearId Identificador do ano letivo ao qual o beneficiário pertence
 * @property phoneNumber Número de telemóvel para contacto direto
 * @property status Estado atual da conta (ATIVO ou INATIVO)
 */
data class Beneficiary(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: Int,
    val schoolYearId: String,
    val phoneNumber: Int,
    val ccNumber: String,
    val status: BeneficiaryStatus
)