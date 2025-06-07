package com.wmc.eventplaner.util

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.gson.Gson
import org.json.JSONObject
import java.util.Locale
@Composable
fun rememberToast(): (String) -> Unit {
    val context = LocalContext.current
    return remember { { message: String ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    } }
}
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
// Extension function to format date/time strings
fun String.formatDateTime(): String {
    return try {
        // Try parsing as ISO format first
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = isoFormat.parse(this)

        // Format for display
        val displayFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        displayFormat.format(date)
    } catch (e: Exception) {
        try {
            // If ISO format fails, try parsing as simple time
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = timeFormat.parse(this)

            val displayFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            displayFormat.format(date)
        } catch (e: Exception) {
            this // Return original if parsing fails
        }
    }
}
inline fun <reified T> T.toJsonObject(): JSONObject {
    val gson = Gson()
    val json = gson.toJson(this, T::class.java)
    return JSONObject(json)
}