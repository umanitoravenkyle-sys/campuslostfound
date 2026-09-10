package com.example.campuslostfound.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Notification(
    val id: String = "",
    val userId: String = "", // Recipient
    val title: String = "",
    val message: String = "",
    val type: String = "Info", // "Message", "Resolved", "System"
    val timestamp: Timestamp = Timestamp.now(),
    val seen: Boolean = false,
    val senderName: String? = null, // Added to show real name
    val relatedId: String? = null // e.g. Chat ID or Item ID
)
