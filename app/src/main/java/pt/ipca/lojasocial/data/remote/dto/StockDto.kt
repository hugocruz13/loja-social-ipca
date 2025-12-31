package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class StockDto(

    @get:PropertyName("productId") @set:PropertyName("productId")
    var productId: String = "",

    @get:PropertyName("campaignId") @set:PropertyName("campaignId")
    var campaignId: String? = null,

    @get:PropertyName("quantity") @set:PropertyName("quantity")
    var quantity: Int = 0,

    @get:PropertyName("entryDate") @set:PropertyName("entryDate")
    var entryDate: Long = 0L,

    @get:PropertyName("expiryDate") @set:PropertyName("expiryDate")
    var expiryDate: Long = 0L,

    @get:PropertyName("observations") @set:PropertyName("observations")
    var observations: String? = null
)