package com.example.dsmevento.data.model

data class User(
    val uid: String = "",
    val createdAt: Long = 0L,
    val displayName: String = "",
    val email: String = "",
    val role: String = "user"
)