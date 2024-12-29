package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._1.DisplayGridMode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel
import com.example.Apps_Head._3.Modules.Images_Handler.Glide_Display_Image_By_Id
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions.OnClickMainCard

@Composable
internal fun ItemMain_Grid(
    appInitializeViewModel: AppInitialize_ViewModel,
    appInitializeModel: AppInitializeModel,
    produit: AppInitializeModel.ProduitModel,
) {
    // Calculate if the product has a valid position
    val hasPosition = remember(produit.bonCommendDeCetteCota) {
        produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { pos ->
            pos > 0
        } ?: false
    }

    // Main container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = if (hasPosition)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                appInitializeViewModel.OnClickMainCard(produit)
            },
        contentAlignment = Alignment.Center
    ) {
        // Product Image
        Glide_Display_Image_By_Id(
            produit_Id = produit.id,
            produit_Image_Need_Update = produit.it_Image_besoin_To_Be_Updated,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )

        // Delete Position Button
        if (hasPosition) {
            IconButton(
                onClick = {
                    appInitializeViewModel.updateProductPosition(produit.id, 0)
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
        }

        // Product ID
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
            fontSize = 8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Product Name (First Letter)
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
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Position Number (if exists)
        produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
            if (position > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                ) {
                    Text(
                        text = position.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Optional: Show loading indicator when updating
        if (produit.it_Image_besoin_To_Be_Updated) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                // You can add a CircularProgressIndicator here if needed
            }
        }
    }
}
