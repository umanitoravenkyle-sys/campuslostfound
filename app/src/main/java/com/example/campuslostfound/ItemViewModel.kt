package com.example.campuslostfound

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslostfound.model.Item
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {
    private val repository = ItemRepository()

    private val _lostItems = MutableStateFlow<List<Item>>(emptyList())
    val lostItems: StateFlow<List<Item>> = _lostItems

    private val _foundItems = MutableStateFlow<List<Item>>(emptyList())
    val foundItems: StateFlow<List<Item>> = _foundItems

    private val _myReports = MutableStateFlow<List<Item>>(emptyList())
    val myReports: StateFlow<List<Item>> = _myReports

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        fetchLostItems()
        fetchFoundItems()
    }

    private fun fetchLostItems() {
        viewModelScope.launch {
            repository.getItems("Lost").collectLatest { items ->
                _lostItems.value = items
            }
        }
    }

    private fun fetchFoundItems() {
        viewModelScope.launch {
            repository.getItems("Found").collectLatest { items ->
                _foundItems.value = items
            }
        }
    }

    fun fetchMyReports(userId: String) {
        viewModelScope.launch {
            repository.getMyReports(userId).collectLatest { items ->
                _myReports.value = items
            }
        }
    }

    fun reportItem(
        name: String,
        category: String,
        location: String,
        date: String,
        description: String,
        type: String,
        userId: String,
        userName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val item = Item(
                name = name,
                category = category,
                location = location,
                date = date,
                description = description,
                type = type,
                reportedBy = userId,
                reporterName = userName,
                timestamp = Timestamp.now()
            )

            val result = repository.reportItem(item)
            _isLoading.value = false
            
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to report item"
            }
        }
    }

    fun getItemById(itemId: String): Item? {
        return _lostItems.value.find { it.id == itemId }
            ?: _foundItems.value.find { it.id == itemId }
            ?: _myReports.value.find { it.id == itemId }
    }

    fun resolveItem(itemId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.resolveItem(itemId)
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
