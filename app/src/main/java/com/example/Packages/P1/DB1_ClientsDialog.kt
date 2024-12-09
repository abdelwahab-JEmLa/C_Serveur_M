package com.example.Packages.P1

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.Models.ClientsDataBase

@Composable
internal fun ClientsDialog(
    clients: List<ClientsDataBase>,
    onDismiss: () -> Unit,
    actions: FragmentsActions
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = MaterialTheme.shapes.large
        ) {
            Column {
                Text(
                    text = "Select a Client",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn {
                    items(clients) { client ->
                        ClientButtonDialog(
                            actions=actions,
                            client = client,
                            onClientSelect = {
                                actions.onClickToUpdateitsReadyForEdite(
                                    client.idClientsSu
                                )
                            },

                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
internal fun ClientButtonDialog(
    client: ClientsDataBase,
    onClientSelect: () -> Unit,
    actions: FragmentsActions,
) {
    var showNameEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "Client Readiness")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Readiness Opacity"
    )

    if (showNameEditDialog) {
        AlertDialog(
            onDismissRequest = { showNameEditDialog = false },
            title = { Text("Edit Client Name") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text(client.nomClientsSu) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        actions.updateClientName(client.idClientsSu, editedName)
                        showNameEditDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNameEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Button(
        onClick = onClientSelect,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(android.graphics.Color.parseColor(client.couleurSu))
                .copy(alpha = if (client.itsReadyForEdite) alpha else 1f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ID: ${client.idClientsSu}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row {
                    Text(
                        text = client.nomClientsSu,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = client.nameAggregation,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (!client.itsReadyForEdite) {
                    Text(
                        text = "Not Ready for Edit",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            IconButton(
                onClick = { showNameEditDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Client Name"
                )
            }
        }
    }
}
