package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// PERSONAL INFORMATION SCREEN
// Same header/body theme as ProfileScreen: solid green header, white body.
// ---------------------------------------------------------------------------
@Composable
fun PersonalInformationScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val user = viewModel.userData.value

    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") } // Changed val to var
    var studentId by remember { mutableStateOf(user?.studentId ?: "") }
    var course by remember { mutableStateOf(user?.course ?: "") }
    var yearLevel by remember { mutableStateOf(user?.yearLevel ?: "") }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "") }

    val isLoading = viewModel.isLoading.value
    var localErrorMessage by remember { mutableStateOf<String?>(null) } // Added local error state
    val firebaseError = viewModel.errorMessage.value
    val errorMessage = localErrorMessage ?: firebaseError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "Personal Information",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View and edit your information",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(top = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
            
            // Email is usually not editable in Firebase without re-auth, 
            // but I'll make it editable as requested with validation.
            LabeledField(label = "Email", value = email, onValueChange = { email = it })

            LabeledField(label = "Student ID", value = studentId, onValueChange = { studentId = it })
            LabeledField(label = "Course / Program", value = course, onValueChange = { course = it })
            LabeledField(label = "Year Level", value = yearLevel, onValueChange = { yearLevel = it })
            LabeledField(label = "Phone Number", value = phoneNumber, onValueChange = { phoneNumber = it })

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    localErrorMessage = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        "Please enter a valid email address."
                    } else {
                        null
                    }

                    if (localErrorMessage == null) {
                        viewModel.updateProfile(
                            fullName = fullName,
                            studentId = studentId,
                            course = course,
                            yearLevel = yearLevel,
                            phoneNumber = phoneNumber,
                            onSuccess = onSaveClick
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(text = "Save Changes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

