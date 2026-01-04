package pt.ipca.lojasocial.domain.models

data class SchoolYear(
    val id: String,
    val label: String,
    val startDate: Long,
    val endDate: Long
) {
    fun isCurrent(): Boolean {
        val now = System.currentTimeMillis()
        return now in startDate..endDate
    }
}