package com.example.dsmevento.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    val currentUser by authViewModel.currentUser
    val event by eventViewModel.selectedEvent

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        authViewModel.loadCurrentUser()
        eventViewModel.loadEventById(eventId)
    }

    val isPast = event?.date?.let { it < System.currentTimeMillis() } ?: false
    val isAttendee = currentUser?.uid != null && event?.attendees?.contains(currentUser?.uid) == true
    val canReview = isPast && isAttendee

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
            TopAppBar(
                title = { Text("Detalle del evento") }
            )
        }
    ) { padding ->
        if (event == null) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = event!!.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha: ${formatMillisToDateTime(event!!.date)}")
            Text("Lugar: ${event!!.location}")
            Spacer(modifier = Modifier.height(12.dp))
            Text(event!!.description)

            Spacer(modifier = Modifier.height(20.dp))

            if (!isPast) {
                if (isAttendee) {
                    Button(
                        onClick = { eventViewModel.cancelAttendance(eventId, currentUser!!.uid) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar asistencia")
                    }
                } else {
                    Button(
                        onClick = { eventViewModel.confirmAttendance(eventId, currentUser?.uid.orEmpty()) },
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
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_SUBJECT, "Evento: ${event!!.name}")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Te comparto este evento:\n\n" +
                                    "Nombre: ${event!!.name}\n" +
                                    "Fecha: ${formatMillisToDateTime(event!!.date)}\n" +
                                    "Lugar: ${event!!.location}\n\n" +
                                    "${event!!.description}"
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