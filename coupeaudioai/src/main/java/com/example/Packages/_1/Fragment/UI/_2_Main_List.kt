// _2_Main_List.kt
package com.example.Packages._1.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.Packages._1.Fragment.UI._3.ItemMainDifferentsAffichageModes.ItemMain
import com.example.Packages._1.Fragment.UI._4.Components.Sticky_Header
import com.example.Packages._1.Fragment.ViewModel.Models.UiState

@Composable
internal fun List_Main(
    modifier: Modifier = Modifier,
    app_Initialize_Model: AppInitializeModel,
    ui_State: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
) {
    val visibleItems = remember(app_Initialize_Model.audioInfos) {
        app_Initialize_Model.audioInfos.flatMap { audioDatas ->
            // Map each AudioMarks_Model to include its parent Audio_DatasModel for the header
            audioDatas.audioMarks.map { audioMark ->
                audioMark to audioDatas
            }
        }
    }

    when (ui_State.currentMode) {
        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs,
        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xE3C85858).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (visibleItems.isNotEmpty()) {
                    // Group items by their parent Audio_DatasModel
                    val groupedItems = visibleItems.groupBy { it.second }

                    groupedItems.forEach { (audioDatas, marks) ->
                        item(key = "header-${audioDatas.vid}") {
                            Sticky_Header(
                                app_Initialize_Model = audioDatas
                            )
                        }

                        items(
                            items = marks,
                            key = { it.first.id }
                        ) { (audioMark, _) ->
                            ItemMain(
                                item = audioMark
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "No items available for selected filter",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {}
    }
}
