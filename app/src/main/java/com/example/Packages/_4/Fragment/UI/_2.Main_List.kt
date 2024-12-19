package com.example.Packages._4.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.util.Log
import com.example.Packages._4.Fragment._1.Main.Model.Ui_State_4_Fragment
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model

@Composable
fun Main_List(
    modifier: Modifier = Modifier,
    produit_Main_DataBase: SnapshotStateList<App_Initialize_Model.Produit_Main_DataBase>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
    uiState: Ui_State_4_Fragment
) {
    val filtered_Items = produit_Main_DataBase.filter { produit ->
        val lastDemand = produit.demmende_Achate_De_Cette_Produit
            .maxByOrNull { it.time_String }?.colours_Et_Gouts_Acheter_Depuit_Client?.isEmpty()
        lastDemand != null
    }

    val sorted_Visible_Items = filtered_Items.sortedWith(
        compareBy<App_Initialize_Model.Produit_Main_DataBase> { produit ->
            val position = produit.grossist_Choisi_Pour_Acheter_CeProduit
                .maxByOrNull { it.date }
                ?.position_Grossist_Don_Parent_Grossists_List
            position ?: Int.MAX_VALUE
        }.thenBy { produit ->
            val position = produit.grossist_Choisi_Pour_Acheter_CeProduit
                .maxByOrNull { it.date }
                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            position ?: Int.MAX_VALUE
        }
    )

    sorted_Visible_Items.forEach { produit ->
        Log.d("MainList", "✨ Visible product: ${produit.nom}")
    }

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
        items(
            count = sorted_Visible_Items.size
        ) { index ->
            Main_Item(
                uiState = uiState,
                produit = sorted_Visible_Items[index]
            )
        }
    }
}
