package com.example.Packages.Z.Archives.P1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.serveurecherielhanaaebeljemla.Models.UiStat

@Composable
internal fun Screen(
    state: UiStat,
    actions: FragmentsActions
) {
    var showClientsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        MainGridOrColumn(state, actions)

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp)
        ) {

            // Animated FAB group
            FloatingActionButtonGroup(
                modifier = Modifier.padding(top = 8.dp) ,
                onWindowsClientsToEdit = {
                    showClientsDialog = !showClientsDialog
                } ,
                actions
            )
        }
    }
    if (showClientsDialog) {
        ClientsDialog(
            actions=actions,
            clients = state.clientsDataBase,
            onDismiss = { showClientsDialog = false },
            )
    }
}

