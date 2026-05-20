package com.example.dsmevento.data.model

data class Event(
    val uid: String = "",
    val createdAt: Long = 0L,
    val date: Long = 0L,
    val description: String = "",
    val location: String = "",
    val name: String = "",
    val attendees: List<String> = emptyList(),
    val finished: Boolean = false
)