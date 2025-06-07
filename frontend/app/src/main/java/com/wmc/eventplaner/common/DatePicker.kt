package com.wmc.eventplaner.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    initialDate: LocalDate = LocalDate.now(),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    title: String = "Select Date",
    confirmText: String = "OK",
    dismissText: String = "Cancel"
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    val yearRange = remember { (1900..2100) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column (modifier = Modifier.padding(8.dp)) {
                // Year Picker
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Year:", modifier = Modifier.padding(end = 8.dp))
                    Spinner(
                        items = yearRange.toList(),
                        selectedItem = selectedDate.year,
                        onItemSelected = { year ->
                            selectedDate = selectedDate.withYear(year)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month Picker
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Month:", modifier = Modifier.padding(end = 8.dp))
                    Spinner(
                        items = Month.values().toList(),
                        selectedItem = selectedDate.month,
                        onItemSelected = { month ->
                            selectedDate = selectedDate.withMonth(month.value)
                        },
                        itemContent = { month -> Text(month.getDisplayName(TextStyle.FULL, Locale.getDefault())) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day Picker
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Day:", modifier = Modifier.padding(end = 8.dp))
                    val maxDaysInMonth = selectedDate.month.length(selectedDate.isLeapYear)
                    Spinner(
                        items = (1..maxDaysInMonth).toList(),
                        selectedItem = selectedDate.dayOfMonth,
                        onItemSelected = { day ->
                            selectedDate = selectedDate.withDayOfMonth(day)
                        }
                    )
                }

                // Date Preview
                Text(
                    text = "Selected: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            Button (
                onClick = { onDateSelected(selectedDate) },
                enabled = isDateInRange(selectedDate, minDate, maxDate)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isDateInRange(date: LocalDate, minDate: LocalDate?, maxDate: LocalDate?): Boolean {
    return (minDate == null || !date.isBefore(minDate)) &&
            (maxDate == null || !date.isAfter(maxDate))
}@Composable
private fun <T> Spinner(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit = { Text(it.toString()) }
) {
    var expanded by remember { mutableStateOf(false) }

    Box (modifier = modifier.wrapContentSize()) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemContent(selectedItem)
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { itemContent(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

