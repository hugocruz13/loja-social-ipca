package pt.ipca.lojasocial.data.remote

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.remote.dto.UserDto
import javax.inject.Inject


/**
 ** Implementação da lógica de acesso (remoto) à API do FirebaseAuth
 */
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    suspend fun login(email: String, password: String): UserDto {
        //1. Autenticar
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Utilizador não encontrado")

        //4. Buscar dados adicionais
        val name = firebaseUser.displayName ?: ""
        val email = firebaseUser.email ?: ""
        val uid = firebaseUser.uid

        val role = getUserRole(uid)

        return UserDto(
            id = uid,
            name = name,
            email = email,
            role = role
        )
    }

    suspend fun signUp(email: String, pass: String, nome: String): String {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        result.user?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(nome).build()
        )
        return result.user?.uid ?: throw Exception("Erro ao obter UID após registo")
    }

    suspend fun getCurrentUser(): UserDto? {
        val firebaseUser = auth.currentUser ?: return null

        val user = Firebase.auth.currentUser
        val name = user?.displayName ?: ""
        val email = user?.email ?: ""
        val uid = user?.uid ?: ""

        val role = getUserRole(uid)

        return UserDto(
            id = uid,
            name = name,
            email = email,
            role = role
        )
    }

    fun logout() {
        auth.signOut()
    }

    private suspend fun getUserRole(uid: String): String? {
        // Check if the user is in the 'beneficiary' collection
        val beneficiaryDoc = firestore.collection("beneficiarios").document(uid).get().await()
        if (beneficiaryDoc.exists()) {
            return "BENEFICIARY"
        }

        // Check if the user is in the 'colaborador' collection
        val colaboradorDoc = firestore.collection("colaboradores").document(uid).get().await()
        if (colaboradorDoc.exists()) {
            return "STAFF"
        }

        // Return null or a default role if not found in either collection
        return null
    }


}