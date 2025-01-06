package com.example.Packages.A3_DiviseProduitsAuCamionFragment

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Apps_Head._3.Modules.Images_Handler.GlideDisplayImageById

@Composable
fun ItemMain_Fragment_3(
    itemMain: AppsHeadModel.ProduitModel,
    onClickDelete: () -> Unit = {},
    onCLickOnMain: () -> Unit = {},
    initViewModel: InitViewModel,
) {
    // Calculate if the product has a valid position
    val hasPosition = remember(itemMain.bonCommendDeCetteCota) {
        itemMain.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit?.let { pos ->
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
                onCLickOnMain()
            },
        contentAlignment = Alignment.Center
    ) {


        GlideDisplayImageById(
            initViewModel = initViewModel,
            productId = itemMain.id,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            size = 100.dp
        )
        // Delete Position Button
        if (hasPosition) {
            IconButton(
                onClick = {
                    onClickDelete()
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
            text = "ID: ${itemMain.id}",
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
            text = itemMain.nom.firstOrNull()?.toString() ?: "",
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
        itemMain.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit?.let { position ->
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
    }
}
