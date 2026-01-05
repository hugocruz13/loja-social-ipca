package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 ** Definição da classe UserDto para transferência de dados com o Firebase e do modelo `User` de domínio.
 */
data class UserDto(
    @PropertyName("id")
    val id: String? = null,

    @PropertyName("name")
    val name: String? = null,

    @PropertyName("email")
    val email: String? = null,

    @PropertyName("role")
    val role: String? = null
)