package com.example.dsmevento.data.remote.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.data.model.Review

class FirestoreDataSource {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun listenEvents(onChange: (List<Event>) -> Unit): ListenerRegistration {
        return db.collection("events")
            .addSnapshotListener { snapshot, _ ->
                val events = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(uid = doc.id)
                }.sortedBy { it.date }

                onChange(events)
            }
    }

    fun getEvent(
        eventId: String,
        onResult: (Event?) -> Unit
    ) {
        db.collection("events").document(eventId).get()
            .addOnSuccessListener { doc ->
                onResult(doc.toObject(Event::class.java)?.copy(uid = doc.id))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun saveEvent(
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = if (event.uid.isBlank()) db.collection("events").document() else db.collection("events").document(event.uid)
        val eventToSave = event.copy(
            uid = ref.id,
            createdAt = if (event.createdAt == 0L) System.currentTimeMillis() else event.createdAt
        )

        ref.set(eventToSave)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo guardar el evento.")
            }
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("events").document(eventId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo eliminar el evento.")
            }
    }

    fun confirmAttendance(
        eventId: String,
        uid: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("events").document(eventId)
            .update("attendees", FieldValue.arrayUnion(uid))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo confirmar la asistencia.")
            }
    }

    fun cancelAttendance(
        eventId: String,
        uid: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("events").document(eventId)
            .update("attendees", FieldValue.arrayRemove(uid))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo cancelar la asistencia.")
            }
    }

    fun listenHistory(
        userId: String,
        onChange: (List<Event>) -> Unit
    ): ListenerRegistration {
        return listenEvents { events ->
            val now = System.currentTimeMillis()
            onChange(events.filter { it.date < now && it.attendees.contains(userId) })
        }
    }

    fun listenReviews(
        eventId: String,
        onChange: (List<Review>) -> Unit
    ): ListenerRegistration {
        return db.collection("events")
            .document(eventId)
            .collection("reviews")
            .addSnapshotListener { snapshot, _ ->
                val reviews = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toObject(Review::class.java)?.copy(uid = doc.getString("uid") ?: "")
                }.sortedByDescending { it.createdAt }

                onChange(reviews)
            }
    }

    fun addReview(
        eventId: String,
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = db.collection("events")
            .document(eventId)
            .collection("reviews")
            .document()

        val reviewToSave = review.copy(
            createdAt = if (review.createdAt == 0L) System.currentTimeMillis() else review.createdAt
        )

        ref.set(reviewToSave)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo guardar la reseña.")
            }
    }
}