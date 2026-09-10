package com.example.campuslostfound

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onCreateAccountClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
) {

    var fullName by remember {
        mutableStateOf(value = "")
    }

    var studentId by remember {
        mutableStateOf(value = "")
    }

    var password by remember {
        mutableStateOf(value = "")
    }

    var confirmPassword by remember {
        mutableStateOf(value = "")
    }

    var passwordVisible by remember {
        mutableStateOf(value = false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(value = false)
    }

    var localErrorMessage by remember {
        mutableStateOf<String?>(value = null)
    }

    val isLoading = viewModel.isLoading.value
    val firebaseError = viewModel.errorMessage.value

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 35.dp
                    ),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.plmunlogo
                    ),

                    contentDescription = "PLMUN Logo",

                    modifier = Modifier.size(190.dp),

                    contentScale = ContentScale.Fit
                )


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                Text(
                    text = "Campus Lost & Found",

                    color = MaterialTheme.colorScheme.primary,

                    fontSize = 28.sp,

                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                Text(
                    text = "Join us to report and recover lost items.",

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 14.sp
                )


                Spacer(
                    modifier = Modifier.height(40.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,

                        contentDescription = "Create Account",

                        tint = MaterialTheme.colorScheme.primary,

                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "Create New Account",

                        color = MaterialTheme.colorScheme.primary,

                        fontSize = 20.sp,

                        fontWeight = FontWeight.Bold
                    )
                }


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                OutlinedTextField(

                    value = fullName,

                    onValueChange = {
                        fullName = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Full Name")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Person,

                            contentDescription = "Full Name",

                            tint = MaterialTheme.colorScheme.primary
                        )
                    },

                    shape = RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                OutlinedTextField(

                    value = studentId,

                    onValueChange = {
                        studentId = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Email or Student ID")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Email,

                            contentDescription = "Email",

                            tint = MaterialTheme.colorScheme.primary
                        )
                    },

                    shape = RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                OutlinedTextField(

                    value = password,

                    onValueChange = {
                        password = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Password")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Lock,

                            contentDescription = "Password",

                            tint = MaterialTheme.colorScheme.primary
                        )
                    },

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {

                            Icon(

                                imageVector =
                                    if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },

                                contentDescription =
                                    if (passwordVisible) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    }
                            )
                        }
                    },

                    visualTransformation =
                        if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },

                    shape = RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                OutlinedTextField(

                    value = confirmPassword,

                    onValueChange = {
                        confirmPassword = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("Confirm Password")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Lock,

                            contentDescription = "Confirm Password",

                            tint = MaterialTheme.colorScheme.primary
                        )
                    },

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }
                        ) {

                            Icon(

                                imageVector =
                                    if (confirmPasswordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },

                                contentDescription =
                                    if (confirmPasswordVisible) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    }
                            )
                        }
                    },

                    visualTransformation =
                        if (confirmPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },

                    shape = RoundedCornerShape(12.dp)
                )


                val displayError = localErrorMessage ?: firebaseError

                displayError?.let {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = it,

                        color = MaterialTheme.colorScheme.error,

                        fontSize = 13.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                Button(

                    onClick = {

                        localErrorMessage = when {
                            fullName.isBlank() || studentId.isBlank() || password.isBlank() ->
                                "Please fill in all fields."

                            // Email format validation
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(studentId).matches() ->
                                "Please enter a valid email address."

                            password != confirmPassword ->
                                "Passwords do not match."

                            password.length < 6 ->
                                "Password must be at least 6 characters."

                            else -> null
                        }

                        if (localErrorMessage == null) {
                            viewModel.signUp(
                                email = studentId, // Assuming studentId field is used for email
                                password = password,
                                fullName = fullName,
                                studentId = studentId,
                                onSuccess = onCreateAccountClick
                            )
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    enabled = !isLoading,

                    shape = RoundedCornerShape(10.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "CREATE ACCOUNT",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(28.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )


                    Text(
                        text = "  OR  ",

                        color = MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 16.sp
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }


                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                OutlinedButton(

                    onClick = onBackToLoginClick,

                    modifier = Modifier
                        .width(280.dp)
                        .height(55.dp),

                    shape = RoundedCornerShape(10.dp)
                ) {

                    Icon(

                        imageVector = Icons.Default.Person,

                        contentDescription =
                            "Back to Login",

                        tint = MaterialTheme.colorScheme.primary
                    )


                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )


                    Text(

                        text = "Back to Login",

                        color = MaterialTheme.colorScheme.primary,

                        fontSize = 16.sp
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            GreenFooter(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}