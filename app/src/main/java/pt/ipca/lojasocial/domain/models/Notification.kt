package pt.ipca.lojasocial.domain.models

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val date: Date,
    val screenDestination: String?,
    val readAt: Date? = null
)