package com.midas.whatsapp.Model.data

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val about: String = "Hey there! I'm using WhatsApp"
)