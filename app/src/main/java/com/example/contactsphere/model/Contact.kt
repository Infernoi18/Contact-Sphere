package com.example.contactsphere.model

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val role: String,
    val bio: String,
    val isFavorite: Boolean = false
)
