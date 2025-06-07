package com.wmc.eventplaner.util

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.wmc.eventplaner.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getGoogleIdCredential(context: Context): GoogleIdTokenCredential? {
    return try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id)) // <-- YOUR web client ID from Google Console
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response: GetCredentialResponse = withContext(Dispatchers.IO) {
            credentialManager.getCredential(context, request)
        }

        val credential = response.credential
        credential as? GoogleIdTokenCredential
    } catch (e: Exception) {
        Log.e("GoogleSignIn", "Error: ${e.message}")
        null
    }
}
