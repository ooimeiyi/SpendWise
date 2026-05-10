package com.example.spendwise.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var inputUserId by mutableStateOf("")
        private set

    var inputPassword by mutableStateOf("")
        private set

    var isPasswordVisible by mutableStateOf(false)
        private set

    var isLoggingIn by mutableStateOf(false)
        private set

    var loginError by mutableStateOf(false)
        private set

    var loginErrorMessage by mutableStateOf("")
        private set

    fun onUserIdChange(value: String) {
        inputUserId = value
    }

    fun onPasswordChange(value: String) {
        inputPassword = value
    }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

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
                    loginErrorMessage =
                        task.exception?.message ?: "Login Failed"
                    onResult(false)
                }
            }
    }

    fun createAccount(onResult: (Boolean) -> Unit) {

        if (inputUserId.isBlank() || inputPassword.isBlank()) {
            loginError = true
            loginErrorMessage = "Please fill all fields"
            return
        }

        isLoggingIn = true

        auth.createUserWithEmailAndPassword(inputUserId, inputPassword)
            .addOnCompleteListener { task ->

                isLoggingIn = false

                if (task.isSuccessful) {
                    loginError = false
                    onResult(true)
                } else {
                    loginError = true
                    loginErrorMessage =
                        task.exception?.message ?: "Sign Up Failed"
                    onResult(false)
                }
            }
    }
}