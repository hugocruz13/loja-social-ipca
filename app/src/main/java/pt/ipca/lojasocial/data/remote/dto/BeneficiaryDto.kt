package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * Objeto de Transferência de Dados (DTO) para Beneficiários.
 *
 * Representa a estrutura exata do documento armazenado na coleção 'beneficiarios' do Firestore.
 * Utiliza anotações [PropertyName] para mapear os campos da base de dados (snake_case/português)
 * para as propriedades Kotlin (camelCase).
 *
 * **Nota:** As propriedades devem ter valores padrão para permitir a deserialização vazia do Firebase.
 */
data class BeneficiaryDto(
    // Mapeamento: "nome" (Firebase) <-> name (Código)
    @get:PropertyName("nome") @set:PropertyName("nome")
    var name: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    // Nota: O Firebase devolve números como Long. O teu domínio usa Int, faremos a conversão no Mapper.
    @get:PropertyName("dataNascimento") @set:PropertyName("dataNascimento")
    var birthDate: Long = 0,

    @get:PropertyName("idAnoLetivo") @set:PropertyName("idAnoLetivo")
    var schoolYearId: String = "",

    @get:PropertyName("telemovel") @set:PropertyName("telemovel")
    var phoneNumber: Int = 0,

    // Ex: "Ativo", "Inativo"
    @get:PropertyName("estado") @set:PropertyName("estado")
    var status: String = ""
)