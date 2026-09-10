package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuslostfound.model.Chat
import com.example.campuslostfound.model.Notification
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    notifViewModel: NotificationViewModel, // New
    onBackClick: () -> Unit,
    onChatClick: (String, String) -> Unit,
    onStartChattingClick: () -> Unit // New
) {
    val user = authViewModel.userData.value
    val chats by chatViewModel.chats.collectAsState()
    val notifications by notifViewModel.notifications.collectAsState()

    LaunchedEffect(user) {
        user?.uid?.let { 
            chatViewModel.fetchChats(it)
            notifViewModel.fetchNotifications(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (chats.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "No messages yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    "Start a conversation with someone who found an item!",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onStartChattingClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("BROWSE ITEMS")
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(chats) { chat ->
                    val otherId = chat.participantIds.find { it != user?.uid } ?: ""
                    // Try to get name from map, or from a notification if map is empty (old chats)
                    val nameFromMap = chat.participantNames[otherId]
                    val nameFromNotif = notifications.find { it.relatedId == chat.id }?.senderName
                    val otherName = nameFromMap ?: nameFromNotif ?: "User"
                    
                    val hasUnread = notifications.any { !it.seen && it.relatedId == chat.id && it.type == "Message" }
                    
                    ChatRow(chat, otherName, hasUnread) {
                        onChatClick(chat.id, otherName)
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun ChatRow(chat: Chat, otherName: String, hasUnread: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgedBox(
            badge = {
                if (hasUnread) {
                    Badge(containerColor = Color.Red)
                }
            }
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        
        Spacer(Modifier.width(12.dp))
        
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    otherName, 
                    fontWeight = if (hasUnread) FontWeight.ExtraBold else FontWeight.Bold, 
                    fontSize = 16.sp
                )
                val date = chat.lastTimestamp.toDate()
                val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                Text(
                    format.format(date), 
                    fontSize = 12.sp, 
                    color = if (hasUnread) Color.Red else Color.Gray
                )
            }
            Text(
                chat.lastMessage,
                fontSize = 14.sp,
                color = if (hasUnread) MaterialTheme.colorScheme.onBackground else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
