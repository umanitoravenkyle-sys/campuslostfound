package com.example.campuslostfound.model

import com.google.firebase.Timestamp

data class Item(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val location: String = "",
    val date: String = "",
    val description: String = "",
    val type: String = "", // "Lost" or "Found"
    val reportedBy: String = "", // User UID
    val reporterName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val imageUrl: String? = null,
    val status: String = "Open" // "Open" or "Resolved"
)
