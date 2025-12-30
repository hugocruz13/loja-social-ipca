package pt.ipca.lojasocial.data.remote

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.remote.dto.UserDto
import javax.inject.Inject


/**
 ** Implementação da lógica de acesso (remoto) à API do FirebaseAuth
 */
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
){

    suspend fun login(email:String, password:String): UserDto {
        //1. Autenticar
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Utilizador não encontrado")

        //2. Forçar refresh do token para obter custom claims atualizados
        val tokenResult = firebaseUser.getIdToken(true).await()

        //3. Extrair custom claims
        val customClaims = tokenResult.claims
        val role = customClaims["role"] as? String
            ?: throw Exception("Role não definida para o utilizador")

        //4. Buscar dados adicionais
        val name = firebaseUser.displayName ?: ""
        val email = firebaseUser.email ?: ""
        val uid = firebaseUser.uid

        return UserDto(
            id = uid,
            name = name,
            email = email,
            role = role
        )
    }

    suspend fun getCurrentUser(): UserDto? {
        val firebaseUser = auth.currentUser ?: return null

        //Refresh tokens para obter claims atualizados
        val tokenResult = firebaseUser.getIdToken(true).await()
        val customClaims = tokenResult.claims
        val role = customClaims["role"] as? String

        val user = Firebase.auth.currentUser
        val name = user?.displayName ?: ""
        val email = user?.email ?: ""
        val uid = user?.uid ?: ""

        return UserDto(
            id = uid,
            name = name,
            email = email,
            role = role)
    }

    fun logout() {
        auth.signOut()
    }


}