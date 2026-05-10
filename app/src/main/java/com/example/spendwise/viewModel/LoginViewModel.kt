package com.example.spendwise.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // LOGIN
    var inputUserId by mutableStateOf("")
        private set

    var inputPassword by mutableStateOf("")
        private set

    // SIGN UP
    var username by mutableStateOf("")
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    // RESET PASSWORD
    var resetEmail by mutableStateOf("")
        private set

    // UI STATES
    var isPasswordVisible by mutableStateOf(false)
        private set

    var isLoggingIn by mutableStateOf(false)
        private set

    var loginError by mutableStateOf(false)
        private set

    var loginErrorMessage by mutableStateOf("")

    // ================= INPUT FUNCTIONS =================

    fun onUserIdChange(v: String) { inputUserId = v }
    fun onPasswordChange(v: String) { inputPassword = v }
    fun onUsernameChange(v: String) { username = v }
    fun onPhoneChange(v: String) { phoneNumber = v }
    fun onConfirmPasswordChange(v: String) { confirmPassword = v }
    fun onResetEmailChange(v: String) { resetEmail = v }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    // ================= LOGIN =================

    fun attemptLogin(onResult: (Boolean) -> Unit) {

        if (inputUserId.isBlank() || inputPassword.isBlank()) {
            loginError = true
            loginErrorMessage = "Please fill all fields"
            return
        }

        isLoggingIn = true

        auth.signInWithEmailAndPassword(inputUserId, inputPassword)
            .addOnCompleteListener { task ->

                isLoggingIn = false

                if (task.isSuccessful) {
                    loginError = false
                    onResult(true)
                } else {
                    loginError = true
                    loginErrorMessage = task.exception?.message ?: "Login failed"
                    onResult(false)
                }
            }
    }

    // ================= SIGN UP =================

    fun createAccount(onResult: (Boolean) -> Unit) {

        if (
            username.isBlank() ||
            phoneNumber.isBlank() ||
            inputUserId.isBlank() ||
            inputPassword.isBlank() ||
            confirmPassword.isBlank()
        ) {
            loginError = true
            loginErrorMessage = "Please fill all fields"
            return
        }

        if (inputPassword != confirmPassword) {
            loginError = true
            loginErrorMessage = "Password does not match"
            return
        }

        isLoggingIn = true

        auth.createUserWithEmailAndPassword(inputUserId, inputPassword)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = auth.currentUser!!.uid

                    val userMap = hashMapOf(
                        "username" to username,
                        "phone" to phoneNumber,
                        "email" to inputUserId
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(userMap)
                        .addOnSuccessListener {

                            isLoggingIn = false
                            loginError = false

                            clearFields()
                            onResult(true)
                        }
                        .addOnFailureListener {

                            isLoggingIn = false
                            loginError = true
                            loginErrorMessage = it.message ?: "Firestore failed"
                            onResult(false)
                        }

                } else {

                    isLoggingIn = false
                    loginError = true
                    loginErrorMessage = task.exception?.message ?: "Signup failed"
                    onResult(false)
                }
            }
    }

    private fun clearFields() {
        username = ""
        phoneNumber = ""
        inputUserId = ""
        inputPassword = ""
        confirmPassword = ""
    }

    // ================= RESET PASSWORD =================

    fun sendPasswordResetEmail(onResult: (Boolean, String) -> Unit) {

        if (resetEmail.isBlank()) {
            onResult(false, "Enter email")
            return
        }

        auth.sendPasswordResetEmail(resetEmail)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onResult(true, "Reset email sent")
                } else {
                    onResult(false, task.exception?.message ?: "Failed")
                }
            }
    }
}