package com.example.spendwise.ui.loginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.viewModel.LoginViewModel

@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel,
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF3636)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // EMAIL
        OutlinedTextField(
            value = loginViewModel.inputUserId,
            onValueChange = { loginViewModel.onUserIdChange(it) },
            placeholder = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = loginViewModel.inputPassword,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            placeholder = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation =
                if (loginViewModel.isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SIGN UP BUTTON
        Button(
            onClick = {
                loginViewModel.createAccount { success ->
                    if (success) {
                        Toast.makeText(
                            context,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSignUpSuccess()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF3636)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Already have an account? Login",
            color = Color(0xFFEF3636),
            modifier = Modifier.clickable {
                onBackToLogin()
            }
        )
    }
}