package com.example.campuslostfound

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.campuslostfound.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private val prefs = application.getSharedPreferences("clf_prefs", Context.MODE_PRIVATE)

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isUserAuthenticated = MutableStateFlow(auth.currentUser != null)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated

    private val _userData = mutableStateOf<User?>(null)
    val userData: State<User?> = _userData

    init {
        if (auth.currentUser != null) {
            fetchUserData()
        }
    }

    private fun fetchUserData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                _userData.value = document.toObject(User::class.java)
            }
    }

    fun signUp(email: String, password: String, fullName: String, studentId: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val user = User(uid, fullName, studentId, email)
                    
                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            _userData.value = user
                            _isLoading.value = false
                            _isUserAuthenticated.value = true
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            _errorMessage.value = "Failed to save user data: ${e.message}"
                        }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = task.exception?.message ?: "Sign up failed"
                }
            }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    fetchUserData()
                    _isUserAuthenticated.value = true
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.message ?: "Login failed"
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _isUserAuthenticated.value = false
    }

    fun getSavedCredentials(): Pair<String, String>? {
        val email = prefs.getString("saved_email", null)
        val password = prefs.getString("saved_password", null)
        return if (email != null && password != null) email to password else null
    }

    fun saveCredentials(email: String, password: String, remember: Boolean) {
        if (remember) {
            prefs.edit().putString("saved_email", email).putString("saved_password", password).apply()
        } else {
            prefs.edit().remove("saved_email").remove("saved_password").apply()
        }
    }

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            onComplete(false, "Please enter your email address.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Reset link sent to your email.")
                } else {
                    onComplete(false, task.exception?.message ?: "Failed to send reset email.")
                }
            }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun updateProfile(fullName: String, studentId: String, course: String, yearLevel: String, phoneNumber: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email ?: ""
        val updatedUser = User(uid, fullName, studentId, email, course, yearLevel, phoneNumber)

        _isLoading.value = true
        db.collection("users").document(uid).set(updatedUser)
            .addOnSuccessListener {
                _userData.value = updatedUser
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = "Failed to update profile: ${e.message}"
            }
    }
}
