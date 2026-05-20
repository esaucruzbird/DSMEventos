package com.example.dsmevento.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsmevento.data.model.Event
import com.example.dsmevento.util.formatMillisToDateTime

@Composable
fun EventCard(
    event: Event,
    isPastEvent: Boolean,
    canEditDelete: Boolean,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Fecha: ${formatMillisToDateTime(event.date)}")
            Text(text = "Lugar: ${event.location}")
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = event.description)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isPastEvent) "Evento finalizado" else "Evento próximo",
                style = MaterialTheme.typography.labelMedium
            )

            if (canEditDelete && !isPastEvent) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onEdit?.invoke() }) {
                        Text("Editar")
                    }
                    OutlinedButton(onClick = { onDelete?.invoke() }) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}