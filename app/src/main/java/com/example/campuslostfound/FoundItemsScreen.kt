package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.campuslostfound.model.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemsScreen(
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    itemViewModel: ItemViewModel,
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onMessageClick: (String, String) -> Unit = { _, _ -> }
) {
    var searchText by remember { mutableStateOf("") }
    val foundItems by itemViewModel.foundItems.collectAsState()
    val filteredItems = foundItems.filter { item ->
        item.name.contains(searchText, ignoreCase = true) ||
                item.category.contains(searchText, ignoreCase = true) ||
                item.location.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Found Items", color = MaterialTheme.colorScheme.onPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = "View items that have been found", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), fontSize = 13.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(text = "Search Found Items", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Search found items...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = { Icon(imageVector = Icons.Default.Tune, contentDescription = "Filter") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "${filteredItems.size} found item(s)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
            }
            items(filteredItems) { item ->
                FoundItemCard(
                    item = item,
                    onClick = { onItemClick(item.id) },
                    onMessageClick = {
                        val currentUser = authViewModel.userData.value
                        if (currentUser != null && item.reportedBy != currentUser.uid) {
                            chatViewModel.startChat(currentUser.uid, currentUser.fullName, item.reportedBy, item.reporterName) { onMessageClick(it, item.reporterName) }
                        }
                    },
                    isMine = item.reportedBy == authViewModel.userData.value?.uid
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun FoundItemCard(item: Item, onClick: () -> Unit, onMessageClick: () -> Unit, isMine: Boolean) {
    val backgroundColor = Color(0xFFE3F2FD)
    val contentColor = Color(0xFF0D47A1)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.imageUrl != null) {
                        AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = contentColor, modifier = Modifier.size(40.dp))
                    }
                }
                Spacer(modifier = Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.category, fontSize = 13.sp, color = contentColor, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = item.description, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = item.location, fontSize = 13.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = item.date, fontSize = 12.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)) {
                    Text(text = "DETAILS", fontWeight = FontWeight.Bold)
                }
                if (!isMine) {
                    Button(onClick = onMessageClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = contentColor)) {
                        Text(text = "MESSAGE", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
