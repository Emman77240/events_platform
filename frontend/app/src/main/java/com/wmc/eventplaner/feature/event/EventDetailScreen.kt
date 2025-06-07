package com.wmc.eventplaner.feature.event

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.wmc.eventplaner.R
import com.wmc.eventplaner.common.ErrorDialog
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.common.LoadingDialog
import com.wmc.eventplaner.data.dto.EventDetails
import com.wmc.eventplaner.data.dto.EventDetailsData
import com.wmc.eventplaner.data.dto.EventGetByIdRequest
import com.wmc.eventplaner.data.dto.GetEventByIdResponse
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.feature.home.HomeViewModel
import com.wmc.eventplaner.util.formatDateTime
import com.wmc.eventplaner.ui.theme.Black

@Composable
fun EventDetailsScreenRoute(onBack: () -> Boolean,
                            viewModel: HomeViewModel = hiltViewModel(),
                            shareViewModel: ShareViewModel) {
    val item  = shareViewModel.eventEdit.collectAsStateWithLifecycle()
    val authData  = shareViewModel.authData.collectAsStateWithLifecycle()
     val isLoading by viewModel.isLoading
    val errorState by viewModel.errorState
    val eventDetails by viewModel.eventDetailState

    val request = EventGetByIdRequest(id = item.value?.id,
        email =authData.value?.user?.email)
    LaunchedEffect(Unit) {

        viewModel.getEventDetail(request=request)
    }
    EventDetailsScreen(
        onBack = {onBack()},
        isLoading = isLoading,
        errorState = errorState,
        onDismissError = viewModel::clearError,
        eventDetails = eventDetails?.data
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventDetails: EventDetailsData? = null,
    onBack: () -> Unit = {},
    isLoading: Boolean = false,
    errorState: ErrorState? = null,
    onDismissError: () -> Unit = {},
) {
    // Parallax scroll state
    val scrollState = rememberScrollState()
    val imageHeight = 280.dp
    val maxOffset = with(LocalDensity.current) { imageHeight.toPx() * 0.5f }
    val offset = minOf(maxOffset, scrollState.value * 0.5f)
    LaunchedEffect(eventDetails) {
        eventDetails?.let {
            Log.d("TAG", "EventDetailsScreen: ")
        }
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details",color = Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            // Parallax Image Background
            eventDetails?.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Event image background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight + offset.dp)
                        .offset(y = -offset.dp)
                        .graphicsLayer {
                            alpha = 0.9f
                        }
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight + offset.dp)
                        .offset(y = -offset.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.surface
                                ),
                                startY = 0f,
                                endY = with(LocalDensity.current) { imageHeight.toPx() }
                            )
                        )
                )
            }

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(imageHeight - 56.dp))

                // Event Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Event Title with fancy typography
                        Text(
                            text = eventDetails?.title ?: "No title",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Status Chip
                        Row {
                            eventDetails?.status?.let { status ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            when (status.lowercase()) {
                                                "active" -> Color.Green.copy(alpha = 0.2f)
                                                "soldout" -> Color.Red.copy(alpha = 0.2f)
                                                "upcoming" -> Color.Blue.copy(alpha = 0.2f)
                                                else -> Color.Gray.copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = status,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when (status.lowercase()) {
                                            "active" -> Color.Green
                                            "soldout" -> Color.Red
                                            "upcoming" -> Color.Blue
                                            else -> Color.Gray
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Public/Private Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (eventDetails?.isPublic == true)
                                            Color.Blue.copy(alpha = 0.2f)
                                        else
                                            Color.Gray.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (eventDetails?.isPublic == true) "PUBLIC" else "PRIVATE",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (eventDetails?.isPublic == true) Color.Blue else Color.Gray
                                )
                            }
                        }

                        // Description with improved styling
                        Text(
                            text = eventDetails?.description ?: "No description available",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )

                        // Details Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Date and Time
                            DetailRow(
                                icon = Icons.Default.DateRange,
                                title = "Date & Time",
                                content = buildString {
                                    append(eventDetails?.startDateTime?.formatDateTime() ?: "Not specified")
                                    eventDetails?.eventDate?.let { endTime ->
                                        append(" to ${endTime.formatDateTime()}")
                                    }
                                }
                            )

                            // Location
                            DetailRow(
                                icon = Icons.Default.LocationOn,
                                title = "Location",
                                content = eventDetails?.location ?: "Location not specified"
                            )

                            // Max Attendees
                            DetailRow(icon = Icons.Default.Person, title = "Capacity", content = eventDetails?.maxAttendees?.toString() ?: "No limit")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                if (false){
                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* Handle RSVP */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.sync_to_calendar))
                    }

                    OutlinedButton(
                        onClick = { /* Handle Share */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Share Event")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))}
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, title: String, content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsScreenPreview() {
    MaterialTheme {
        EventDetailsScreen(
            eventDetails = null
        )
    }
}

val dummyEventDetails = EventDetails(
    id = 12345,
    title = "Tech Conference 2023",
    description = "Annual technology conference featuring the latest innovations in AI, blockchain, and cloud computing. Join us for three days of workshops, keynotes, and networking with industry leaders.",
    startTime = "2023-11-15T09:00:00",
    endTime = "2023-11-17T18:00:00",
    location = "Convention Center, 747 Howard St, San Francisco, CA 94103",
    imageUrl = "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2000&q=80",
    status = "Active",
    maxAttendees = 500,
    isPublic = true
)