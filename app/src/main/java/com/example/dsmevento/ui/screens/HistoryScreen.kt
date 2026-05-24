package com.example.dsmevento.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.dsmevento.ui.theme.EventPastGray
import com.example.dsmevento.viewmodel.AuthViewModel
import com.example.dsmevento.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    val currentUser = authViewModel.currentUser
    val history = eventViewModel.history

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            eventViewModel.startHistoryListener(uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Historial") })
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
            if (history.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay eventos finalizados en tu historial.")
                    }
                }
            } else {
                items(history, key = { it.uid }) { event ->
                    EventCard(
                        event = event,
                        isPastEvent = true,
                        canEditDelete = false,
                        containerColor = EventPastGray,
                        onClick = { }
                    )
                }
            }

            item {
                TextButton(onClick = onBack) {
                    Text("Volver")
                }
            }
        }
    }
}