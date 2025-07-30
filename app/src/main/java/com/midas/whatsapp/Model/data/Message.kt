package com.midas.whatsapp.Model.data

data class Message(
    val id: String = "",    // Firebase push key
    val senderId: String = "",
    val receiverId: String = "",    // For one-to-one chat, indicates who receives
    val text: String = "",
    val timestamp: Long = 0L
)