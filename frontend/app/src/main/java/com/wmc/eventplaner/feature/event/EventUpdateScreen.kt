package com.wmc.eventplaner.feature.event

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
import com.wmc.eventplaner.data.dto.Event
import com.wmc.eventplaner.data.dto.EventCreationResponse
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.feature.home.HomeViewModel
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Calendar

@Composable
fun EventUpdateScreenRoute(
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel()
){
    val updateEventState by viewModel.updateEventState
    val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState
    val eventEdit by shareViewModel.eventEdit.collectAsStateWithLifecycle()
    val userDetail by shareViewModel.authData.collectAsStateWithLifecycle()
    EventUpdateScreen(
        onSave = {
            it.email = userDetail?.user?.email
            it.id = eventEdit?.id
            Log.d("TAG", "EventUpdateScreenRoute: event id ${it.id}")
            viewModel.updateEvent(it)
        },
        onBack = {
            onBack
        },
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        eventEditDetail = eventEdit,
        updateEventState = updateEventState,
        updateEventSuccess = {
            viewModel.updateEventState.value=null
            shareViewModel.isAllEventCall= true
            onBack()
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventUpdateScreen(
    onSave: (CreateEventRequest) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onDismissError: () -> Unit = {},
    eventEditDetail: Event? = null,
    updateEventState: EventCreationResponse? = null,
    updateEventSuccess: () -> Unit = {}
) {
    // State variables initialized with event details if available
    var eventTitle by remember { mutableStateOf(eventEditDetail?.title ?: "") }
    var description by remember { mutableStateOf(eventEditDetail?.description ?: "") }
    var date by remember { mutableStateOf(eventEditDetail?.date ?: "") }
    var startTime by remember { mutableStateOf(eventEditDetail?.startDateTime ?: "") }
    var endTime by remember { mutableStateOf(eventEditDetail?.endDateTime ?: "") }
    var location by remember { mutableStateOf(eventEditDetail?.location ?: "") }
    var imageFile by remember { mutableStateOf(eventEditDetail?.imageUrl ?: null) }

    // Track original values for change detection
    val originalValues = remember(eventEditDetail) {
        Event(
            id = eventEditDetail?.id,
            title = eventEditDetail?.title ?: "",
            description = eventEditDetail?.description ?: "",
            date = eventEditDetail?.date ?: "",
            startDateTime = eventEditDetail?.startDateTime ?: "",
            endDateTime = eventEditDetail?.endDateTime ?: "",
            location = eventEditDetail?.location ?: "",
            imageUrl = eventEditDetail?.imageUrl ?: null
        )
    }

    // Check if any field has changed
    val hasChanges by remember(eventTitle, description, date, startTime, endTime, location, imageFile) {
        derivedStateOf {
            eventTitle != originalValues.title ||
                    description != originalValues.description ||
                    date != originalValues.date ||
                    startTime != originalValues.startDateTime ||
                    endTime != originalValues.endDateTime ||
                    location != originalValues.location ||
                    imageFile != originalValues.imageUrl
        }
    }

    // Validation states
    var titleError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var startTimeError by remember { mutableStateOf(false) }
    var endTimeError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }

    // Date/Time pickers state
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Initialize with current date/time if creating new event
    val currentDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    val currentTime = remember {
        SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
    }

    // Formatters
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val context = LocalContext.current

    // State for selected image
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Handle API responses

        updateEventState?.let {
            SuccessDialog(
                title = "Success",
                message = updateEventState.responseMessage?:"Event Update successfully",
                onDismiss = { updateEventSuccess()  },
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

    // Image picker launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            imageFile = it.toString()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventEditDetail != null) "Edit Event" else "Create Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
                value = date.ifEmpty { currentDate },
                onValueChange = { /* Handled by date picker */ },
                placeholder = "Date*",
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
                    Text(
                        text = "Start Time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    CustomTextField(
                        value = startTime.ifEmpty { currentTime },
                        onValueChange = {},
                        placeholder = "Start Time*",
                        readOnly = true,
                        isError = startTimeError,
                        supportingText = if (startTimeError) "Start time is required" else null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        trailingIcon = Icons.Default.DateRange,
                        onTrailingIconClick = { showStartTimePicker = true },
                    )
                }

                // End Time
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End Time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    CustomTextField(
                        value = endTime.ifEmpty { currentTime },
                        onValueChange = {},
                        placeholder = "End Time*",
                        readOnly = true,
                        isError = endTimeError,
                        supportingText = if (endTimeError) "End time is required" else null,
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
                text = if (imageFile != null) "Change Image" else "Upload Image"
            )

            // Show current image or new selection
            (imageFile ?: eventEditDetail?.imageUrl)?.let { imageUri ->
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Event image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button - Enabled only when there are changes
            CustomButton(
                text = if (eventEditDetail != null) "Update Event" else "Create Event",
                onClick = {
                    // Validate fields
                    titleError = eventTitle.isBlank()
                    dateError = date.isBlank()
                    startTimeError = startTime.isBlank()
                    endTimeError = endTime.isBlank()
                    locationError = location.isBlank()

                    if (!titleError && !dateError && !startTimeError && !endTimeError && !locationError) {
                        val cacheFile = if (!imageFile.isNullOrEmpty() && !imageFile?.startsWith("http")!!) {
                            try {
                                val uri = imageFile?.toUri()
                            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri!!, "r", null)
                                    ?: throw IOException("Failed to open file")

                                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

                                File(context.cacheDir, "event_image.jpg").apply {
                                    outputStream().use { output ->
                                        inputStream.use { input ->
                                            input.copyTo(output)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        } else {
                            null
                        }


                        onSave(
                            CreateEventRequest(
                                title = eventTitle,
                                description = description,
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                location = location,
                                imageFile = cacheFile
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = hasChanges // Only enable if there are changes
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
        val time = if (startTime.isNotBlank()) startTime else currentTime
        val (initialHour, initialMinute) = extractHourMinute(time)

        CustomTimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24HourFormat = false,
            onDismissRequest = { showStartTimePicker = false },
            onTimeSelected = { hour, minute ->
                startTime = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                showStartTimePicker = false
            }
        )
    }

    // End Time Picker Dialog
    if (showEndTimePicker) {
        val time = if (endTime.isNotBlank()) endTime else currentTime
        val (initialHour, initialMinute) = extractHourMinute(time)

        CustomTimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24HourFormat = false,
            onDismissRequest = { showEndTimePicker = false },
            onTimeSelected = { hour, minute ->
                endTime = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                showEndTimePicker = false
            }
        )
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


@Preview(showBackground = true)
@Composable
fun EventUpdateScreenPreview() {
    MaterialTheme {
        EventUpdateScreen(
            onSave = {},
            onBack = {}
        )
    }
}