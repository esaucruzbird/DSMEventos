package com.example.dsmevento.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.data.model.Review
import com.example.dsmevento.data.remote.firebase.FirestoreDataSource

class EventRepository {

    private val dataSource = FirestoreDataSource()

    fun listenEvents(onChange: (List<Event>) -> Unit): ListenerRegistration =
        dataSource.listenEvents(onChange)

    fun getEvent(eventId: String, onResult: (Event?) -> Unit) =
        dataSource.getEvent(eventId, onResult)

    fun saveEvent(
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.saveEvent(event, onSuccess, onError)

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.deleteEvent(eventId, onSuccess, onError)

    fun confirmAttendance(
        eventId: String,
        uid: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.confirmAttendance(eventId, uid, onSuccess, onError)

    fun cancelAttendance(
        eventId: String,
        uid: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.cancelAttendance(eventId, uid, onSuccess, onError)

    fun listenHistory(
        userId: String,
        onChange: (List<Event>) -> Unit
    ): ListenerRegistration = dataSource.listenHistory(userId, onChange)

    fun listenReviews(
        eventId: String,
        onChange: (List<Review>) -> Unit
    ): ListenerRegistration = dataSource.listenReviews(eventId, onChange)

    fun addOrUpdateReview(
        eventId: String,
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.addOrUpdateReview(eventId, review, onSuccess, onError)

    fun deleteReview(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.deleteReview(eventId, userId, onSuccess, onError)
}