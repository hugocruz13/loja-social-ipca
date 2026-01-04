package pt.ipca.lojasocial.domain.models

import pt.ipca.lojasocial.domain.models.ProductType.CLEANING
import pt.ipca.lojasocial.domain.models.ProductType.FOOD
import pt.ipca.lojasocial.domain.models.ProductType.HYGIENE
import pt.ipca.lojasocial.domain.models.ProductType.OTHER


/**
 * Define as categorias principais dos bens geridos pela Loja Social.
 *
 * - [FOOD]: Géneros alimentares (ex: Arroz, Leite, Enlatados).
 * - [HYGIENE]: Produtos de higiene pessoal (ex: Champô, Pasta de dentes).
 * - [CLEANING]: Produtos de limpeza doméstica (ex: Detergentes, Lixívia).
 * - [OTHER]: Outros bens que não se enquadram nas categorias principais (fallback).
 */
enum class ProductType {
    FOOD,
    HYGIENE,
    CLEANING,
    OTHER
}

/**
 * Representa a ficha técnica ou definição de um produto no catálogo.
 *
 * Esta entidade define "o que é" o bem (ex: "Leite Meio Gordo"), mas não guarda
 * a quantidade existente nem a validade. Para quantidades e gestão de lotes,
 * ver a entidade [Stock].
 *
 * **Invariantes sugeridas:**
 * - O `name` deve ser único para evitar duplicados no catálogo.
 * - O `name` não deve ser vazio.
 *
 * @property id Identificador único do produto no sistema.
 * @property name Nome descritivo do produto (ex: "Arroz Agulha 1kg").
 * @property type Categoria a que o produto pertence (ver [ProductType]).
 * @property photoUrl URL opcional para uma imagem ilustrativa do produto.
 * @property observations Notas adicionais ou especificidades do produto (opcional).
 */
data class Product(
    val id: String,
    val name: String,
    val type: ProductType,
    val photoUrl: String? = null,
    val observations: String? = null,
)