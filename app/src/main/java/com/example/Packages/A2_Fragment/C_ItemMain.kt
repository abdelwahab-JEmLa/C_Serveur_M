package com.example.Packages.A2_Fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._3.Modules.Images_Handler.GlideDisplayImageById

@Composable
fun C_ItemMainFragment_2(
    itemMain: AppsHeadModel.ProduitModel,
    onCLickOnMain: (() -> Unit)? = null,
) {
    // Main container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color =
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                if (onCLickOnMain != null) {
                    onCLickOnMain()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Product Image
        GlideDisplayImageById(
            produit_Id = itemMain.id,
            sonImageBesoinActualisation = itemMain.statuesBase.sonImageBesoinActualisation,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0 ,
            onRelodeDonne = {}
        )

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
        Column() {
            // Product Name (First Letter)
            Text(
                text = itemMain.nom,
                modifier = Modifier
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
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(80.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val colorsList = itemMain.bonCommendDeCetteCota
                        ?.coloursEtGoutsCommendeList
                        ?.sortedBy { it.quantityAchete }
                        ?.filter { it.quantityAchete > 0 }
                        ?: emptyList()

                    items(colorsList.size) { index ->
                        val colorFlavor = colorsList[index]
                        val displayText = when {
                            colorFlavor.emoji.isNotEmpty() -> colorFlavor.emoji
                            else -> colorFlavor.nom.take(3)
                        }

                        Text(
                            text = "(${colorFlavor.quantityAchete})$displayText",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
