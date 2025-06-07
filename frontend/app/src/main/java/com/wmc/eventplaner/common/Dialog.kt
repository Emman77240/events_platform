package com.wmc.eventplaner.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ui/common/Dialogs.kt
@Composable
fun LoadingDialog() {
    Dialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissRequest = { }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            androidx.compose.foundation.layout.Row {
                onRetry?.let {
                    TextButton(onClick = it) {
                        Text("Retry")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }


        }
    )
}
@Composable
fun SuccessDialog(
    title: String = "Success",
    message: String,
    onDismiss: () -> Unit,
    onOkay: (() -> Unit)? = null,
    dismissOnOkay: Boolean = true,
    dismissOnBackPress: Boolean = true,
    dismissOnOutsideClick: Boolean = true
) {
    AlertDialog(
        onDismissRequest = {
            if (dismissOnBackPress || dismissOnOutsideClick) onDismiss()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onOkay?.invoke()
                    if (dismissOnOkay) onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnOutsideClick
        )
    )
}


@Composable

fun CustomTimePicker(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24HourFormat: Boolean = true,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    // Convert initial 24-hour to 12-hour format if needed
    val (initialDisplayHour, initialIsAm) = if (!is24HourFormat) {
        when {
            initialHour == 0 -> Pair(12, true) // 12 AM
            initialHour < 12 -> Pair(initialHour, true) // AM
            initialHour == 12 -> Pair(12, false) // 12 PM
            else -> Pair(initialHour - 12, false) // PM
        }
    } else {
        Pair(initialHour, initialHour < 12)
    }

    var selectedDisplayHour by remember { mutableStateOf(initialDisplayHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var isAm by remember { mutableStateOf(initialIsAm) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Time Display
        Text(
            text = if (is24HourFormat) {
                "${(if (!isAm && !is24HourFormat) selectedDisplayHour + 12 else selectedDisplayHour).toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
            } else {
                "${selectedDisplayHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')} ${if (isAm) "AM" else "PM"}"
            },
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Time Selection Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Hour Picker
            NumberPicker(
                value = selectedDisplayHour,
                onValueChange = { selectedDisplayHour = it },
                range = if (is24HourFormat) 0..23 else 1..12,
                modifier = Modifier.weight(1f)
            )

            Text(":", style = MaterialTheme.typography.displayMedium)

            // Minute Picker
            NumberPicker(
                value = selectedMinute,
                onValueChange = { selectedMinute = it },
                range = 0..59,
                modifier = Modifier.weight(1f)
            )

            // AM/PM Picker (only shown in 12-hour format)
            if (!is24HourFormat) {
                Column(
                    modifier = Modifier.weight(0.5f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        onClick = { isAm = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAm) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                    ) {
                        Text("AM")
                    }
                    TextButton(
                        onClick = { isAm = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAm) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                    ) {
                        Text("PM")
                    }
                }
            }
        }

        // Confirm Button
        Button(
            onClick = {
                val finalHour = if (!is24HourFormat) {
                    when {
                        isAm && selectedDisplayHour == 12 -> 0 // 12 AM
                        isAm -> selectedDisplayHour // AM
                        selectedDisplayHour == 12 -> 12 // 12 PM
                        else -> selectedDisplayHour + 12 // PM
                    }
                } else {
                    selectedDisplayHour
                }
                onTimeSelected(finalHour, selectedMinute)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Set Time")
        }
    }
}
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                if (value < range.last) {
                    onValueChange(value + 1)
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Increase") // Changed to ArrowUp
        }

        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium
        )

        IconButton(
            onClick = {
                if (value > range.first) {
                    onValueChange(value - 1)
                }},
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
        }
    }
}
@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = {
            TextButton(onClick = {
                onConfirmLogout()
                onDismiss()
            }) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}
