package com.example.dsmevento.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.ui.components.ConfirmDialog
import com.example.dsmevento.util.formatMillisToDateTime
import com.example.dsmevento.viewmodel.AuthViewModel
import com.example.dsmevento.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onOpenReviews: (String) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val context = LocalContext.current

    val currentUser = authViewModel.currentUser
    val currentEvent = eventViewModel.selectedEvent
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        authViewModel.loadCurrentUser()
        eventViewModel.loadEventById(eventId)
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Eliminar evento",
            message = "¿Seguro que deseas eliminar este evento?",
            confirmText = "Eliminar",
            onConfirm = {
                eventViewModel.deleteEvent(eventId)
                showDeleteDialog = false
                onBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle del evento") })
        }
    ) { padding ->
        if (currentEvent == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Cargando evento...")
            }
            return@Scaffold
        }

        val isPast = currentEvent.date < System.currentTimeMillis()
        val isAttendee = currentUser?.uid?.let { uid ->
            currentEvent.attendees.contains(uid)
        } ?: false

        val canReview = isPast && isAttendee

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = currentEvent.name,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha: ${formatMillisToDateTime(currentEvent.date)}")
            Text("Lugar: ${currentEvent.location}")
            Spacer(modifier = Modifier.height(12.dp))
            Text(currentEvent.description)

            Spacer(modifier = Modifier.height(20.dp))

            if (!isPast) {
                if (isAttendee) {
                    Button(
                        onClick = {
                            currentUser?.uid?.let { uid ->
                                eventViewModel.cancelAttendance(eventId, uid)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar asistencia")
                    }
                } else {
                    Button(
                        onClick = {
                            currentUser?.uid?.let { uid ->
                                eventViewModel.confirmAttendance(eventId, uid)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar asistencia")
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { onOpenReviews(eventId) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canReview
                ) {
                    Text("Ver / agregar reseñas")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, "Evento: ${currentEvent.name}")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Te comparto este evento:\n\n" +
                                    "Nombre: ${currentEvent.name}\n" +
                                    "Fecha: ${formatMillisToDateTime(currentEvent.date)}\n" +
                                    "Lugar: ${currentEvent.location}\n\n" +
                                    currentEvent.description
                        )
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compartir por correo")
            }

            if (currentUser?.role == "organizer" || currentUser?.role == "admin") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onEdit(eventId) },
                        enabled = !isPast
                    ) {
                        Text("Editar")
                    }
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !isPast
                    ) {
                        Text("Eliminar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TextButton(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}