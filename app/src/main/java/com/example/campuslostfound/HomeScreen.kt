package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    notifViewModel: NotificationViewModel, // Added for badge
    onSearchClick: () -> Unit = {},
    onLostItemsClick: () -> Unit = {},
    onFoundItemsClick: () -> Unit = {},
    onReportLostClick: () -> Unit = {},
    onReportFoundClick: () -> Unit = {},
    onMyReportsClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}, // New parameter
    onProfileClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {

    var searchText by remember {
        mutableStateOf("")
    }
    
    val focusManager = LocalFocusManager.current

    val userData = authViewModel.userData.value
    val userName = userData?.fullName?.split(" ")?.firstOrNull() ?: "Student"

    LaunchedEffect(userData) {
        userData?.uid?.let { notifViewModel.fetchNotifications(it) }
    }

    val lostItems by itemViewModel.lostItems.collectAsState()
    val foundItems by itemViewModel.foundItems.collectAsState()
    
    // Notification Badge logic
    val notifications by notifViewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.seen }

    val recentReports = (lostItems + foundItems)
        .filter { 
            it.name.contains(searchText, ignoreCase = true) || 
            it.category.contains(searchText, ignoreCase = true) ||
            it.location.contains(searchText, ignoreCase = true)
        }
        .sortedByDescending { it.timestamp }
        .take(5)
        .map { item ->
            Report(
                id = item.id,
                name = item.name,
                location = item.location,
                status = item.type
            )
        }

    Scaffold(

        // WHITE BACKGROUND


        containerColor = MaterialTheme.colorScheme.background,

        // TOP BAR


        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 26.dp,
                        bottom = 14.dp
                    ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {}
                ) {

                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Campus Lost & Found",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Find • Report • Return",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onNotificationsClick
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(unreadCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },

        // BOTTOM NAVIGATION


        bottomBar = {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},

                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },

                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onSearchClick,

                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },

                    label = {
                        Text("Search")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onMyReportsClick,

                    icon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "My Reports"
                        )
                    },

                    label = {
                        Text("My Reports")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onChatClick,

                    icon = {
                        BadgedBox(
                            badge = {
                                val messageNotifs = notifications.count { !it.seen && it.type == "Message" }
                                if (messageNotifs > 0) {
                                    Badge { Text(messageNotifs.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Messages"
                            )
                        }
                    },

                    label = {
                        Text("Messages")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,

                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },

                    label = {
                        Text("Profile")
                    }
                )
            }
        }

    ) { innerPadding ->


        // MAIN CONTENT


        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // GREETING


            item {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Hello, $userName! 👋",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "How can we help you today?",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {

                OutlinedTextField(

                    value = searchText,

                    onValueChange = {
                        searchText = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    placeholder = {
                        Text(
                            text = "Search lost or found items..."
                        )
                    },

                    leadingIcon = {

                        IconButton(onClick = { 
                            if (searchText.isNotBlank()) onSearchClick() 
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },

                    trailingIcon = {

                        IconButton(
                            onClick = onSearchClick
                        ) {

                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter"
                            )
                        }
                    },

                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearchClick()
                        }
                    ),

                    shape = RoundedCornerShape(14.dp)
                )
            }


            // BROWSE ITEMS


            item {

                Text(
                    text = "Browse Items",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }


            // LOST / FOUND CARDS


            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    BrowseCard(

                        title = "Lost Items",

                        description = "View all lost items",

                        icon = Icons.Default.Warning,

                        iconBackground =
                            MaterialTheme.colorScheme.errorContainer,

                        iconColor =
                            MaterialTheme.colorScheme.error,

                        onClick = onLostItemsClick,

                        modifier =
                            Modifier.weight(1f)
                    )

                    BrowseCard(

                        title = "Found Items",

                        description = "View all found items",

                        icon = Icons.Default.Inventory2,

                        iconBackground =
                            MaterialTheme.colorScheme.primaryContainer,

                        iconColor =
                            MaterialTheme.colorScheme.primary,

                        onClick = onFoundItemsClick,

                        modifier =
                            Modifier.weight(1f)
                    )
                }
            }


            // QUICK ACTIONS TITLE


            item {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }


            // REPORT LOST ITEM


            item {

                ActionCard(

                    title = "Report Lost Item",

                    description =
                        "Let others know what you lost",

                    icon =
                        Icons.Default.Warning,

                    iconColor =
                        MaterialTheme.colorScheme.secondary,

                    iconBackground =
                        MaterialTheme.colorScheme.secondaryContainer,

                    onClick = onReportLostClick
                )
            }


            // REPORT FOUND ITEM


            item {

                ActionCard(

                    title = "Report Found Item",

                    description =
                        "Help return lost items",

                    icon =
                        Icons.Default.Add,

                    iconColor =
                        MaterialTheme.colorScheme.primary,

                    iconBackground =
                        MaterialTheme.colorScheme.primaryContainer,

                    onClick = onReportFoundClick
                )
            }


            // RECENT REPORTS TITLE


            item {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text = "Recent Reports",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            MaterialTheme.colorScheme.onBackground,

                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(

                        text = "View all  ›",

                        color =
                            MaterialTheme.colorScheme.primary,

                        fontWeight =
                            FontWeight.Bold,

                        modifier = Modifier.clickable { onMyReportsClick() }
                    )
                }
            }


            items(recentReports) { report ->

                RecentReportCard(
                    report = report,
                    onClick = { onItemClick(report.id) }
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



// BROWSE CARD


@Composable
fun BrowseCard(

    title: String,

    description: String,

    icon: ImageVector,

    iconBackground: Color,

    iconColor: Color,

    onClick: () -> Unit = {},

    modifier: Modifier = Modifier

) {

    Card(

        modifier = modifier
            .height(190.dp)
            .clickable { onClick() },

        shape =
            RoundedCornerShape(18.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // ICON

            Box(

                modifier = Modifier
                    .size(55.dp)
                    .background(
                        iconBackground,
                        RoundedCornerShape(50)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = iconColor,

                    modifier =
                        Modifier.size(30.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // TITLE

            Text(

                text = title,

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.Bold,

                color = iconColor
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            // DESCRIPTION

            Text(

                text = description,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

                fontSize = 13.sp
            )

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            // ARROW

            Text(

                text = "›",

                fontSize = 30.sp,

                color = MaterialTheme.colorScheme.onSurfaceVariant,

                modifier =
                    Modifier.align(
                        Alignment.End
                    )
            )
        }
    }
}



// ACTION CARD


@Composable
fun ActionCard(

    title: String,

    description: String,

    icon: ImageVector,

    iconColor: Color,

    iconBackground: Color,

    onClick: () -> Unit = {}

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ICON

            Box(

                modifier = Modifier
                    .size(52.dp)
                    .background(
                        iconBackground,
                        RoundedCornerShape(14.dp)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = iconColor,

                    modifier =
                        Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(15.dp)
            )

            // TEXT

            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(

                    text = description,

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 13.sp
                )
            }

            // ARROW

            Text(

                text = "›",

                fontSize = 28.sp,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



// REPORT DATA


data class Report(

    val id: String = "",

    val name: String,

    val location: String,

    val status: String
)



// RECENT REPORT CARD


@Composable
fun RecentReportCard(

    report: Report,
    onClick: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth().clickable { onClick() },

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ITEM IMAGE PLACEHOLDER

            Box(

                modifier = Modifier
                    .size(65.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        Icons.Default.Inventory2,

                    contentDescription = null,

                    tint = MaterialTheme.colorScheme.onSurfaceVariant,

                    modifier =
                        Modifier.size(30.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            // REPORT INFORMATION

            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text = report.name,

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(

                    text = "📍 ${report.location}",

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 13.sp
                )

                Text(

                    text = "📅 Recent report",

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 12.sp
                )
            }

            // STATUS

            Box(

                modifier = Modifier
                    .background(

                        if (report.status == "Found")
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,

                        RoundedCornerShape(10.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    )
            ) {

                Text(

                    text = report.status,

                    color =

                        if (report.status == "Found")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 12.sp
                )
            }
        }
    }
}