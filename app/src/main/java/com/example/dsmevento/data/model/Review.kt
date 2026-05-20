package com.example.dsmevento.data.model

data class Review(
    val uid: String = "",
    val comment: String = "",
    val createdAt: Long = 0L,
    val name: String = "",
    val rating: Int = 0
)