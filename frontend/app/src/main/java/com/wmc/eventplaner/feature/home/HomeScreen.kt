package com.wmc.eventplaner.feature.home

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.CalendarContract
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import com.wmc.eventplaner.R
import com.wmc.eventplaner.common.ErrorDialog
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.common.LoadingDialog
import com.wmc.eventplaner.common.LogoutConfirmationDialog
import com.wmc.eventplaner.common.ScreenContainer
import com.wmc.eventplaner.common.SuccessDialog
import com.wmc.eventplaner.data.dto.Event
import com.wmc.eventplaner.data.dto.EventCreationResponse
import com.wmc.eventplaner.data.dto.EventGetByIdRequest
import com.wmc.eventplaner.data.dto.EventsResponse
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.data.dto.User
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.ui.theme.Black
import com.wmc.eventplaner.util.CalendarEventHelper
import com.wmc.eventplaner.util.formatDateTime
import com.wmc.eventplaner.util.rememberToast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun HomeScreenRoute(
    onAddEvent: () -> Unit,
    onEventClick: () -> Unit,
    onEdit: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {

    // Observe viewModel states
    val userEventState by viewModel.allEventState
    val adminEventState by viewModel.adminEventState
    val createEventState by viewModel.createEventState
    var deleteEventState by viewModel.deleteEventState
    val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState
    val userDetail by shareViewModel.authData.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        if (userEventState==null|| shareViewModel.isAllEventCall){
            shareViewModel.isAllEventCall= false
            viewModel.allEventState.value=null
            viewModel.getAllEvent()
        }
    }
    BackHandler {
        onBack() // This will call `activity?.finish()` from above
    }
    HomeScreen(
        onAddEvent = { onAddEvent() },
        onEventClick = {
            shareViewModel.setEditEvent(it)
            onEventClick()
        },
        modifier = Modifier,
        userDetail = userDetail?.user,
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        getAllEventState = userEventState,
        adminEventState = adminEventState,
        createEventState = createEventState,
        deleteEventState = deleteEventState,
        onDelete = {
            val request =EventGetByIdRequest(id = it.id, email = userDetail?.user?.email)
            viewModel.deleteEvent(request)
        },
        onEdit = {
            shareViewModel.setEditEvent(it)
            onEdit()
        },
        onDeleteSuccess = {
            viewModel.allEventState.value=null
            viewModel.deleteEventState.value = null
            viewModel.getAllEvent()
        },
        onLogout = {
            viewModel.clearError()
            viewModel.clearAuthInfo()
            onLogout()}

    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddEvent: () -> Unit,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onDismissError: () -> Unit = {},
    getAllEventState: EventsResponse? = null,
    adminEventState: LoginResponse? = null,
    createEventState: EventCreationResponse? = null,
    deleteEventState: EventCreationResponse? = null,
    userDetail: User? = null,
    onDelete: (Event) -> Unit = {},
    onEdit: (Event) -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    refreshCashe: Boolean = false,
    onLogout: () -> Unit = {},

    ) {
    // Replace with actual user data from your state or authentication
    val isAdmin = remember { userDetail?.role?.lowercase() == "Admin".lowercase() }
    // State for showing/hiding the menu

    //  val dummyEvents = generateDummyEvents()
    val refreshCashe = remember { mutableStateOf(refreshCashe) }

    // Handle success
    LaunchedEffect(getAllEventState) {
        getAllEventState?.let {

        }
    }
    deleteEventState?.let {
        SuccessDialog(
            title = "Success",
            message = deleteEventState.responseMessage?:"Event is deleted successfully",
            onDismiss = { onDeleteSuccess()  },
            onOkay = {
                onDeleteSuccess()
            }
        )

    }

    if (isLoading) {
        LoadingDialog()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirmLogout = {
                onLogout()

            }
        )
    }

    errorState?.let { error ->
        ErrorDialog(
            message = error.message ?: "Something went wrong",
            onDismiss = {
                onDismissError()
            },
            onRetry = error.retryAction,
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = if (isAdmin) "Admin Dashboard" else "My Events",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                },
                actions = {
                    IconButton(modifier = Modifier.padding(end = 15.dp),onClick = { showLogoutDialog =true }) {
                        Icon(
                            painter = painterResource(R.drawable.logout_) ,
                            contentDescription = "Logout",
                            tint = Black
                        )
                    }
                }
            )

        },
        floatingActionButton = {
            if (isAdmin) { // Only show FAB for admin
                FloatingActionButton(
                    onClick = onAddEvent,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        },
        modifier = modifier
    ) { padding ->
        ScreenContainer {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Spacer(modifier= Modifier.padding(top = 8.dp))
                    Text(
                        text = "Welcome, ${userDetail?.fullName ?: ""}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (isAdmin) {
                            "You have admin privileges to manage all events"
                        } else {
                            "Here are your upcoming events"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )

                    if (isAdmin) {
                        Text(
                            text = "You can create, edit, and delete events",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Events list
                    items(getAllEventState?.data ?: emptyList()) { event ->
                        EventCard(
                            event = event,
                            onClick = { onEventClick(event) },
                            isAdmin = isAdmin,
                            onEditEvent = { onEdit(event) },
                            onDeleteEvent = { onDelete(event) },

                            )
                    }
                }
            }

        }

    }
}


