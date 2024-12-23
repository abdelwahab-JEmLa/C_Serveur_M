package com.example.Packages._4.Fragment.UI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.App_Produits_Main._3.Modules.Images_Handler.Glide_Display_Image_By_Id
import com.example.Packages._4.Fragment._1.Main.Model.Ui_State_4_Fragment
import com.example.App_Produits_Main._1.Model.AppInitializeModel
   /*
private const val TAG = "Main_Item"

@Composable
internal fun Main_Item(
    uiState: Ui_State_4_Fragment,
    produit: AppInitializeModel.ProduitModel,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val heightCard = when {
        uiState.currentMode == Ui_State_4_Fragment.Affichage_Et_Click_Modes.MODE_Affiche_Produits ->
            if (isExpanded) 300.dp else 100.dp
        else -> 100.dp
    }

    // Debug log for demande achat list
    Log.d(TAG, "Product ${produit.nom} has ${produit.acheteurs_pour_Cette_Cota.size} demandes")
    produit.acheteurs_pour_Cette_Cota.forEach { demande ->
        Log.d(TAG, "Demande time: ${demande.time_String}, colors size: ${demande.colours_Et_Gouts_Acheter_Depuit_Client.size}")
    }

    val last_Demend_Achat = produit.acheteurs_pour_Cette_Cota
        .maxByOrNull { it.time_String }
        ?.also { lastDemand ->
            Log.d(TAG, "Last demand for ${produit.nom} at time: ${lastDemand.time_String}")
        }

    val totalQuantity = last_Demend_Achat?.let { demand ->
        demand.colours_Et_Gouts_Acheter_Depuit_Client.sumOf { it.quantity_Achete }
            .also { sum ->
                Log.d(TAG, "Total quantity for ${produit.nom}: $sum")
                Log.d(TAG, "Colors breakdown: ${
                    demand.colours_Et_Gouts_Acheter_Depuit_Client.joinToString {
                        "${it.nom}:${it.quantity_Achete}"
                    }
                }")
            }
    } ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (uiState.currentMode == Ui_State_4_Fragment.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs)
                    Modifier.wrapContentHeight()
                else
                    Modifier.height(heightCard)
            )
    ) {
        // Background image
        Glide_Display_Image_By_Id(
            produit_Id = produit.id,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )

        // Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = Color.Black.copy(alpha = 0.4f))
        )

        // Non trouvé highlight
        if (produit.non_Trouve) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0xFFFFD700).copy(alpha = 0.7f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = produit.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: $totalQuantity",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(if (isExpanded) 280.dp else 80.dp)
                    .clickable { isExpanded = !isExpanded }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val colorsList = last_Demend_Achat?.let { demand ->
                        demand.colours_Et_Gouts_Acheter_Depuit_Client
                            .filter { it.quantity_Achete > 0 }
                            .sortedByDescending { it.quantity_Achete }  // Changed to sortedByDescending
                            .also { filtered ->
                                Log.d(TAG, "Displaying ${filtered.size} colors for ${produit.nom}")
                            }
                    } ?: emptyList()

                    items(colorsList.size) { index ->
                        val colorFlavor = colorsList[index]
                        val displayText = when {
                            colorFlavor.imogi.isNotEmpty() -> colorFlavor.imogi
                            else -> colorFlavor.nom.take(3)
                        }

                        Text(
                            text = "(${colorFlavor.quantity_Achete})$displayText",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
                   */
