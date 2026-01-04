package pt.ipca.lojasocial.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.repository.StorageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadFile(uri: Uri, fileName: String): String {
        return try {
            // 1. Criar uma referência para onde o ficheiro vai ficar
            val storageRef = storage.reference.child(fileName)

            // 2. Fazer o upload
            storageRef.putFile(uri).await()

            // 3. Obter o URL de download após o upload terminar
            val downloadUrl = storageRef.downloadUrl.await().toString()

            android.util.Log.d("STORAGE_OK", "URL Gerado: $downloadUrl")

            downloadUrl
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Falha no upload da imagem: ${e.message}")
        }
    }

    override suspend fun deleteFile(fileUrl: String) {
        try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl)
            storageRef.delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
            // Opcional: Não lançar erro se o ficheiro já não existir
        }
    }
}