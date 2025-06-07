package com.wmc.eventplaner.feature.auth

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wmc.eventplaner.R
import com.wmc.eventplaner.common.CustomTextField
import com.wmc.eventplaner.common.ErrorDialog
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.common.LoadingDialog
import com.wmc.eventplaner.common.PasswordTextField
import com.wmc.eventplaner.common.SuccessDialog
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.data.dto.RegistrationRequest
import com.wmc.eventplaner.data.dto.RegistrationResponse
import com.wmc.eventplaner.data.dto.User
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.util.Const
import com.wmc.eventplaner.util.getGoogleIdCredential
import kotlinx.coroutines.launch
import kotlin.math.log

@Composable
fun CreateAccountScreenRoute(onBack: () -> Boolean,onLogin: () -> Unit,viewModel:
                            AuthViewModel =hiltViewModel(),
                             shareViewModel : ShareViewModel = hiltViewModel()
                             ) {
    // Observe viewModel states
    val registerState by viewModel.registerState
    val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState
    CreateAccountScreen(
        onBackClick = {onBack()},
        onSignUpClick = { request->

            viewModel.registerUserWithLocal(request)
        },
        onGoogleSignUpClick = {
            viewModel.registerUserWithSocial(it)
        },
        onLoginClick = {onBack()},
        registerState = registerState,
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        onRegisterSuccess ={
            // Suppose LoginResponse looks similar
            val loginResponse = LoginResponse(
                success = registerState?.success,
                message = registerState?.message,
                token = registerState?.token,
                user  = User(
                    id = registerState?.user?.id,
                    email = registerState?.user?.email,
                    fullName = registerState?.user?.fullName,
                    role = registerState?.user?.role
                ),
            )
            Const.authToken = registerState?.token?:""
            shareViewModel.updateAuthData(loginResponse)
            viewModel.registerState.value = null
            onLogin()
        }

    )
}
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit,
    onSignUpClick: (RegistrationRequest) -> Unit,  // Changed to accept RegistrationRequest
    onGoogleSignUpClick: (RegistrationRequest) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    registerState: RegistrationResponse? = null,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onDismissError: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Validation states
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // Handle API responses

        registerState?.let {
            SuccessDialog(
                title = "Success",
                message = registerState.message?:"Register successfully",
                onDismiss = { onRegisterSuccess()  },
                onOkay = {

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
            onDismiss ={onDismissError()
                //onRegisterSuccess()
            } ,
            onRetry = error.retryAction
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header and back button
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
        }

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Full Name Field
        CustomTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = if (it.isBlank()) "Full name is required" else null
            },
            placeholder = "Full Name",
            leadingIcon = Icons.Default.Person,
            isError = fullNameError != null,
            supportingText = fullNameError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email Field
        CustomTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = when {
                    it.isBlank() -> "Email is required"
                    !it.isValidEmail() -> "Invalid email format"
                    else -> null
                }
            },
            placeholder = "Email",
            leadingIcon = Icons.Default.Email,
            isError = emailError != null,
            supportingText = emailError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Field
        PasswordTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = when {
                    it.isBlank() -> "Password is required"
                    it.length < 8 -> "Password must be at least 8 characters"
                    !it.hasRequiredChars() -> "Include uppercase , lowercase, or special characters"
                    else -> null
                }
                // Update confirm password validation if needed
                if (confirmPassword.isNotBlank()) {
                    confirmPasswordError = if (it != confirmPassword) "Passwords don't match" else null
                }
            },
            label = "Password",
            isError = passwordError != null,
            errorMessage = passwordError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password Field
        PasswordTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = when {
                    it.isBlank() -> "Please confirm your password"
                    it != password -> "Passwords don't match"
                    else -> null
                }
            },
            label = "Confirm Password",
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button
        Button(
            onClick = {
                // Validate all fields
                fullNameError = if (fullName.isBlank()) "Full name is required" else null
                emailError = when {
                    email.isBlank() -> "Email is required"
                    !email.isValidEmail() -> "Invalid email format"
                    else -> null
                }
                passwordError = when {
                    password.isBlank() -> "Password is required"
                    password.length < 8 -> "Password must be at least 8 characters"
                    !password.hasRequiredChars() -> "Include uppercase , lowercase, or special characters"
                    else -> null
                }
                confirmPasswordError = when {
                    confirmPassword.isBlank() -> "Please confirm your password"
                    confirmPassword != password -> "Passwords don't match"
                    else -> null
                }

                // Only proceed if all validations pass
                if (listOf(
                        fullNameError,
                        emailError,
                        passwordError,
                        confirmPasswordError
                    ).all { it == null }
                ) {
                    onSignUpClick(
                        RegistrationRequest(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            password = password,
                         //   confirmPassword = confirmPassword
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign Up Button
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    val googleCredential = getGoogleIdCredential(context)
                    if (googleCredential != null) {
                        val idToken = googleCredential.idToken
                        val email = googleCredential.id
                        val displayName = googleCredential.displayName
                        val profilePictureUri = googleCredential.profilePictureUri
                        onGoogleSignUpClick(RegistrationRequest(
                            email = email,
                           // token = idToken,
                            fullName = displayName,
                            //image = profilePictureUri.toString()

                        ))

                    } else {
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign up with Google", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Prompt
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLoginClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", color = MaterialTheme.colorScheme.onSurface)
            Text("Log In", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

// Extension functions for validation
private fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

private fun String.hasRequiredChars(): Boolean {
    val hasUpper = any { it.isUpperCase() }
    val hasLower = any { it.isLowerCase() }
    val hasSpecial = any { !it.isLetterOrDigit() }
    return hasUpper || hasLower || hasSpecial
    //return hasUpper
}



@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    MaterialTheme {
        CreateAccountScreen(
            onBackClick = {},
            onSignUpClick = { },
            onGoogleSignUpClick = {},
            onLoginClick = {}
        )
    }
}

