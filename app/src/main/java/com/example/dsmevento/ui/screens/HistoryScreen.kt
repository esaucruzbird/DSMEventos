package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsmevento.ui.components.EventCard
import com.example.dsmevento.util.formatMillisToDateTime
import com.example.dsmevento.viewmodel.AuthViewModel
import com.example.dsmevento.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    val currentUser by authViewModel.currentUser
    val history by eventViewModel.history

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { eventViewModel.startHistoryListener(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Historial") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (history.isEmpty()) {
                Text("No hay eventos finalizados en tu historial.")
            } else {
                history.forEach { event ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(event.name, style = MaterialTheme.typography.titleLarge)
                            Text("Fecha: ${formatMillisToDateTime(event.date)}")
                            Text("Lugar: ${event.location}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}