@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onEditEvent: () -> Unit = {},
    onDeleteEvent: () -> Unit = {},
) {
    // State management
    var showMenu by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    // Context and helpers
    val context = LocalContext.current
    val calendarHelper = remember { CalendarEventHelper(context) }
    val showToast = rememberToast()

    // Date formatting
    val dateFormats = rememberDateFormats()
//    val (displayDate, displayTime) = remember(event) {
//        formatEventDates(event, dateFormats)
//    }
    val displayDate =event.date?.formatDateTime()
    val displayTime =event.startDateTime?.formatDateTime()
    val permissions = arrayOf(
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.READ_CALENDAR
    )

    // Calendar permission launcher
    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false

        handleCalendarPermissionResult(
            writeGranted && readGranted, // Only proceed if both are granted
            calendarHelper,
            event,
            showToast,
            context,
            showDialog = { showCalendarDialog = true }
        )
    }

    // Main card content
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        EventContent(
            event = event,
            isAdmin = isAdmin,
            showMenu = showMenu,
            displayDate = displayDate?:"",
            displayTime = displayTime?:"",
            onMenuClick = { showMenu = true },
            onAddToCalendar = { handleAddToCalendarClick(
                calendarHelper,
                calendarPermissionLauncher,
                showToast,
                showDialog ={showCalendarDialog= true},
                context = context,
                event =event
            )},
            onEditEvent = {
                onEditEvent()
                showMenu = false
            },
            onDeleteEvent = {
                onDeleteEvent()
                showMenu = false
            }
        )
    }

    // Admin menu dropdown
    if (isAdmin && showMenu) {
        EventAdminDialog(
            onDismiss = { showMenu = false },
            onEditEvent = onEditEvent,
            onDeleteEvent = onDeleteEvent
        )
    }

    // Calendar selection dialog
    if (showCalendarDialog) {
        CalendarSelectionDialog(
            calendarHelper = calendarHelper,
            event = event,
            showToast = showToast,
            onDismiss = { showCalendarDialog = false },
            onCalendarSelected = { calendarId ->
                addEventToCalendar(
                    calendarHelper,
                    event,
                    calendarId,
                    showToast,
                    context
                )
                showCalendarDialog = false
            }
        )
    }
}

@Composable
private fun EventContent(
    event: Event,
    isAdmin: Boolean,
    showMenu: Boolean,
    displayDate: String,
    displayTime: String,
    onMenuClick: () -> Unit,
    onAddToCalendar: () -> Unit,
    onEditEvent: () -> Unit,
    onDeleteEvent: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrlWithTimestamp = "${event.imageUrl}?t=${System.currentTimeMillis()}"

        AsyncImage(
            model = imageUrlWithTimestamp,
            contentDescription = "Event image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.error_image)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EventHeader(
                title = event.title,
                isAdmin = isAdmin,
                onMenuClick = onMenuClick
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.DateRange, null, Modifier.size(16.dp))
                    Text(displayDate, style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                    Text(displayTime, style = MaterialTheme.typography.bodyMedium)
                }
            }
            event.location?.takeIf { it.isNotBlank() }?.let { location ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Button(
                onClick = onAddToCalendar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(Icons.Default.DateRange, null, Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.sync_to_calendar))
            }
        }
    }
}


@Composable
private fun EventHeader(
    title: String?,
    isAdmin: Boolean,
    onMenuClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title.orEmpty(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (isAdmin) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Event options"
                )
            }
        }
    }
}

