package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class DeliveryDto(
    @get:PropertyName("criadoPor") @set:PropertyName("criadoPor")
    var criadoPor: DocumentReference? = null,

    @get:PropertyName("dataEntrega") @set:PropertyName("dataEntrega")
    var dataEntrega: Long = 0L,

    @get:PropertyName("dataHoraPlaneada") @set:PropertyName("dataHoraPlaneada")
    var dataHoraPlaneada: Long = 0L,

    @get:PropertyName("estado") @set:PropertyName("estado")
    var estado: String = "",

    @get:PropertyName("idBeneficiario") @set:PropertyName("idBeneficiario")
    var idBeneficiario: DocumentReference? = null,

    @get:PropertyName("observacoes") @set:PropertyName("observacoes")
    var observacoes: String = "",

    @get:PropertyName("produtosEntregues") @set:PropertyName("produtosEntregues")
    var produtosEntregues: Map<String, Int> = emptyMap()
)