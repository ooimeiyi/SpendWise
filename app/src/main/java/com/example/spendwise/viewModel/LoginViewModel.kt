package com.example.spendwise.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

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

    var isConfirmPasswordVisible by mutableStateOf(false)
        private set

    var isLoggingIn by mutableStateOf(false)
        private set

    var loginError by mutableStateOf(false)
        private set

    var loginErrorMessage by mutableStateOf("")

    // SIGN UP FIELD ERRORS
    var usernameError by mutableStateOf<String?>(null)
        private set

    var phoneError by mutableStateOf<String?>(null)
        private set

    var emailError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

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

    fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
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
                    val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                    loginErrorMessage = when {
                        errorCode == "ERROR_USER_NOT_FOUND" -> "No account found. Please sign up."
                        errorCode == "ERROR_INVALID_EMAIL" -> "Invalid email format"
                        task.exception is FirebaseAuthInvalidUserException -> "No account found. Please sign up."
                        task.exception is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password"
                        else -> task.exception?.message ?: "Login failed"
                    }
                    onResult(false)
                }
            }
    }

    // ================= SIGN UP =================

    fun createAccount(onResult: (Boolean) -> Unit) {

        if (!validateSignUpFields()) return

        isLoggingIn = true

        auth.createUserWithEmailAndPassword(inputUserId, inputPassword)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = auth.currentUser!!.uid
                    val usernameKey = username.trim()

                    val userDoc = firestore.collection("users").document(uid)
                    val profileDoc = userDoc.collection("profile").document(usernameKey)

                    val userRootMap = hashMapOf(
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    val profileMap = hashMapOf(
                        "username" to usernameKey,
                        "phone" to phoneNumber,
                        "email" to inputUserId,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )

                    firestore.batch()
                        .apply {
                            set(userDoc, userRootMap)
                            set(profileDoc, profileMap)
                        }
                        .commit()
                        .addOnSuccessListener {

                            isLoggingIn = false
                            loginError = false

                            clearSignUpForm(clearAuthInputs = true)
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

    fun clearSignUpForm(clearAuthInputs: Boolean = false) {
        username = ""
        phoneNumber = ""
        confirmPassword = ""
        clearSignUpErrors()

        if (clearAuthInputs) {
            inputUserId = ""
            inputPassword = ""
        }
    }

    fun clearLoginInputs() {
        inputUserId = ""
        inputPassword = ""
    }

    private fun clearSignUpErrors() {
        usernameError = null
        phoneError = null
        emailError = null
        passwordError = null
        confirmPasswordError = null
    }

    private fun validateSignUpFields(): Boolean {
        clearSignUpErrors()

        var isValid = true

        val trimmedUsername = username.trim()
        if (trimmedUsername.isBlank()) {
            usernameError = "Username is required"
            isValid = false
        } else if (trimmedUsername.contains("/")) {
            usernameError = "Username cannot contain '/'"
            isValid = false
        }

        val phoneRegex = Regex("^[0-9]{10,15}$")
        if (phoneNumber.isBlank()) {
            phoneError = "Phone number is required"
            isValid = false
        } else if (!phoneRegex.matches(phoneNumber)) {
            phoneError = "Phone number must be 10-15 digits"
            isValid = false
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        if (inputUserId.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!emailRegex.matches(inputUserId)) {
            emailError = "Invalid email format"
            isValid = false
        }

        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-={}\\[\\]:;\"'<>,./]).{8,}$")
        if (inputPassword.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (!passwordRegex.matches(inputPassword)) {
            passwordError = "Use 8+ chars with upper, lower, number, and symbol"
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Confirm password is required"
            isValid = false
        } else if (inputPassword != confirmPassword) {
            confirmPasswordError = "Password does not match"
            isValid = false
        }

        loginError = !isValid
        loginErrorMessage = if (isValid) "" else "Please fix highlighted fields"
        return isValid
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