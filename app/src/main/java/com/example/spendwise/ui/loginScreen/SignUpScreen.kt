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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.ui.theme.SpendWisePrimary
import com.example.spendwise.viewModel.LoginViewModel

@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel,
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Create your new Spend Wise account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SpendWisePrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "USERNAME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E6368),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = loginViewModel.username,
                        onValueChange = { loginViewModel.onUsernameChange(it) },
                        placeholder = { Text("Enter username") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "PHONE NUMBER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E6368),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = loginViewModel.phoneNumber,
                        onValueChange = { loginViewModel.onPhoneChange(it) },
                        placeholder = { Text("Enter phone number") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "EMAIL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E6368),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = loginViewModel.inputUserId,
                        onValueChange = { loginViewModel.onUserIdChange(it) },
                        placeholder = { Text("Enter email") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "PASSWORD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E6368),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = loginViewModel.inputPassword,
                        onValueChange = { loginViewModel.onPasswordChange(it) },
                        placeholder = {
                            Text(
                                "Enter password",
                                color = Color.LightGray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                null,
                                tint = Color.LightGray
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    loginViewModel.togglePasswordVisibility()
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (loginViewModel.isPasswordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }
                        },
                        visualTransformation =
                            if (loginViewModel.isPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )


                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "CONFIRM PASSWORD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E6368),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = loginViewModel.confirmPassword,
                        onValueChange = {
                            loginViewModel.onConfirmPasswordChange(it)
                        },
                        placeholder = {
                            Text(
                                "Confirm password",
                                color = Color.LightGray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                null,
                                tint = Color.LightGray
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    loginViewModel.togglePasswordVisibility()
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (loginViewModel.isPasswordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }
                        },
                        visualTransformation =
                            if (loginViewModel.isPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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

                                } else {

                                    Toast.makeText(
                                        context,
                                        loginViewModel.loginErrorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpendWisePrimary
                        )
                    ) {
                        Text(
                            text = "Sign Up",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")

                    withStyle(
                        style = SpanStyle(
                            color = SpendWisePrimary,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Log In")
                    }
                },
                modifier = Modifier.clickable {
                    onBackToLogin()
                },
                color = SpendWisePrimary
            )
        }
    }
}