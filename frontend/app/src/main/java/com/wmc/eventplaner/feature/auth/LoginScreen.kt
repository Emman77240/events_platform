package com.wmc.eventplaner.feature.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wmc.eventplaner.R
import com.wmc.eventplaner.common.CustomButton
import com.wmc.eventplaner.common.CustomTextField
import com.wmc.eventplaner.common.ErrorDialog
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.common.LoadingDialog
import com.wmc.eventplaner.common.PasswordTextField
import com.wmc.eventplaner.common.ScreenContainer
import com.wmc.eventplaner.data.dto.LoginRequest
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.data.dto.SocialLoginRequest
import com.wmc.eventplaner.data.dto.User
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.util.Const
import com.wmc.eventplaner.util.getGoogleIdCredential
import kotlinx.coroutines.launch

@Composable
 fun LoginScreenRoute(onNavigateToSignup: () -> Unit, onNavigateToForgot: () -> Unit,onLogin: () -> Unit,
                      viewModel: AuthViewModel =hiltViewModel(),
                      shareViewModel : ShareViewModel = hiltViewModel()
                      ) {
    // Observe viewModel states
    val loginState by viewModel.loginState
    val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState
    val authRequestCache by viewModel.authRequestCache.collectAsStateWithLifecycle()


    LoginScreen(
        onNavigateToSignup = {onNavigateToSignup()},
        onNavigateToForgot = {onNavigateToForgot()},
        onLogin = {
            viewModel.isSocialLogin = false
            viewModel.login(it)
        },
        onLoginSuccess = {
            shareViewModel.updateAuthData(it)
            Const.authToken = it.token?:""
            onLogin()
        },
        onSocialLogin = {
            viewModel.isSocialLogin = true
            viewModel.loginWithSocial(it)
        },
        loginState = loginState,
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        authRequestCache = authRequestCache

    )
}


@Composable
private fun LoginScreen(
    onNavigateToSignup: () -> Unit = {},
    onNavigateToForgot: () -> Unit = {},
    onLogin: (LoginRequest) -> Unit = {},  // Changed to accept LoginRequest
    onSocialLogin: (SocialLoginRequest) -> Unit = {},
    loginState: LoginResponse? = null,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onDismissError : () -> Unit = {},
    onLoginSuccess: (LoginResponse) -> Unit = {},
    authRequestCache: LoginRequest? = null
) {
    var email by remember { mutableStateOf(authRequestCache?.email?:"") }
    var password by remember { mutableStateOf(authRequestCache?.password?:"") }
    var passwordError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    
    val context  = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // Handle success
    LaunchedEffect(loginState) {
        loginState?.let {
            onLoginSuccess(it)
        }
    }

    if (isLoading) {
        LoadingDialog()
    }

    errorState?.let { error ->
        ErrorDialog(
            message = error.message ?: "Something went wrong",
            onDismiss = {
                onDismissError()
               // onLoginSuccess(generateDummyLoginResponse()) // dummy to be remove

               // error.dissmissAction
            },
            onRetry = error.retryAction,

        )
    }


    // Validation function
    fun validateInputs(): Boolean {
        val isEmailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 8  // Minimum 8 characters

        emailError = !isEmailValid
        passwordError = !isPasswordValid

        return isEmailValid && isPasswordValid
    }
    ScreenContainer  {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome back", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Please enter your email and password to sign in",
                textAlign = TextAlign.Center, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false  // Reset error when typing
                },
                placeholder = "Email",
                leadingIcon = Icons.Default.Email,
                isError = emailError,
                errorMessage = if (emailError) "Invalid email format" else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false  // Reset error when typing
                },
                label = "Enter Password",
                isError = passwordError,
                errorMessage = if (passwordError) "Password must be at least 8 characters" else null,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Forgot Password?",
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        onNavigateToForgot()
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                text = "Login",
                onClick = {
                    if (validateInputs()) {
                        onLogin(LoginRequest(
                            email = email,
                            password = password
                        ))
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("OR")

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
                            val givenName = googleCredential.givenName
                            val familyName = googleCredential.familyName
                            val profilePictureUri = googleCredential.profilePictureUri

                            onSocialLogin(SocialLoginRequest(
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
                    Text("Login with Google", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Don't have an account? Sign up",
                color = Color(0xFF4CAF50),
                modifier = Modifier.clickable { onNavigateToSignup() }
            )
        }
    }


}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
fun generateDummyLoginResponse(): LoginResponse {
    return LoginResponse(
        success = true,
        message = "Login successful",
        token = "dummy_token_1234567890",
        user = User(
            id = 1,
            email = "john.doe@example.com",
            fullName = "John Doe",
            role = "admin"
        )
    )
}
