package com.example.campuslostfound.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

data class Chat(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(), // UID to Name mapping
    val lastMessage: String = "",
    val lastTimestamp: Timestamp = Timestamp.now()
)
