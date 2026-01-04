package pt.ipca.lojasocial.data.remote.dto


data class ColaboradorDto(
    var nome: String = "",

    var email: String = "",

    var cargo: String = "",

    var permissao: String = "",

    var ativo: Boolean = true
)