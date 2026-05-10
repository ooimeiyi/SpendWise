package com.example.spendwise

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendwise.ui.dashboard.DashboardScreen
import com.example.spendwise.ui.loginScreen.ForgotPasswordScreen
import com.example.spendwise.ui.loginScreen.LogInScreen
import com.example.spendwise.ui.loginScreen.SignUpScreen
import com.example.spendwise.viewModel.LoginViewModel

@Composable
fun SpendWiseScreen() {

    val viewModel: LoginViewModel = viewModel()

    var screen by remember { mutableStateOf("login") }

    when (screen) {

        "login" -> {

            LogInScreen(
                loginViewModel = viewModel,

                onLoginSuccess = {
                    screen = "dashboard"
                },

                onGoToSignUp = {
                    screen = "signup"
                },

                onForgotPassword = {
                    screen = "forgotPassword"
                }
            )
        }

        "signup" -> {

            SignUpScreen(
                loginViewModel = viewModel,

                onBackToLogin = {
                    screen = "login"
                }
            )
        }

        "forgotPassword" -> {

            ForgotPasswordScreen(
                loginViewModel = viewModel,

                onBackToLogin = {
                    screen = "login"
                }
            )
        }

        "dashboard" -> {

            DashboardScreen(
                onLogout = {

                    viewModel.onUserIdChange("")
                    viewModel.onPasswordChange("")

                    screen = "login"
                }
            )
        }
    }
}