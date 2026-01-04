package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.StorageRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: StorageRepository
) {
    suspend operator fun invoke(uri: android.net.Uri, path: String): String? {
        return repository.uploadFile(uri, path) // Deve devolver o URL da imagem
    }
}