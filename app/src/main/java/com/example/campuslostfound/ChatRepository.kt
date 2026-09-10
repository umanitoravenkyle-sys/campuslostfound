package com.example.campuslostfound

import com.example.campuslostfound.model.Chat
import com.example.campuslostfound.model.Message
import com.example.campuslostfound.model.Notification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")
    private val notifRepo = NotificationRepository()

    fun getChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = chatsCollection.whereArrayContains("participantIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val chats = snapshot?.toObjects(Chat::class.java) ?: emptyList()
                // Sort manually in Kotlin to avoid needing a Firebase Index immediately
                val sortedChats = chats.sortedByDescending { it.lastTimestamp }
                trySend(sortedChats)
            }
        awaitClose { listener.remove() }
    }

    fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = chatsCollection.document(chatId).collection("messages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                val sortedMessages = messages.sortedBy { it.timestamp }
                trySend(sortedMessages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(chatId: String, senderId: String, content: String) {
        val chatDoc = chatsCollection.document(chatId).get().await()
        val chat = chatDoc.toObject(Chat::class.java)
        val otherId = chat?.participantIds?.find { it != senderId } ?: ""
        val senderName = chat?.participantNames?.get(senderId) ?: "Someone"

        val messageDoc = chatsCollection.document(chatId).collection("messages").document()
        val message = Message(
            id = messageDoc.id,
            senderId = senderId,
            content = content,
            timestamp = Timestamp.now()
        )

        db.runBatch { batch ->
            batch.set(messageDoc, message)
            batch.update(chatsCollection.document(chatId), 
                "lastMessage", content,
                "lastTimestamp", Timestamp.now()
            )
        }.await()

        // Create Notification
        notifRepo.sendNotification(
            Notification(
                userId = otherId,
                title = "New Message",
                message = "$senderName: $content",
                type = "Message",
                senderName = senderName,
                relatedId = chatId
            )
        )
    }

    suspend fun getOrCreateChat(myId: String, myName: String, otherId: String, otherName: String): String {
        val chatQuery = chatsCollection
            .whereArrayContains("participantIds", myId)
            .get().await()

        val existingChat = chatQuery.documents.find { doc ->
            val participants = doc.get("participantIds") as? List<*>
            participants?.contains(otherId) == true
        }

        if (existingChat != null) {
            return existingChat.id
        }

        val newChatDoc = chatsCollection.document()
        val newChat = Chat(
            id = newChatDoc.id,
            participantIds = listOf(myId, otherId),
            participantNames = mapOf(myId to myName, otherId to otherName),
            lastMessage = "No messages yet",
            lastTimestamp = Timestamp.now()
        )
        newChatDoc.set(newChat).await()
        return newChatDoc.id
    }
}
