package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.util.parseDateTimeToMillis
import com.example.dsmevento.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditEventScreen(
    eventId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val eventViewModel: EventViewModel = viewModel()
    val selectedEvent by eventViewModel.selectedEvent

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        if (eventId != "new") {
            eventViewModel.loadEventById(eventId)
        }
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let {
            name = it.name
            location = it.location
            description = it.description
            dateText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventId == "new") "Crear evento" else "Editar evento") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del evento") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ubicación") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción") },
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Fecha y hora (yyyy-MM-dd HH:mm)") }
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val millis = parseDateTimeToMillis(dateText)
                    if (millis == null) {
                        error = "La fecha no tiene el formato correcto."
                        return@Button
                    }

                    val event = Event(
                        uid = if (eventId == "new") "" else eventId,
                        createdAt = selectedEvent?.createdAt ?: System.currentTimeMillis(),
                        date = millis,
                        description = description,
                        location = location,
                        name = name,
                        attendees = selectedEvent?.attendees ?: emptyList()
                    )

                    eventViewModel.saveEvent(event) {
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}