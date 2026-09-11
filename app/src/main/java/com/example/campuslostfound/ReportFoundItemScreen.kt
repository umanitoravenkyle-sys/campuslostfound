package com.example.campuslostfound

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    val context = LocalContext.current
    var itemName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateFound by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Category Selection State
    val categories = listOf("Bags", "Electronics", "Documents", "Personal Items", "Others")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var otherCategory by remember { mutableStateOf("") }

    // Photo State
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    val isLoading = itemViewModel.isLoading.value
    val firebaseError = itemViewModel.errorMessage.value

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                        dateFound = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("CANCEL") } }
        ) { DatePicker(state = datePickerState) }
    }

    LaunchedEffect(Unit) { itemViewModel.clearError() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Report Found Item", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        Text(text = "Help return an item", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), fontSize = 13.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Found Item Information", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Please provide details about the item you found.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
                label = { Text("Item Name") },
                placeholder = { Text("e.g. Black Wallet") },
                leadingIcon = { Icon(imageVector = Icons.Default.Category, contentDescription = "Item") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // CATEGORY
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = { selectedCategory = selectionOption; expanded = false },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            if (selectedCategory == "Others") {
                OutlinedTextField(
                    value = otherCategory,
                    onValueChange = { otherCategory = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    label = { Text("Please specify category") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
                label = { Text("Location Found") },
                placeholder = { Text("e.g. Library") },
                leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = dateFound,
                onValueChange = { dateFound = it },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                readOnly = true,
                enabled = false,
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
                label = { Text("Date Found") },
                placeholder = { Text("e.g. September 3, 2026") },
                leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Date") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
                label = { Text("Description") },
                placeholder = { Text("Describe the item, color, brand, or anything that can help identify it.") },
                singleLine = false,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            // PHOTO SECTION
            Text(text = "Item Photo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            
            if (imageUri != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { imageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) { Icon(Icons.Default.Close, contentDescription = "Remove Photo", tint = Color.White) }
                }
            } else {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Add Photo", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val displayError = localErrorMessage ?: firebaseError
            displayError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }

            Button(
                onClick = {
                    val finalCategory = if (selectedCategory == "Others") otherCategory else selectedCategory
                    val user = authViewModel.userData.value
                    if (user != null) {
                        itemViewModel.reportItem(
                            name = itemName,
                            category = finalCategory,
                            location = location,
                            date = dateFound,
                            description = description,
                            type = "Found",
                            userId = user.uid,
                            userName = user.fullName,
                            imageUri = imageUri,
                            context = context,
                            onSuccess = onSubmitClick
                        )
                    } else { localErrorMessage = "User data not loaded" }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && itemName.isNotBlank() && selectedCategory.isNotBlank() && (selectedCategory != "Others" || otherCategory.isNotBlank()) && location.isNotBlank() && dateFound.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(text = "SUBMIT FOUND ITEM", color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
