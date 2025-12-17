package pt.ipca.lojasocial.data.remote.dto

/**
 * Definição da classe BeneficiaryDto para transferência de dados com o Firebase
 * e estrutura intermédia para o mapeamento do modelo `Beneficiary` de domínio.
 * * **Notas Técnicas:**
 * - Exige construtor vazio para serialização do Firebase.
 * - Campos anuláveis (nullable) para tolerância a falhas na leitura da BD.
 */
data class BeneficiaryDto(
    var id: String? = null,
    var nome: String? = null,
    var email: String? = null,
    var dataNascimento: Int? = null,
    var idAnoLetivo: String? = null,
    var telemovel: Int? = null,
    var estado: String? = null // Guardamos o Enum como String na BD
)