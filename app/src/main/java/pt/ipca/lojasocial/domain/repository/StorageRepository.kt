package pt.ipca.lojasocial.domain.repository

import android.net.Uri

interface StorageRepository {
    /**
     * Faz upload de um ficheiro e devolve o URL de download.
     * @param uri O caminho do ficheiro no telemóvel.
     * @param fileName O nome com que o ficheiro vai ficar guardado (ex: "requerimentos/user123_doc.pdf").
     * @return O URL de download (String).
     */
    suspend fun uploadFile(uri: Uri, fileName: String): String
}