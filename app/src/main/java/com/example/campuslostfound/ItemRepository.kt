package com.example.campuslostfound

import com.example.campuslostfound.model.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ItemRepository {
    private val db = FirebaseFirestore.getInstance()
    private val itemsCollection = db.collection("items")

    fun getItems(type: String? = null): Flow<List<Item>> = callbackFlow {
        var query = itemsCollection.whereEqualTo("status", "Open")
        
        if (type != null) {
            query = query.whereEqualTo("type", type)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // If it fails (e.g. missing index), send an empty list instead of crashing
                trySend(emptyList())
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(Item::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    fun getMyReports(userId: String): Flow<List<Item>> = callbackFlow {
        val listener = itemsCollection.whereEqualTo("reportedBy", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(Item::class.java) ?: emptyList()
                // Sort manually in Kotlin
                val sortedItems = items.sortedByDescending { it.timestamp }
                trySend(sortedItems)
            }
        awaitClose { listener.remove() }
    }

    suspend fun reportItem(item: Item): Result<Unit> {
        return try {
            val docRef = itemsCollection.document()
            val newItem = item.copy(id = docRef.id)
            docRef.set(newItem).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveItem(itemId: String): Result<Unit> {
        return try {
            val itemDoc = itemsCollection.document(itemId).get().await()
            val item = itemDoc.toObject(Item::class.java)
            
            itemsCollection.document(itemId).update("status", "Resolved").await()
            
            // Send notification to the owner/reporter if they are not the one resolving (admins etc, but usually same user)
            // For now, let's just create a system log notification
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
