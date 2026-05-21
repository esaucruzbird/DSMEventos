package com.example.dsmevento.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.example.dsmevento.data.model.Review
import com.example.dsmevento.data.repository.EventRepository

class ReviewViewModel : ViewModel() {

    private val repository = EventRepository()

    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var reviewsListener: ListenerRegistration? = null

    fun startReviewsListener(eventId: String) {
        reviewsListener?.remove()
        reviewsListener = repository.listenReviews(eventId) { list ->
            reviews = list
        }
    }

    fun addOrUpdateReview(
        eventId: String,
        review: Review,
        onSuccess: () -> Unit
    ) {
        loading = true
        errorMessage = null

        repository.addOrUpdateReview(
            eventId = eventId,
            review = review,
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

    fun deleteReview(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit
    ) {
        loading = true
        errorMessage = null

        repository.deleteReview(
            eventId = eventId,
            userId = userId,
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

    override fun onCleared() {
        reviewsListener?.remove()
        super.onCleared()
    }
}