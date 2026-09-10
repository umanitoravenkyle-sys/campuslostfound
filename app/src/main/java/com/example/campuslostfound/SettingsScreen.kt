package com.example.campuslostfound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// SETTINGS SCREEN
// Same header/body theme as ProfileScreen: solid green header, white body.
// ---------------------------------------------------------------------------
@Composable
fun SettingsScreen(
    viewModel: AuthViewModel,
    onThemeChange: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var darkMode by remember { mutableStateOf(viewModel.isDarkMode()) }
    var selectedLanguage by remember { mutableStateOf(viewModel.getLanguage()) }
    
    // Dialog States
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var changePasswordMessage by remember { mutableStateOf<String?>(null) }
    var isChangeLoading by remember { mutableStateOf(false) }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showChangePasswordDialog = false
                changePasswordMessage = null
                newPassword = ""
                confirmPassword = ""
                newPasswordVisible = false
                confirmPasswordVisible = false
            },
            title = { Text("Change Password") },
            text = {
                Column {
                    Text("Enter your new password below.")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("New Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    changePasswordMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = if (it.contains("success", true)) Color(0xFF4CAF50) else Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPassword != confirmPassword) {
                            changePasswordMessage = "Passwords do not match."
                            return@TextButton
                        }
                        if (newPassword.length < 6) {
                            changePasswordMessage = "Password must be at least 6 characters."
                            return@TextButton
                        }
                        
                        isChangeLoading = true
                        viewModel.changePassword(newPassword) { success, msg ->
                            isChangeLoading = false
                            changePasswordMessage = msg
                            if (success) {
                                // Close the dialog automatically on success
                                showChangePasswordDialog = false
                                newPassword = ""
                                confirmPassword = ""
                                newPasswordVisible = false
                                confirmPasswordVisible = false
                                changePasswordMessage = null
                            }
                        }
                    },
                    enabled = !isChangeLoading && newPassword.isNotBlank() && confirmPassword.isNotBlank()
                ) {
                    if (isChangeLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("UPDATE", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showChangePasswordDialog = false
                    changePasswordMessage = null
                    newPassword = ""
                    confirmPassword = ""
                }) {
                    Text("CANCEL")
                }
            }
        )
    }

    val languages = listOf(
        "English (US)",
        "Tagalog",
        "Spanish (Español)",
        "Chinese (中文)",
        "Japanese (日本語)",
        "Korean (한국어)",
        "French (Français)",
        "German (Deutsch)"
    )

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (lang == selectedLanguage),
                                onClick = {
                                    selectedLanguage = lang
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(lang)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("CANCEL") }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = { 
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Your privacy is important to us. PLMUN Lost & Found collects your name, student ID, and email to facilitate item recovery.")
                    Spacer(Modifier.height(8.dp))
                    Text("Data is stored securely in Google Firebase and is only used within the app for communication between students.")
                }
            },
            confirmButton = { TextButton(onClick = { showPrivacyDialog = false }) { Text("OK") } }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("1. Be honest: Do not report fake items.")
                    Text("2. Respect others: Use the chat only for item recovery.")
                    Text("3. Safety: Meet in public places or the Security Office.")
                }
            },
            confirmButton = { TextButton(onClick = { showTermsDialog = false }) { Text("AGREE") } }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support") },
            text = {
                Column {
                    Text("Need help? Contact the PLMUN App Support Team.")
                    Spacer(Modifier.height(8.dp))
                    Text("Email: support.lostfound@plmun.edu.ph", fontWeight = FontWeight.Bold)
                    Text("Location: IT Department, 3rd Floor")
                }
            },
            confirmButton = { TextButton(onClick = { showHelpDialog = false }) { Text("CLOSE") } }
        )
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
                    text = "Settings",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "App preferences",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Dark Mode — toggle row
            SettingsToggleRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Switch between light and dark theme",
                checked = darkMode,
                onCheckedChange = { 
                    darkMode = it
                    viewModel.setDarkMode(it)
                    onThemeChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // Language — navigation row
            SettingsNavRow(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = selectedLanguage,
                onClick = { showLanguageDialog = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            SettingsNavRow(
                icon = Icons.Default.Lock,
                title = "Change Password",
                subtitle = "Update your account security",
                onClick = { showChangePasswordDialog = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            SettingsNavRow(
                icon = Icons.Default.Lock,
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = { showPrivacyDialog = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            SettingsNavRow(
                icon = Icons.Default.Description,
                title = "Terms of Service",
                subtitle = "Rules for using this app",
                onClick = { showTermsDialog = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            SettingsNavRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Help & Support",
                subtitle = "Get help or send feedback",
                onClick = { showHelpDialog = true }
            )
        }
    }
}

@Composable
private fun SettingsIconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBox(icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBox(icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

