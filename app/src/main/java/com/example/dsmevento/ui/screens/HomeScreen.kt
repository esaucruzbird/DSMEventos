package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.ui.components.EventCard
import com.example.dsmevento.ui.theme.EventAttendingOrange
import com.example.dsmevento.ui.theme.EventPastGray
import com.example.dsmevento.ui.theme.EventUpcomingGreen
import com.example.dsmevento.viewmodel.AuthViewModel
import com.example.dsmevento.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateEvent: () -> Unit,
    onOpenEvent: (String) -> Unit,
    onEditEvent: (String) -> Unit,
    onOpenHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    val currentUser = authViewModel.currentUser
    val events = eventViewModel.events

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
                    TextButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Text("Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentUser?.role == "organizer" || currentUser?.role == "admin") {
                FloatingActionButton(onClick = onCreateEvent) {
                    Icon(Icons.Default.Add, contentDescription = "Crear evento")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (events.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No hay eventos registrados todavía.")
                        }
                    }
                }
            } else {
                items(events, key = { it.uid }) { event ->
                    val isPast = event.date < System.currentTimeMillis()
                    val isAttending = currentUser?.uid?.let { uid ->
                        event.attendees.contains(uid)
                    } ?: false

                    val cardColor = when {
                        isPast -> EventPastGray
                        isAttending -> EventAttendingOrange
                        else -> EventUpcomingGreen
                    }

                    EventCard(
                        event = event,
                        isPastEvent = isPast,
                        canEditDelete = currentUser?.role == "organizer" || currentUser?.role == "admin",
                        containerColor = cardColor,
                        onClick = { onOpenEvent(event.uid) },
                        onEdit = { onEditEvent(event.uid) },
                        onDelete = { eventViewModel.deleteEvent(event.uid) }
                    )
                }
            }
        }
    }
}