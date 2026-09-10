package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuslostfound.model.Notification
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    authViewModel: AuthViewModel,
    viewModel: NotificationViewModel,
    onBackClick: () -> Unit = {},
    onNotificationClick: (String, String?, String?) -> Unit = { _, _, _ -> }
) {
    val user = authViewModel.userData.value
    val notifications by viewModel.notifications.collectAsState()

    LaunchedEffect(user) {
        user?.uid?.let { viewModel.fetchNotifications(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Notifications",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your recent activity",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            TextButton(
                onClick = { user?.uid?.let { viewModel.markAllAsRead(it) } }
            ) {
                Text("Mark all read", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
            }
        }

        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                items(notifications) { notif ->
                    NotificationRow(
                        notif = notif,
                        onClick = {
                            viewModel.markAsRead(notif.id)
                            onNotificationClick(notif.type, notif.relatedId, notif.senderName)
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notif: Notification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (notif.seen) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (notif.type == "Message") Icons.Default.Chat else Icons.Default.Notifications,
                contentDescription = null,
                tint = if (notif.seen) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = if (notif.seen) FontWeight.Normal else FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = notif.message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                maxLines = 2
            )
            
            val date = notif.timestamp.toDate()
            val format = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
            Text(
                text = format.format(date),
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        if (!notif.seen) {
            Box(
                modifier = Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color.Red)
            )
        }
    }
}
