package com.example.spendwise.ui.loginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.ui.theme.SpendWisePrimary
import com.example.spendwise.viewModel.LoginViewModel

@Composable
fun ForgotPasswordScreen(
    loginViewModel: LoginViewModel,
    onBackToLogin: () -> Unit
) {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        IconButton(
            onClick = {
                onBackToLogin()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = SpendWisePrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Reset Password",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = SpendWisePrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = loginViewModel.resetEmail,
                onValueChange = {
                    loginViewModel.onResetEmailChange(it)
                },
                label = {
                    Text("Enter Email")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    loginViewModel.sendPasswordResetEmail { success, message ->

                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (success) {
                            onBackToLogin()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpendWisePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "Send Reset Email",
                    color = Color.White
                )
            }
        }
    }
}