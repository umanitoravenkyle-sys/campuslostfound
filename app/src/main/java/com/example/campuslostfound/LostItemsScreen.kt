package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.campuslostfound.model.Item

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LostItemsScreen(
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    itemViewModel: ItemViewModel,
    onBackClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onMessageClick: (String, String) -> Unit = { _, _ -> }
) {

    var searchText by remember {
        mutableStateOf("")
    }

    val lostItems by itemViewModel.lostItems.collectAsState()

    val filteredItems = lostItems.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.category.contains(searchText, ignoreCase = true) ||
                it.location.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lost Items",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Find items reported missing",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Search Lost Items",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search item, category, or location",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "${filteredItems.size} lost items",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            items(filteredItems) { item ->

                LostItemCard(
                    item = item,
                    onClick = {
                        onItemClick(item.id)
                    },
                    onMessageClick = {
                        val currentUser = authViewModel.userData.value
                        if (currentUser != null && item.reportedBy != currentUser.uid) {
                            chatViewModel.startChat(
                                myId = currentUser.uid,
                                myName = currentUser.fullName,
                                otherId = item.reportedBy,
                                otherName = item.reporterName,
                                onReady = { chatId ->
                                    onMessageClick(chatId, item.reporterName)
                                }
                            )
                        }
                    },
                    isMine = item.reportedBy == authViewModel.userData.value?.uid
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
fun LostItemCard(
    item: Item,
    onClick: () -> Unit,
    onMessageClick: () -> Unit,
    isMine: Boolean
) {
    val backgroundColor = Color(0xFFFFEBEE)
    val contentColor = Color(0xFFB71C1C)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.imageUrl != null) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Backpack,
                            contentDescription = "Lost item",
                            tint = contentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.name,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text(
                            text = item.category,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = item.description,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = item.location,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = item.date,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
                ) {
                    Text(
                        text = "DETAILS",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isMine) {
                    Button(
                        onClick = onMessageClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = contentColor)
                    ) {
                        Text(
                            text = "MESSAGE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
