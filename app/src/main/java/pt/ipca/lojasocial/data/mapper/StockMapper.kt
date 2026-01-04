package pt.ipca.lojasocial.data.mapper

import pt.ipca.lojasocial.data.remote.dto.StockDto
import pt.ipca.lojasocial.domain.models.Stock

fun StockDto.toDomain(id: String): Stock {
    return Stock(
        id = id,
        productId = productId,
        campaignId = campaignId,
        quantity = quantity,
        entryDate = entryDate,
        expiryDate = expiryDate,
        observations = observations
    )
}

fun Stock.toDto(): StockDto {
    return StockDto(
        productId = productId,
        campaignId = campaignId,
        quantity = quantity,
        entryDate = entryDate,
        expiryDate = expiryDate,
        observations = observations
    )
}