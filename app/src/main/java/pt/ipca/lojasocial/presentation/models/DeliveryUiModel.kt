package pt.ipca.lojasocial.presentation.models

import pt.ipca.lojasocial.domain.models.Delivery

data class DeliveryUiModel(
    val delivery: Delivery,
    val beneficiaryName: String
)