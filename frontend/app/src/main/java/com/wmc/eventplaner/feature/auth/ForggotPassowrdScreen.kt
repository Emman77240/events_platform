package com.wmc.eventplaner.feature.auth

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wmc.eventplaner.common.CustomButton
import com.wmc.eventplaner.common.CustomTextField
import com.wmc.eventplaner.common.ErrorDialog
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.common.LoadingDialog
import com.wmc.eventplaner.common.ScreenContainer
import com.wmc.eventplaner.common.SuccessDialog
import com.wmc.eventplaner.data.dto.ForgotPasswordRequest
import com.wmc.eventplaner.data.dto.LoginResponse

@Composable
fun ForgotPasswordScreenRoute(onBackToLogin: () -> Unit,
                              viewModel: AuthViewModel = hiltViewModel()) {
    // Observe viewModel states
    var forgotPasswordState by viewModel.forgotPasswordState
    val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState

    ForgotPasswordScreen(
        onBackToLogin = onBackToLogin,
        onSendResetLink = {
            viewModel.forgotPassword(ForgotPasswordRequest(email = it))

        },
        forgotPasswordState = forgotPasswordState,
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        onSuccess = {
            forgotPasswordState =null
            onBackToLogin()
        }
    )
}

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onSendResetLink: (String) -> Unit,
    forgotPasswordState: LoginResponse? = null,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onSuccess: () -> Unit = {},
    onDismissError: () -> Unit = {}
) {
    var emailState by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Handle API responses

        forgotPasswordState?.let {
            SuccessDialog(
                title = "Password Reset",
                message = forgotPasswordState.message?:"Password is send to your Email",
                onDismiss = { onSuccess()  },
                onOkay = {
                    onSuccess()
                }
            )
        }

    // Loading and error states
    if (isLoading) {
        LoadingDialog()
    }

    errorState?.let { error ->
        ErrorDialog(
            message = error.message ?: "Something went wrong",
            onDismiss = onDismissError,
            onRetry = error.retryAction
        )
    }

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize().padding(top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Instructions
            Text(
                text = "Enter the email associated with your account and we'll send an email with instructions to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email field with validation
            CustomTextField(
                value = emailState,
                onValueChange = {
                    emailState = it
                    emailError = if (it.isNotBlank() && !it.isValidEmail()) {
                        "Invalid email format"
                    } else null
                },
                placeholder = "Email",
                leadingIcon = Icons.Default.Email,
                isError = emailError != null,
                supportingText = emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Send Password Button
            CustomButton(
                text = "Send Password",
                onClick = {
                    // Validate email before proceeding
                    val isValid = emailState.isValidEmail()
                    emailError = if (!isValid) "Please enter a valid email" else null

                    if (isValid) {
                        onSendResetLink(emailState.trim())
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // Back to Login link
            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text("Back to Login")
            }
        }
    }
}

// Email validation extension
private fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen(
            onBackToLogin = {},
            onSendResetLink = {}
        )
    }
}