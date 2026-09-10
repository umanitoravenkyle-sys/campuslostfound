package com.example.campuslostfound

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslostfound.model.Chat
import com.example.campuslostfound.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _currentChatId = mutableStateOf<String?>(null)
    val currentChatId: State<String?> = _currentChatId

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun fetchChats(userId: String) {
        viewModelScope.launch {
            repository.getChats(userId).collectLatest { chatList ->
                _chats.value = chatList
            }
        }
    }

    fun fetchMessages(chatId: String) {
        _currentChatId.value = chatId
        viewModelScope.launch {
            repository.getMessages(chatId).collectLatest { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun startChat(myId: String, myName: String, otherId: String, otherName: String, onReady: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val chatId = repository.getOrCreateChat(myId, myName, otherId, otherName)
            _currentChatId.value = chatId
            _isLoading.value = false
            onReady(chatId)
        }
    }

    fun sendMessage(senderId: String, content: String) {
        val chatId = _currentChatId.value ?: return
        if (content.isBlank()) return
        
        viewModelScope.launch {
            repository.sendMessage(chatId, senderId, content)
        }
    }
}
