package pt.ipca.lojasocial.data.mapper

import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.lojasocial.data.remote.dto.DeliveryDto
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus

object DeliveryMapper {
    fun toDomain(id: String, dto: DeliveryDto): Delivery {
        return Delivery(
            id = id,
            beneficiaryId = dto.idBeneficiario?.id
                ?: throw IllegalArgumentException("Beneficiary ID DocumentReference cannot be null"),
            date = dto.dataEntrega,
            scheduledDate = dto.dataHoraPlaneada,
            status = when (dto.estado.uppercase()) {
                "AGENDADA" -> DeliveryStatus.SCHEDULED
                "ENTREGUE" -> DeliveryStatus.DELIVERED
                "CANCELADA" -> DeliveryStatus.CANCELLED
                "REJEITADA" -> DeliveryStatus.REJECTED
                "EM_ANALISE" -> DeliveryStatus.UNDER_ANALYSIS
                else -> throw IllegalArgumentException("Estado de entrega inválido ou não definido: ${dto.estado}")
            },
            items = dto.produtosEntregues,
            observations = dto.observacoes.ifEmpty { null },
            createdBy = dto.criadoPor?.id
                ?: throw IllegalArgumentException("Created By DocumentReference cannot be null")
        )
    }

    fun toDto(delivery: Delivery, firestore: FirebaseFirestore): DeliveryDto {
        return DeliveryDto(
            criadoPor = firestore.collection("colaboradores").document(delivery.createdBy),
            dataEntrega = delivery.date,
            dataHoraPlaneada = delivery.scheduledDate,
            estado = when (delivery.status) {
                DeliveryStatus.SCHEDULED -> "AGENDADA"
                DeliveryStatus.DELIVERED -> "ENTREGUE"
                DeliveryStatus.CANCELLED -> "CANCELADA"
                DeliveryStatus.REJECTED -> "REJEITADA"
                DeliveryStatus.UNDER_ANALYSIS -> "EM_ANALISE"
            },
            idBeneficiario = firestore.collection("beneficiarios").document(delivery.beneficiaryId),
            observacoes = delivery.observations ?: "",
            produtosEntregues = delivery.items
        )
    }
}