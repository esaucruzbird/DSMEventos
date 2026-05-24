package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.util.formatMillisToInputDateTime
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
    val selectedEvent = eventViewModel.selectedEvent
    val loading = eventViewModel.loading
    val remoteError = eventViewModel.errorMessage

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        localError = null
        if (eventId == "new") {
            eventViewModel.clearSelectedEvent()
            name = ""
            location = ""
            description = ""
            dateText = ""
        } else {
            eventViewModel.loadEventById(eventId)
        }
    }

    LaunchedEffect(selectedEvent?.uid) {
        selectedEvent?.let { event ->
            name = event.name
            location = event.location
            description = event.description
            dateText = formatMillisToInputDateTime(event.date)
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del evento") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ubicación") },
                singleLine = true
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
                label = { Text("Fecha y hora (yyyy-MM-dd HH:mm)") },
                singleLine = true
            )

            if (localError != null || remoteError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = localError ?: remoteError.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val millisFromText = parseDateTimeToMillis(dateText)
                    val finalMillis = millisFromText ?: selectedEvent?.date

                    if (finalMillis == null) {
                        localError = "La fecha no tiene el formato correcto."
                        return@Button
                    }

                    val event = Event(
                        uid = if (eventId == "new") "" else eventId,
                        createdAt = selectedEvent?.createdAt ?: System.currentTimeMillis(),
                        date = finalMillis,
                        description = description,
                        location = location,
                        name = name,
                        attendees = selectedEvent?.attendees ?: emptyList(),
                        finished = selectedEvent?.finished ?: false
                    )

                    eventViewModel.saveEvent(event) {
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
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