package com.example.Main.StartFragment

import P1_StartupScreen.Main.FloatingActionButtonGroup.FloatingActionButtonGroup
import androidx.compose.runtime.Composable
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun Screen(
    state: UiStat,
    actions: FragmentsActions
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp)
        ) {

            // Animated FAB group
            FloatingActionButtonGroup(
                modifier = Modifier.padding(top = 8.dp) ,
                actions=actions
            )
        }
    }
}
