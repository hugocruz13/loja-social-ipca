package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class GetUpcomingDeliveriesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    // limitDays: Quantos dias no futuro queremos verificar (ex: 7 dias)
    suspend operator fun invoke(limitDays: Int): List<Delivery> {
        val currentTime = System.currentTimeMillis()
        val limitTime = currentTime + (limitDays * 24 * 60 * 60 * 1000L)

        return repository.getUpcomingDeliveries(limitTime)
    }
}