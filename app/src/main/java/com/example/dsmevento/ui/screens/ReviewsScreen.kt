package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    val currentUser by authViewModel.currentUser
    val selectedEvent by eventViewModel.selectedEvent
    val reviews by reviewViewModel.reviews

    var ratingText by remember { mutableStateOf("5") }
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
        eventViewModel.loadEventById(eventId)
        reviewViewModel.startReviewsListener(eventId)
    }

    val isPast = selectedEvent?.date?.let { it < System.currentTimeMillis() } ?: false
    val isAttendee = currentUser?.uid != null && selectedEvent?.attendees?.contains(currentUser?.uid) == true
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
                    label = { Text("Calificación 1-5") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario") },
                    modifier = Modifier.fillMaxWidth(),
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
                            name = currentUser?.displayName?.ifBlank { currentUser?.email.orEmpty() }
                                ?: "Usuario",
                            rating = rating
                        )

                        reviewViewModel.addReview(eventId, review) {
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