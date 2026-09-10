package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostItemScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {

    var itemName by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var dateLost by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    // Category Selection State
    val categories = listOf("Bags", "Electronics", "Documents", "Personal Items", "Others")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var otherCategory by remember { mutableStateOf("") }

    var localErrorMessage by remember {
        mutableStateOf<String?>(null)
    }

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
                        dateLost = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCEL")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(Unit) {
        itemViewModel.clearError()
    }

    Scaffold(

        containerColor = MaterialTheme.colorScheme.background,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "Report Lost Item",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Tell us what you lost",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            fontSize = 13.sp
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

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // TITLE

            Text(
                text = "Lost Item Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Please provide details about the item you lost.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // ITEM NAME


            OutlinedTextField(

                value = itemName,

                onValueChange = {
                    itemName = it
                },

                modifier = Modifier.fillMaxWidth(),


                textStyle = androidx.compose.ui.text.TextStyle(
                    color = MaterialTheme.colorScheme.onBackground
                ),

                label = {
                    Text("Item Name")
                },

                placeholder = {
                    Text("e.g. Black Backpack")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = "Item"
                    )
                },

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
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Category, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
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
                            onClick = {
                                selectedCategory = selectionOption
                                expanded = false
                            },
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
                    label = { Text("Please specify category") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // LOCATION


            OutlinedTextField(

                value = location,

                onValueChange = {
                    location = it
                },

                modifier = Modifier.fillMaxWidth(),


                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black
                ),

                label = {
                    Text("Location Lost")
                },

                placeholder = {
                    Text("e.g. Library")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location"
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(12.dp)
            )

            // DATE


            OutlinedTextField(

                value = dateLost,

                onValueChange = {
                    dateLost = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },

                readOnly = true,
                enabled = false, // Disabled to prevent keyboard, but clickable handles the touch

                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black
                ),

                label = {
                    Text("Date Lost")
                },

                placeholder = {
                    Text("e.g. September 3, 2026")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date"
                    )
                },

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


            // DESCRIPTION

            OutlinedTextField(

                value = description,

                onValueChange = {
                    description = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),

                textStyle = androidx.compose.ui.text.TextStyle(
                    color = MaterialTheme.colorScheme.onBackground
                ),

                label = {
                    Text("Description")
                },

                placeholder = {
                    Text(
                        "Describe the item, color, brand, " +
                                "or anything that can help identify it."
                    )
                },

                singleLine = false,

                maxLines = 5,

                shape = RoundedCornerShape(12.dp)
            )

            // PHOTO

            Text(
                text = "Item Photo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(

                onClick = {

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),

                shape = RoundedCornerShape(12.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "Add Photo",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            val displayError = localErrorMessage ?: firebaseError

            displayError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            // SUBMIT BUTTON


            Button(

                onClick = {
                    val finalCategory = if (selectedCategory == "Others") otherCategory else selectedCategory
                    val user = authViewModel.userData.value
                    if (user != null) {
                        itemViewModel.reportItem(
                            name = itemName,
                            category = finalCategory,
                            location = location,
                            date = dateLost,
                            description = description,
                            type = "Lost",
                            userId = user.uid,
                            userName = user.fullName,
                            onSuccess = onSubmitClick
                        )
                    } else {
                        localErrorMessage = "User data not loaded"
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),

                shape = RoundedCornerShape(12.dp),

                enabled = !isLoading && itemName.isNotBlank() &&
                        selectedCategory.isNotBlank() &&
                        (selectedCategory != "Others" || otherCategory.isNotBlank()) &&
                        location.isNotBlank() &&
                        dateLost.isNotBlank()
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SUBMIT LOST ITEM",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}