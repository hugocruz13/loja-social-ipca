package pt.ipca.lojasocial.domain.use_cases.staff

import pt.ipca.lojasocial.domain.repository.StaffRepository
import javax.inject.Inject

class GetStaffUseCase @Inject constructor(private val repository: StaffRepository) {
    operator fun invoke() = repository.getStaff()
}