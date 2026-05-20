package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.ui.components.EventCard
import com.example.dsmevento.viewmodel.AuthViewModel
import com.example.dsmevento.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateEvent: () -> Unit,
    onOpenEvent: (String) -> Unit,
    onOpenHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    val events by eventViewModel.events
    val currentUser by authViewModel.currentUser

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
        eventViewModel.startEventsListener()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos") },
                actions = {
                    TextButton(onClick = onOpenHistory) {
                        Text("Historial")
                    }
                    TextButton(onClick = onLogout) {
                        Text("Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentUser?.role == "organizer" || currentUser?.role == "admin") {
                FloatingActionButton(onClick = onCreateEvent) {
                    Icon(Icons.Default.EventNote, contentDescription = "Crear evento")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay eventos registrados todavía.")
                }
            } else {
                events.forEach { event ->
                    Spacer(modifier = Modifier.height(8.dp))
                    EventCard(
                        event = event,
                        isPastEvent = event.date < System.currentTimeMillis(),
                        canEditDelete = currentUser?.role == "organizer" || currentUser?.role == "admin",
                        onClick = { onOpenEvent(event.uid) },
                        onEdit = { onCreateEvent() },
                        onDelete = { eventViewModel.deleteEvent(event.uid) }
                    )
                }
            }
        }
    }
}