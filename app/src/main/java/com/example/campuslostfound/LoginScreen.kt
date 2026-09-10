package com.example.campuslostfound

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuslostfound.ui.theme.PLMUNGreen

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {


    var studentId by remember {
        mutableStateOf(value = "")
    }

    var password by remember {
        mutableStateOf(value = "")
    }

    var passwordVisible by remember {
        mutableStateOf(value = false)
    }

    var rememberMe by remember {
        mutableStateOf(value = false)
    }

    var localErrorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val isLoading = viewModel.isLoading.value
    val firebaseError = viewModel.errorMessage.value

    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotMessage by remember { mutableStateOf<String?>(null) }
    var isForgotLoading by remember { mutableStateOf(false) }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email address and we'll send you a link to reset your password.")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    forgotMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = if (it.contains("sent", true)) Color(0xFF4CAF50) else Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isForgotLoading = true
                        viewModel.resetPassword(forgotEmail) { success, msg ->
                            isForgotLoading = false
                            forgotMessage = msg
                            if (success) {
                                // Close after a delay? Or just let user close it
                            }
                        }
                    },
                    enabled = !isForgotLoading && forgotEmail.isNotBlank()
                ) {
                    if (isForgotLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("SEND RESET LINK", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showForgotDialog = false
                    forgotMessage = null
                    forgotEmail = ""
                }) {
                    Text("CANCEL")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.clearError()
        // Load saved credentials
        viewModel.getSavedCredentials()?.let { (email, pass) ->
            studentId = email
            password = pass
            rememberMe = true
        }
    }



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(), // Pushes everything up
        color = MaterialTheme.colorScheme.background
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {



            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 35.dp,
                        bottom = 150.dp
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

                    color = PLMUNGreen,

                    fontSize = 28.sp,

                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )



                Text(
                    text = "Find what you've lost. Help return what you've found.",

                    color = Color.Gray,

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

                        contentDescription = "Login",

                        tint = PLMUNGreen,

                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "Login to your account",

                        color = PLMUNGreen,

                        fontSize = 20.sp,

                        fontWeight = FontWeight.Bold
                    )
                }


                Spacer(
                    modifier = Modifier.height(18.dp)
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

                            tint = PLMUNGreen
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

                            tint = PLMUNGreen
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


                Row(
                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Checkbox(

                            checked = rememberMe,

                            onCheckedChange = { isChecked ->
                                rememberMe = isChecked
                                if (isChecked) {
                                    // Auto-fill when checked
                                    viewModel.getSavedCredentials()?.let { (email, pass) ->
                                        studentId = email
                                        password = pass
                                    }
                                } else {
                                    // Clear when unchecked
                                    studentId = ""
                                    password = ""
                                }
                            }
                        )

                        Text(
                            text = "Remember me",

                            fontSize = 14.sp
                        )
                    }


                    TextButton(
                        onClick = { showForgotDialog = true }
                    ) {

                        Text(
                            text = "Forgot Password?",

                            color = PLMUNGreen,

                            fontSize = 14.sp
                        )
                    }
                }


                val displayError = localErrorMessage ?: firebaseError

                displayError?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = it, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                Button(

                    onClick = {
                        localErrorMessage = when {
                            studentId.isBlank() || password.isBlank() -> "Please fill in all fields."
                            else -> null
                        }

                        if (localErrorMessage == null) {
                            viewModel.saveCredentials(studentId, password, rememberMe)
                            viewModel.signIn(
                                email = studentId,
                                password = password,
                                onSuccess = onLoginClick
                            )
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    enabled = !isLoading,

                    shape = RoundedCornerShape(10.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = PLMUNGreen
                    )
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "LOGIN",
                            color = Color.White,
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
                            .background(Color.LightGray)
                    )


                    Text(
                        text = "  OR  ",

                        color = Color.Gray,

                        fontSize = 16.sp
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.LightGray)
                    )
                }


                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                OutlinedButton(

                    onClick = onCreateAccountClick,

                    modifier = Modifier
                        .width(280.dp)
                        .height(55.dp),

                    shape = RoundedCornerShape(10.dp)
                ) {

                    Icon(

                        imageVector = Icons.Default.Person,

                        contentDescription =
                            "Create Account",

                        tint = PLMUNGreen
                    )


                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )


                    Text(

                        text = "Create New Account",

                        color = PLMUNGreen,

                        fontSize = 16.sp
                    )
                }
            }


            GreenFooter(
                modifier = Modifier.align(
                    Alignment.BottomCenter
                ),
            )
        }
    }
}


@Composable
fun GreenFooter(
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
    ) {


        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val path = Path().apply {

                moveTo(
                    0f,
                    size.height * 0.35f
                )

                cubicTo(

                    size.width * 0.25f,
                    size.height * 0.05f,

                    size.width * 0.55f,
                    size.height * 0.70f,

                    size.width,
                    size.height * 0.15f
                )

                lineTo(
                    size.width,
                    size.height
                )

                lineTo(
                    0f,
                    size.height
                )

                close()
            }


            drawPath(
                path = path,

                color = PLMUNGreen
            )
        }


        Column(

            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 24.dp,
                    bottom = 15.dp
                )
        ) {

            Text(
                text = "Pamantasan ng Lungsod ng Muntinlupa",

                color = Color.White,

                fontSize = 13.sp
            )


            Text(
                text = "Campus Lost & Found System",

                color = Color.White,

                fontSize = 13.sp
            )
        }
    }
}
