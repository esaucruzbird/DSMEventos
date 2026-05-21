package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.data.model.Review
import com.example.dsmevento.ui.components.ReviewItem
import com.example.dsmevento.viewmodel.AuthViewModel
import com.example.dsmevento.viewmodel.EventViewModel
import com.example.dsmevento.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    eventId: String,
    onBack: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val reviewViewModel: ReviewViewModel = viewModel()

    val currentUser = authViewModel.currentUser
    val selectedEvent = eventViewModel.selectedEvent
    val reviews = reviewViewModel.reviews

    var ratingText by remember { mutableStateOf("5") }
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
        eventViewModel.loadEventById(eventId)
        reviewViewModel.startReviewsListener(eventId)
    }

    val isPast = selectedEvent?.date?.let { it < System.currentTimeMillis() } ?: false
    val isAttendee = currentUser?.uid?.let { uid ->
        selectedEvent?.attendees?.contains(uid) == true
    } ?: false
    val canReview = isPast && isAttendee

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reseñas") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (canReview) {
                OutlinedTextField(
                    value = ratingText,
                    onValueChange = { ratingText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Calificación 1-5") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Comentario") },
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val rating = ratingText.toIntOrNull() ?: 0
                        val review = Review(
                            uid = currentUser?.uid.orEmpty(),
                            comment = comment,
                            createdAt = System.currentTimeMillis(),
                            name = currentUser?.displayName
                                ?.takeIf { it.isNotBlank() }
                                ?: currentUser?.email.orEmpty(),
                            rating = rating
                        )

                        reviewViewModel.addOrUpdateReview(eventId, review) {
                            comment = ""
                            ratingText = "5"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar reseña")
                }

                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Text("Solo los asistentes de un evento finalizado pueden agregar reseñas.")
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (reviews.isEmpty()) {
                Text("Todavía no hay reseñas.")
            } else {
                reviews.forEach { review ->
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewItem(review = review)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}