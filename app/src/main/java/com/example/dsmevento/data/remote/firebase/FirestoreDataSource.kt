package com.example.dsmevento.data.remote.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.data.model.Review

class FirestoreDataSource {

    private val db = FirebaseFirestore.getInstance()

    fun listenEvents(onChange: (List<Event>) -> Unit): ListenerRegistration {
        return db.collection("events")
            .addSnapshotListener { snapshot, _ ->
                val events = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toEventSafely()
                }.sortedBy { it.date }

                onChange(events)
            }
    }

    fun getEvent(eventId: String, onResult: (Event?) -> Unit) {
        db.collection("events").document(eventId)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.toEventSafely())
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
        val ref = if (event.uid.isBlank()) {
            db.collection("events").document()
        } else {
            db.collection("events").document(event.uid)
        }

        val eventToSave = event.copy(
            uid = ref.id,
            createdAt = if (event.createdAt == 0L) System.currentTimeMillis() else event.createdAt
        )

        ref.set(eventToSave, SetOptions.merge())
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
        db.collection("events").document(eventId)
            .delete()
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
        return db.collection("events")
            .addSnapshotListener { snapshot, _ ->
                val now = System.currentTimeMillis()
                val history = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toEventSafely()
                }.filter { event ->
                    event.date < now && event.attendees.contains(userId)
                }.sortedByDescending { it.date }

                onChange(history)
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
                    doc.toReviewSafely()
                }.sortedByDescending { it.createdAt }

                onChange(reviews)
            }
    }

    fun addOrUpdateReview(
        eventId: String,
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = review.uid.trim()
        if (userId.isBlank()) {
            onError("No se pudo identificar al usuario.")
            return
        }

        val ref = db.collection("events")
            .document(eventId)
            .collection("reviews")
            .document(userId)

        val reviewToSave = review.copy(
            uid = userId,
            createdAt = if (review.createdAt == 0L) System.currentTimeMillis() else review.createdAt
        )

        ref.set(reviewToSave, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo guardar la reseña.")
            }
    }

    fun deleteReview(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("events")
            .document(eventId)
            .collection("reviews")
            .document(userId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo eliminar la reseña.")
            }
    }

    private fun DocumentSnapshot.toEventSafely(): Event? {
        val data = data ?: return null

        return Event(
            uid = id,
            createdAt = data["createdAt"].asMillis(),
            date = data["date"].asMillis(),
            description = data["description"] as? String ?: "",
            location = data["location"] as? String ?: "",
            name = data["name"] as? String ?: "",
            attendees = (data["attendees"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            finished = data["finished"] as? Boolean ?: false
        )
    }

    private fun DocumentSnapshot.toReviewSafely(): Review? {
        val data = data ?: return null

        return Review(
            uid = data["uid"] as? String ?: id,
            comment = data["comment"] as? String ?: "",
            createdAt = data["createdAt"].asMillis(),
            name = data["name"] as? String ?: "",
            rating = (data["rating"] as? Number)?.toInt() ?: 0
        )
    }

    private fun Any?.asMillis(): Long {
        return when (this) {
            is Long -> this
            is Int -> this.toLong()
            is Double -> this.toLong()
            is Float -> this.toLong()
            is Number -> this.toLong()
            is Timestamp -> this.toDate().time
            is Date -> this.time
            else -> 0L
        }
    }
}