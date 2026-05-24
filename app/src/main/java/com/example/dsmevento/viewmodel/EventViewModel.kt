package com.example.dsmevento.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.data.repository.EventRepository

class EventViewModel : ViewModel() {

    private val repository = EventRepository()

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var history by mutableStateOf<List<Event>>(emptyList())
        private set

    var selectedEvent by mutableStateOf<Event?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var eventsListener: ListenerRegistration? = null
    private var historyListener: ListenerRegistration? = null

    fun startEventsListener() {
        eventsListener?.remove()
        eventsListener = repository.listenEvents { list ->
            events = list
        }
    }

    fun loadEventById(eventId: String) {
        repository.getEvent(eventId) { event ->
            selectedEvent = event
        }
    }

    fun startHistoryListener(userId: String) {
        historyListener?.remove()
        historyListener = repository.listenHistory(userId) { list ->
            history = list
        }
    }

    fun saveEvent(
        event: Event,
        onSuccess: () -> Unit
    ) {
        loading = true
        errorMessage = null

        repository.saveEvent(
            event = event,
            onSuccess = {
                loading = false
                onSuccess()
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun deleteEvent(eventId: String) {
        loading = true
        errorMessage = null

        repository.deleteEvent(
            eventId = eventId,
            onSuccess = {
                loading = false
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun confirmAttendance(
        eventId: String,
        uid: String,
        onSuccess: (() -> Unit)? = null
    ) {
        loading = true
        errorMessage = null

        repository.confirmAttendance(
            eventId = eventId,
            uid = uid,
            onSuccess = {
                loading = false
                updateSelectedEventAttendance(eventId, uid, add = true)
                onSuccess?.invoke()
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun cancelAttendance(
        eventId: String,
        uid: String,
        onSuccess: (() -> Unit)? = null
    ) {
        loading = true
        errorMessage = null

        repository.cancelAttendance(
            eventId = eventId,
            uid = uid,
            onSuccess = {
                loading = false
                updateSelectedEventAttendance(eventId, uid, add = false)
                onSuccess?.invoke()
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun clearSelectedEvent() {
        selectedEvent = null
    }

    private fun updateSelectedEventAttendance(eventId: String, uid: String, add: Boolean) {
        val current = selectedEvent ?: return
        if (current.uid != eventId) return

        selectedEvent = if (add) {
            if (uid in current.attendees) current
            else current.copy(attendees = current.attendees + uid)
        } else {
            current.copy(attendees = current.attendees.filterNot { it == uid })
        }
    }

    override fun onCleared() {
        eventsListener?.remove()
        historyListener?.remove()
        super.onCleared()
    }
}