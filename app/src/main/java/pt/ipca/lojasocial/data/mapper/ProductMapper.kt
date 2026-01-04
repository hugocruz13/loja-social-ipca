package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.ProductDto
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.ProductType

/**
 * Converte o DTO do Firebase para o Modelo de Domínio [Product].
 *
 * @param documentId O ID do documento (que vem separado do corpo do JSON no Firestore).
 * @return [Product] pronto para ser usado na UI/Domínio.
 */
fun ProductDto.toDomain(documentId: String): Product
{
    return Product(
        id = documentId,
        name = this.name,
        type = try {
            ProductType.valueOf(this.type.uppercase())
        } catch (e: Exception) {
            ProductType.OTHER
        },
        photoUrl = this.photoUrl,
        observations = this.observations
    )
}

/**
 * Converte o Modelo de Domínio [Product] para DTO para enviar para o Firebase.
 *
 * @return [ProductDto] com os dados formatados para persistência.
 */
fun Product.toDto(): ProductDto
{
    return ProductDto(
        name = this.name,
        type = this.type.name,
        photoUrl = this.photoUrl,
        observations = this.observations
    )
}