@Composable
fun EventAdminDialog(
    onDismiss: () -> Unit,
    onEditEvent: () -> Unit,
    onDeleteEvent: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Manage Event",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                EventAdminOption(
                    icon = Icons.Default.Edit,
                    label = "Edit Event",
                    onClick = {
                        onEditEvent()
                        onDismiss()
                    }
                )
                EventAdminOption(
                    icon = Icons.Default.Delete,
                    label = "Delete Event",
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = {
                        onDeleteEvent()
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EventAdminOption(
    icon: ImageVector,
    label: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


//@Composable
//private fun EventAdminMenu(
//    onDismiss: () -> Unit,
//    onEditEvent: () -> Unit,
//    onDeleteEvent: () -> Unit
//) {
//    DropdownMenu(
//        expanded = true,
//        onDismissRequest = onDismiss,
//        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
//    ) {
//        DropdownMenuItem(
//            text = { Text("Edit") },
//            onClick = onEditEvent,
//            leadingIcon = { Icon(Icons.Default.Edit, null) }
//        )
//        DropdownMenuItem(
//            text = { Text("Delete") },
//            onClick = onDeleteEvent,
//            leadingIcon = { Icon(Icons.Default.Delete, null) }
//        )
//    }
//}

@Composable
private fun CalendarSelectionDialog(
    calendarHelper: CalendarEventHelper,
    event: Event,
    showToast: (String) -> Unit,
    onDismiss: () -> Unit,
    onCalendarSelected: (Long) -> Unit
) {
    val writableCalendars by remember {
        derivedStateOf {
            try {
                calendarHelper.getWritableCalendars()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Calendar", style = MaterialTheme.typography.titleLarge) },
        text = {
            LazyColumn(Modifier.heightIn(max = 400.dp)) {
                items(writableCalendars) { calendar ->
                    CalendarOptionItem(
                        calendar = calendar,
                        onClick = { onCalendarSelected(calendar.id) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@Composable
private fun CalendarOptionItem(
    calendar: CalendarEventHelper.CalendarAccount,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(Modifier.weight(1f)) {
                Text(
                    text = calendar.name ?: "Unnamed Calendar",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = calendar.account ?: "Unknown account",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// Helper functions
@Composable
private fun rememberDateFormats(): DateFormats {
    return remember {
        DateFormats(
            dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()),
            timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            },
            isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        )
    }
}

private fun formatEventDates(event: Event, dateFormats: DateFormats): Pair<String, String> {
    val parsedDate = try {
        dateFormats.isoFormat.parse(event.endDateTime.orEmpty())
    } catch (e: Exception) {
        null
    }

    return Pair(
        parsedDate?.let { dateFormats.dateFormat.format(it) } ?: event.endDateTime.orEmpty(),
        parsedDate?.let { dateFormats.timeFormat.format(it) } ?: event.endDateTime.orEmpty()
    )
}
fun CalendarEventHelper.isEventInCalendar(event: Event, context: Context): Boolean {
    if (!hasCalendarPermissions(context)) return false

    val projection = arrayOf(CalendarContract.Events._ID)

    val startTime = parseDateTime(event.date, event.startDateTime) ?: return false
    val endTime = parseDateTime(event.date, event.endDateTime) ?: return false

    val selection = """
        ${CalendarContract.Events.TITLE} = ? AND
        ${CalendarContract.Events.DTSTART} = ? AND
        ${CalendarContract.Events.DTEND} = ?
    """.trimIndent()

    val selectionArgs = arrayOf(
        event.title ?: "",
        startTime.toString(),
        endTime.toString()
    )

    return try {
        context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            cursor.moveToFirst()
        } ?: false
    } catch (e: Exception) {
        false
    }
}

private fun handleAddToCalendarClick(
    calendarHelper: CalendarEventHelper,
    permissionLauncher: ActivityResultLauncher<Array<String>>, // Changed to Array<String>
    showToast: (String) -> Unit,
    showDialog: (Boolean) -> Unit,
    context: Context,
    event: Event
) {
    // First check if event already exists
    if (calendarHelper.hasCalendarPermissions(context) &&
        calendarHelper.isEventInCalendar(context = context, event = event)) {
        showToast("Event is already in your calendar")
        return
    }

    when {
        !calendarHelper.hasCalendarPermissions(context) -> {
            // Launch both permissions at once
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR
                )
            )
        }
        calendarHelper.getWritableCalendars().isEmpty() -> {
            showToast("No writable calendars available")
        }
        calendarHelper.getWritableCalendars().size == 1 -> {
            addEventToCalendar(
                showToast = showToast,
                context = context,
                calendarId = calendarHelper.getWritableCalendars().first().id,
                event = event,
                calendarHelper = calendarHelper,
            )
        }
        else -> {
            showDialog(true)
        }
    }
}

private fun handleCalendarPermissionResult(
    granted: Boolean,
    calendarHelper: CalendarEventHelper,
    event: Event,
    showToast: (String) -> Unit,
    context: Context,
    showDialog: (Boolean) -> Unit
) {
    if (granted) {
        when {
            calendarHelper.getWritableCalendars().isEmpty() -> {
                showToast("No writable calendars available")
            }
            calendarHelper.getWritableCalendars().size == 1 -> {
                addEventToCalendar(
                    calendarHelper,
                    event,
                    calendarHelper.getWritableCalendars().first().id,
                    showToast,
                    context
                )
            }
            else -> {
                showDialog(true)
            }
        }
    } else {
        showToast("Calendar permission denied")
    }
}
private fun addEventToCalendar(
    calendarHelper: CalendarEventHelper,
    event: Event,
    calendarId: Long?,
    showToast: (String) -> Unit,
    context: Context
) {
    val startTime = parseDateTime(date = event.date,event.startDateTime)
    val endTime = parseDateTime(date = event.date,event.endDateTime)

    if (startTime == null || endTime == null) {
        showToast("Invalid date format")
        return
    }

    calendarHelper.addEventToCalendar(
        title = event.title ?: "Event",
        description = event.description ?: "",
        startTime = startTime,
        endTime = endTime,
        location = event.location ?: "",
        calendarId = calendarId,
        onSuccess = { eventId ->
            showToast("Added to calendar!")
            verifyEventExists(eventId, context, showToast)
        },
        onError = { error ->
            showToast(error)
        }
    )
}
private data class DateFormats(
    val dateFormat: SimpleDateFormat,
    val timeFormat: SimpleDateFormat,
    val isoFormat: SimpleDateFormat
)



private fun verifyEventExists(eventId: Long, context: Context, showToast: (String) -> Unit) {
    val projection = arrayOf(CalendarContract.Events.TITLE)
    val selection = "${CalendarContract.Events._ID} = ?"
    val selectionArgs = arrayOf(eventId.toString())

    try {
        context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                showToast("Verified in calendar!")
            } else {
                showToast("Event not found in calendar")
            }
        }
    } catch (e: Exception) {
        showToast("Verification failed: ${e.message}")
    }
}

private fun parseDateTime(date: String?, time: String?): Long? {
    if (date.isNullOrEmpty() || time.isNullOrEmpty()) return null
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        val dateTime = "$date $time"
        formatter.parse(dateTime)?.time
    } catch (e: Exception) {
        null
    }
}


// Compatible date creation for all API levels
private fun createDate(year: Int, month: Int, day: Int): Date {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Use modern API if available
        Date.from(
            LocalDate.of(year, month + 1, day)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant())
    } else {
        // Fallback for older APIs
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        calendar.time
    }
}


@Preview(showBackground = true)
@Composable
fun EventPlannerHomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onAddEvent = {},
            onEventClick = {}
        )
    }
}

val dummyEventResponse = EventsResponse(
        data = generateDummyEvents())
fun generateDummyEvents(): List<Event> {
    return listOf(
        Event(
            id = 1,
            title = "Tech Conference",
            description = "An annual tech conference with top speakers.",
            startDateTime = "2025-06-10T10:00:00",
            endDateTime = "2025-06-10T16:00:00",
            location = "Karachi Expo Center",
            status = "Scheduled",
            isPublic = true,
            imageUrl = "https://picsum.photos/id/237/200/300"
        ),
        Event(
            id = 2,
            title = "Startup Meetup",
            description = "Networking event for local startups.",
            startDateTime = "2025-06-15T14:00:00",
            endDateTime = "2025-06-15T17:00:00",
            location = "NEST I/O",
            status = "Upcoming",
            isPublic = false,
            imageUrl = "https://picsum.photos/200/300"
        ),
        Event(
            id = 3,
            title = "Hackathon",
            description = "48-hour coding challenge with prizes.",
            startDateTime = "2025-07-01T09:00:00",
            endDateTime = "2025-07-03T09:00:00",
            location = "IBA University",
            status = "Open",
            isPublic = true,
            imageUrl = "https://picsum.photos/200/300"
        )
        ,
         Event(
            id = 5,
            title = "AI Hackathon",
            description = "48-hour coding challenge with prizes.",
            startDateTime = "2025-07-01T09:00:00",
            endDateTime = "2025-07-03T09:00:00",
            location = "IBA University",
            status = "Open",
            isPublic = true,
            imageUrl = "https://picsum.photos/200/300"
        )


    )

}
