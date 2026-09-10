package com.example.campuslostfound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslostfound.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val repository = NotificationRepository()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    fun fetchNotifications(userId: String) {
        viewModelScope.launch {
            repository.getNotifications(userId).collectLatest { list ->
                _notifications.value = list
            }
        }
    }

    fun markAsRead(id: String) {
        repository.markAsRead(id)
    }

    fun markAllAsRead(userId: String) {
        repository.markAllAsRead(userId)
    }

    fun markChatMessagesAsRead(userId: String, chatId: String) {
        repository.markMessagesAsRead(userId, chatId)
    }
}
