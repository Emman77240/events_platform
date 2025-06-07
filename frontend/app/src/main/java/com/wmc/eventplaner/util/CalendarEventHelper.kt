package com.wmc.eventplaner.util

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.core.content.ContextCompat
import com.wmc.eventplaner.data.dto.Event
import java.util.Calendar
import java.util.TimeZone
class CalendarEventHelper(private val context: Context) {

    fun getWritableCalendars(): List<CalendarAccount> {
        val calendars = mutableListOf<CalendarAccount>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val selection = "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?"
        val selectionArgs = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())

        try {
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    calendars.add(
                        CalendarAccount(
                            id = cursor.getLong(0),
                            name = cursor.getString(1) ?: "Unknown",
                            account = cursor.getString(2) ?: "Unknown",
                            type = cursor.getString(3) ?: "Unknown"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("CalendarHelper", "Error reading calendars", e)
        }

        return calendars
    }

    fun addEventToCalendar(
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
        location: String,
        calendarId: Long? = null,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val calendars = getWritableCalendars()
            if (calendars.isEmpty()) {
                onError("No writable calendars found")
                return
            }

            val targetCalendarId = calendarId ?: calendars.first().id

            val values = ContentValues().apply {
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.EVENT_LOCATION, location)
                put(CalendarContract.Events.DTSTART, startTime)
                put(CalendarContract.Events.DTEND, endTime)
                put(CalendarContract.Events.CALENDAR_ID, targetCalendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.ALL_DAY, 0)
            }

            val uri = context.contentResolver.insert(
                CalendarContract.Events.CONTENT_URI,
                values
            ) ?: throw Exception("Failed to insert event")

            val eventId = ContentUris.parseId(uri)
            onSuccess(eventId)

        } catch (e: Exception) {
            onError("Error: ${e.localizedMessage ?: "Unknown error"}")
        }}


    data class CalendarAccount(
        val id: Long,
        val name: String,
        val account: String,
        val type: String
    )

    fun hasCalendarPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
    }

}

