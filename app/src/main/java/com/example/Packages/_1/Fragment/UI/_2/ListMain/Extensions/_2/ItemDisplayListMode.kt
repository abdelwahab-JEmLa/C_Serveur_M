package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._3.Modules.Images_Handler.Glide_Display_Image_By_Id
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.Z.Actions.OnClickMainCard
import com.example.Packages._1.Fragment.ViewModel.Models.UiState
import kotlinx.coroutines.launch

@Composable
internal fun ItemDisplayListMode(
    app_Initialize_Model: AppInitializeModel,
    uiState: UiState,
    produit: AppInitializeModel.ProduitModel,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = if (produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit != null)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { OnClickMainCard(coroutineScope, app_Initialize_Model, produit) },
        contentAlignment = Alignment.Center
    ) {
        Glide_Display_Image_By_Id(
            produit_Id = produit.id,
            produit_Image_Need_Update = produit.it_Image_besoin_To_Be_Updated,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )

        IconButton(
            onClick = {
                coroutineScope.launch {
                    produit.bonCommendDeCetteCota?.let { supplier ->
                        supplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                    }
                    app_Initialize_Model.update_Produits_FireBase()
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove position",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }

        Text(
            text = "ID: ${produit.id}",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 8.sp
        )

        Text(
            text = produit.nom.firstOrNull()?.toString() ?: "",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(4.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
            if (position != 0) {
                Text(
                    text = position.toString(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

