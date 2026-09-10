package com.example.campuslostfound

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

import com.example.campuslostfound.ui.theme.PLMUNGreen
import com.example.campuslostfound.ui.theme.PLMUNGreenLight

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    notifViewModel: NotificationViewModel,
    onSearchClick: () -> Unit = {},
    onLostItemsClick: () -> Unit = {},
    onFoundItemsClick: () -> Unit = {},
    onReportLostClick: () -> Unit = {},
    onReportFoundClick: () -> Unit = {},
    onMyReportsClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onMapClick: () -> Unit = {}, // New
    onItemClick: (String) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // New Feature States
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showMapDialog by remember { mutableStateOf(false) }
    var showLeaderboardDialog by remember { mutableStateOf(false) }
    var showDraftsDialog by remember { mutableStateOf(false) }
    var showQRDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    authViewModel.signOut()
                    onLogoutClick()
                }) { Text("LOGOUT", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("CANCEL") } }
        )
    }

    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = { Text("Campus Security") },
            text = {
                Column {
                    Text("Physical items should be surrendered to the Security Office at the Main Gate.")
                    Spacer(Modifier.height(8.dp))
                    Text("Office Hours: 8:00 AM - 5:00 PM", fontSize = 13.sp)
                    Text("Contact: (02) 8861-1234", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = { TextButton(onClick = { showSecurityDialog = false }) { Text("OK") } }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("How it Works") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Report items you lost or found.")
                    Text("2. Chat with finders/owners.")
                    Text("3. Meet at the Security Office for safety.")
                    Text("4. Mark reports as 'Resolved' once returned.")
                }
            },
            confirmButton = { TextButton(onClick = { showHelpDialog = false }) { Text("GOT IT") } }
        )
    }

    val context = LocalContext.current

    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            title = { Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("University Map")
            }},
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("📍 Security Office - Main Gate")
                    Text("📍 Admin Hub - Library Bldg")
                    
                    Spacer(Modifier.height(10.dp))
                    
                    // Display actual map image if available, otherwise placeholder
                    Card(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plmunlogo), // Replace with R.drawable.campus_map later
                            contentDescription = "Campus Map",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Inside,
                            alpha = 0.3f // placeholder look
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:14.3916,121.0450?q=Pamantasan+ng+Lungsod+ng+Muntinlupa"))
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("OPEN IN GOOGLE MAPS")
                    }

                    Button(
                        onClick = {
                            showMapDialog = false
                            onMapClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("VIEW LIVE IN-APP MAP")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMapDialog = false }) { Text("CLOSE") } }
        )
    }

    if (showLeaderboardDialog) {
        AlertDialog(
            onDismissRequest = { showLeaderboardDialog = false },
            title = { Text("🏆 Top Returners") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Current Community Heroes:")
                    Text("🥇 Raven Kyle - 12 items returned")
                    Text("🥈 Mark Anthony - 8 items returned")
                    Text("🥉 Charles Green - 5 items returned")
                }
            },
            confirmButton = { TextButton(onClick = { showLeaderboardDialog = false }) { Text("AWESOME") } }
        )
    }

    if (showQRDialog) {
        AlertDialog(
            onDismissRequest = { showQRDialog = false },
            title = { Text("Return QR Code") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Show this QR to the finder to confirm your item was returned safely.", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(140.dp), tint = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = { TextButton(onClick = { showQRDialog = false }) { Text("DONE") } }
        )
    }

    if (showDraftsDialog) {
        AlertDialog(
            onDismissRequest = { showDraftsDialog = false },
            title = { Text("My Drafts") },
            text = { Text("You currently have 0 saved drafts. You can save reports here to finish them later.") },
            confirmButton = { TextButton(onClick = { showDraftsDialog = false }) { Text("OK") } }
        )
    }

    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val userData = authViewModel.userData.value
    val userName = userData?.fullName?.split(" ")?.firstOrNull() ?: "Student"

    LaunchedEffect(userData) {
        userData?.uid?.let { notifViewModel.fetchNotifications(it) }
    }

    val lostItems by itemViewModel.lostItems.collectAsState()
    val foundItems by itemViewModel.foundItems.collectAsState()
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
            Report(id = item.id, name = item.name, location = item.location, status = item.type)
        }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(24.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.plmunlogo),
                            contentDescription = null,
                            modifier = Modifier.size(65.dp).clip(CircleShape).background(Color.White).padding(4.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(userData?.fullName ?: "User", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(userData?.email ?: "student@plmun.edu.ph", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text("University Map") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showMapDialog = true 
                    },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Top Returners") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showLeaderboardDialog = true 
                    },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = { Text("My Drafts") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showDraftsDialog = true 
                    },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Return QR Code") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showQRDialog = true 
                    },
                    icon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Campus Security") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showSecurityDialog = true 
                    },
                    icon = { Icon(Icons.Default.ContactPhone, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = { Text("How it Works") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showHelpDialog = true 
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                NavigationDrawerItem(
                    label = { Text("Log Out", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        showLogoutDialog = true 
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.padding(12.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Campus Lost & Found", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Find • Report • Return", color = Color.White, fontSize = 13.sp)
                    }

                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                                        Text(unreadCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PLMUNGreen,
                        selectedTextColor = PLMUNGreen,
                        indicatorColor = PLMUNGreenLight,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )

                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onMyReportsClick,
                        icon = { Icon(Icons.Default.List, contentDescription = "My Reports") },
                        label = { Text("My Reports") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onChatClick,
                        icon = {
                            BadgedBox(
                                badge = {
                                    val messageNotifs = notifications.count { !it.seen && it.type == "Message" }
                                    if (messageNotifs > 0) {
                                        Badge(containerColor = Color.Red) { Text(messageNotifs.toString(), color = Color.White) }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages")
                            }
                        },
                        label = { Text("Messages") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onProfileClick,
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = navItemColors
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Hello, $userName! 👋", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "How can we help you today?", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(text = "Search lost or found items...") },
                        leadingIcon = {
                            IconButton(onClick = { if (searchText.isNotBlank()) onSearchClick() }) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = onSearchClick) {
                                Icon(imageVector = Icons.Default.Tune, contentDescription = "Filter")
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus(); onSearchClick() }),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                item {
                    Text(text = "Browse Items", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BrowseCard(
                        title = "Lost Items",
                        description = "View all lost items",
                        icon = Icons.Default.Warning,
                        iconBackground = Color.White.copy(alpha = 0.6f),
                        iconColor = Color(0xFFB71C1C),
                        containerColor = Color(0xFFFFEBEE),
                        onClick = onLostItemsClick,
                        modifier = Modifier.weight(1f)
                    )
                    BrowseCard(
                        title = "Found Items",
                        description = "View all found items",
                        icon = Icons.Default.Inventory2,
                        iconBackground = Color.White.copy(alpha = 0.6f),
                        iconColor = Color(0xFF0D47A1),
                        containerColor = Color(0xFFE3F2FD),
                        onClick = onFoundItemsClick,
                        modifier = Modifier.weight(1f)
                    )
                    }
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    Text(text = "Quick Actions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }

                item {
                    ActionCard(
                        title = "Report Lost Item",
                        description = "Let others know what you lost",
                        icon = Icons.Default.Warning,
                        iconColor = MaterialTheme.colorScheme.secondary,
                        iconBackground = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = onReportLostClick
                    )
                }

                item {
                    ActionCard(
                        title = "Report Found Item",
                        description = "Help return lost items",
                        icon = Icons.Default.Add,
                        iconColor = MaterialTheme.colorScheme.primary,
                        iconBackground = MaterialTheme.colorScheme.primaryContainer,
                        onClick = onReportFoundClick
                    )
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Recent Reports", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                        Text(text = "View all  ›", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onMyReportsClick() })
                    }
                }

                items(recentReports) { report ->
                    RecentReportCard(report = report, onClick = { onItemClick(report.id) })
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun BrowseCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBackground: Color,
    iconColor: Color,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.size(55.dp).background(iconBackground, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = iconColor)
            Spacer(Modifier.height(4.dp))
            Text(text = description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Text(text = "›", fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
fun ActionCard(title: String, description: String, icon: ImageVector, iconColor: Color, iconBackground: Color, onClick: () -> Unit = {}) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).background(iconBackground, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(15.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Text(text = "›", fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

data class Report(val id: String = "", val name: String, val location: String, val status: String)

@Composable
fun RecentReportCard(report: Report, onClick: () -> Unit) {
    val backgroundColor = if (report.status == "Lost") Color(0xFFFFEBEE) else Color(0xFFE3F2FD)
    val contentColor = if (report.status == "Lost") Color(0xFFB71C1C) else Color(0xFF0D47A1)
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(65.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, tint = contentColor, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "📍 ${report.location}", color = Color.DarkGray, fontSize = 13.sp)
                Text(text = "📅 Recent report", color = Color.Gray, fontSize = 12.sp)
            }
            Box(modifier = Modifier.background(contentColor, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 7.dp)) {
                Text(text = report.status, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
