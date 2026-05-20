package com.example.dsmevento.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsmevento.data.model.Review
import com.example.dsmevento.util.formatMillisToDateTime

@Composable
fun ReviewItem(review: Review) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = review.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Calificación: ${review.rating}/5")
            Text(text = "Fecha: ${formatMillisToDateTime(review.createdAt)}")
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = review.comment)
        }
    }
}