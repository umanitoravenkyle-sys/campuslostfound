package com.example.campuslostfound

import com.example.campuslostfound.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")

    fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val notifs = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                // Sort manually in Kotlin to avoid index issues
                val sortedNotifs = notifs.sortedByDescending { it.timestamp }
                trySend(sortedNotifs)
            }
        awaitClose { listener.remove() }
    }

    fun markMessagesAsRead(userId: String, chatId: String) {
        notificationsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("relatedId", chatId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                var hasUpdates = false
                for (doc in snapshot.documents) {
                    val isSeen = doc.getBoolean("seen") ?: false
                    val type = doc.getString("type") ?: ""
                    if (!isSeen && type == "Message") {
                        batch.update(doc.reference, "seen", true)
                        batch.update(doc.reference, "isRead", true)
                        hasUpdates = true
                    }
                }
                if (hasUpdates) batch.commit()
            }
    }

    fun markAsRead(notificationId: String) {
        notificationsCollection.document(notificationId).update("seen", true)
    }

    fun markAllAsRead(userId: String) {
        notificationsCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) return@addOnSuccessListener
                
                val batch = db.batch()
                var hasUpdates = false
                for (doc in snapshot.documents) {
                    // Check if it needs updating (handle both seen and isRead for compatibility)
                    val isSeen = doc.getBoolean("seen") ?: false
                    val oldIsRead = doc.getBoolean("isRead") ?: false
                    
                    if (!isSeen || !oldIsRead) {
                        batch.update(doc.reference, "seen", true)
                        batch.update(doc.reference, "isRead", true) // Clean up old field too
                        hasUpdates = true
                    }
                }
                if (hasUpdates) batch.commit()
            }
    }

    fun sendNotification(notification: Notification) {
        val doc = notificationsCollection.document()
        notificationsCollection.document(doc.id).set(notification.copy(id = doc.id))
    }
}
