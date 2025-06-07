package com.wmc.eventplaner.feature.event

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wmc.eventplaner.common.CustomButton
import com.wmc.eventplaner.common.CustomTextField
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import com.wmc.eventplaner.data.dto.CreateEventRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wmc.eventplaner.common.CustomTimePicker
import com.wmc.eventplaner.common.ErrorDialog
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.common.LoadingDialog
import com.wmc.eventplaner.common.SuccessDialog
import com.wmc.eventplaner.data.dto.EventCreationResponse
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.feature.home.HomeViewModel
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Calendar

@Composable
fun CreateEventScreenRoute(
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel()
){
    val createEventState by viewModel.createEventState
    val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState
    val userDetail by shareViewModel.authData.collectAsStateWithLifecycle()

    CreateEventScreen(
        onSave = {

            it.email = userDetail?.user?.email
            viewModel.createEvent(it)
        },
        onBack = {
            onBack()
        },
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        userDetail = userDetail,
        createEventState = createEventState,
        eventCreated = {
            shareViewModel.isAllEventCall=true
            onBack()
        }


    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onSave: (CreateEventRequest) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onDismissError: () -> Unit = {},
    userDetail: LoginResponse? = null,
    createEventState: EventCreationResponse? = null,
    eventCreated: () -> Unit = {},
) {
    // State variables
    var eventTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Validation states
    var titleError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var startTimeError by remember { mutableStateOf(false) }
    var endTimeError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }
    var timeConflictError by remember { mutableStateOf(false) }

    // Date/Time pickers state
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Date formatters
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter24h = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeFormatter12h = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    // Initialize with current date/time
    val currentDate = remember { dateFormatter.format(Date()) }
    val currentTime24h = remember { timeFormatter24h.format(Date()) }
    val currentTime12h = remember { timeFormatter12h.format(Date()) }

    val context = LocalContext.current
    var date by remember { mutableStateOf("") }
    var startTime24h by remember { mutableStateOf(currentTime24h) }
    var endTime24h by remember { mutableStateOf(currentTime24h) }
    var startTimeDisplay by remember { mutableStateOf("") }
    var endTimeDisplay by remember { mutableStateOf("") }

    // State for selected image
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<String?>(null) }
    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

        createEventState?.let {
            SuccessDialog(
                title = "Success",
                message = createEventState.responseMessage?:"Event create successfully",
                onDismiss = { eventCreated()  },
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
            onDismiss = onDismissError,
            onRetry = error.retryAction
        )
    }

    // Launcher to pick image from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            imageFile = uri.toString()
        }
    }

    // Launcher to request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to validate time selection
    fun validateTimes() {
        val start = timeFormatter24h.parse(startTime24h)?.time ?: 0
        val end = timeFormatter24h.parse(endTime24h)?.time ?: 0
        timeConflictError = start >= end
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = Modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Title
            CustomTextField(
                value = eventTitle,
                onValueChange = {
                    eventTitle = it
                    titleError = it.isBlank()
                },
                label = "Event Title*",
                isError = titleError,
                supportingText = if (titleError) "Title is required" else null
            )

            // Description
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                singleLine = false,
                maxLines = 3
            )

            // Date Picker
            CustomTextField(
                value = date,
                onValueChange = { /* Handled by date picker */ },
                label = "Date*",
                readOnly = true,
                isError = dateError,
                supportingText = if (dateError) "Date is required" else null,
                trailingIcon = Icons.Default.DateRange,
                onTrailingIconClick = { showDatePicker = true }
            )

            // Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Time
                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = "Start Time*",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                    )
                    CustomTextField(
                        value = startTimeDisplay,
                        onValueChange = {},
                        label = "Start Time",
                        readOnly = true,
                        isError = startTimeError || timeConflictError,
                        supportingText = when {
                            startTimeError -> "Start time is required"
                            timeConflictError -> "Must be before end time"
                            else -> null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartTimePicker = true },
                        trailingIcon = Icons.Default.DateRange,
                        onTrailingIconClick = { showStartTimePicker = true }
                    )
                }

                // End Time
                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = "End Time*",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                    )
                    CustomTextField(
                        value = endTimeDisplay,
                        onValueChange = {},
                        label = "End Time",
                        readOnly = true,
                        isError = endTimeError || timeConflictError,
                        supportingText = when {
                            endTimeError -> "End time is required"
                            timeConflictError -> "Must be after start time"
                            else -> null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndTimePicker = true },
                        trailingIcon = Icons.Default.DateRange,
                        onTrailingIconClick = { showEndTimePicker = true }
                    )
                }
            }

            // Location
            CustomTextField(
                value = location,
                onValueChange = {
                    location = it
                    locationError = it.isBlank()
                },
                label = "Location*",
                isError = locationError,
                supportingText = if (locationError) "Location is required" else null
            )

            // Image Upload
            CustomButton(
                onClick = {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context, imagePermission
                    ) == PackageManager.PERMISSION_GRANTED

                    if (isGranted) {
                        galleryLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(imagePermission)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                text = "Upload Image"
            )

            imageFile?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Event image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            CustomButton(
                text = "Save Event",
                onClick = {
                    // Validate fields
                    titleError = eventTitle.isBlank()
                    dateError = date.isBlank()
                    startTimeError = startTime24h.isBlank()
                    endTimeError = endTime24h.isBlank()
                    locationError = location.isBlank()
                    validateTimes()

                    if (!titleError && !dateError && !startTimeError && !endTimeError && !locationError && !timeConflictError) {
                        val uri = imageFile?.toUri()

                        uri?.let {
                            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(it, "r", null)
                                ?: throw IOException("Failed to open file")

                            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

                            val cacheFile = File(context.cacheDir, "event_image.jpg").apply {
                                outputStream().use { output ->
                                    inputStream.use { input ->
                                        input.copyTo(output)
                                    }
                                }
                            }

                            onSave(
                                CreateEventRequest(
                                    title = eventTitle,
                                    description = description,
                                    date = date,
                                    startTime = startTime24h,
                                    endTime = endTime24h,
                                    location = location,
                                    imageFile = cacheFile,
                                )
                            )
                        } ?: run {
                            onSave(
                                CreateEventRequest(
                                    title = eventTitle,
                                    description = description,
                                    date = date,
                                    startTime = startTime24h,
                                    endTime = endTime24h,
                                    location = location,
                                    imageFile = null,
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = dateFormatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Start Time Picker Dialog
    if (showStartTimePicker) {
        val (initialHour, initialMinute) = extractHourMinute(startTime24h)

        TimePickerDialog(
            onCancel = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                startTime24h = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                startTimeDisplay = timeFormatter12h.format(timeFormatter24h.parse(startTime24h)!!)
                validateTimes()
                showStartTimePicker = false
            },
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false // Show in 12-hour format with AM/PM
        )
    }

    // End Time Picker Dialog
    if (showEndTimePicker) {
        val (initialHour, initialMinute) = extractHourMinute(endTime24h)

        TimePickerDialog(
            onCancel = { showEndTimePicker = false },
            onConfirm = { hour, minute ->
                endTime24h = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                endTimeDisplay = timeFormatter12h.format(timeFormatter24h.parse(endTime24h)!!)
                validateTimes()
                showEndTimePicker = false
            },
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false // Show in 12-hour format with AM/PM
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24Hour: Boolean = false
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}


 fun extractHourMinute(time: String): Pair<Int, Int> {
    return try {
        val parts = time.split(":")
        parts[0].toInt() to parts[1].toInt()
    } catch (e: Exception) {
        0 to 0
    }
}

@Composable
private fun CustomTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    is24HourFormat: Boolean = true,
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            CustomTimePicker(
                initialHour = initialHour,
                initialMinute = initialMinute,
                is24HourFormat = is24HourFormat,
                onTimeSelected = onTimeSelected
            )
        },
        confirmButton = {}
    )
}

fun convertTo12HourFormat(time24: String): String {
    return try {
        val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = sdf24.parse(time24)
        sdf12.format(date!!)
    } catch (e: Exception) {
        time24
    }
}


@Preview(showBackground = true)
@Composable
fun CreateEventScreenPreview() {
    MaterialTheme {
        CreateEventScreen(
            onSave = {},
            onBack = {}
        )
    }
}