package com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0.Fragment_2

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.GlideDisplayImageById2
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun MainList_F2(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelInitApp: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = visibleProducts.sortedBy { product ->
                // Handle null cases by providing a default max value
                product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                    ?: Int.MAX_VALUE
            },
            // Ensure unique key by combining id with position if available
            key = { product ->
                "${product.id}_${product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit}"
            }
        ) { product ->
            MainItem_F2(
                mainItem = product,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateItem(fadeInSpec = null, fadeOutSpec = null),
                onCLickOnMain = {

                }
            )
        }
    }
}



@Composable
fun ExpandedMainItem_F4(
    mainItem: _ModelAppsFather.ProduitModel,  // Moved to be first optional parameter
    modifier: Modifier = Modifier,
    onCLickOnMain: () -> Unit = {},
) {
    Box(
        modifier = modifier  // Using the passed modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                onCLickOnMain()
            },
        contentAlignment = Alignment.Center
    ) {

        GlideDisplayImageById2(
            mainItem.id,
            imageGlidReloadTigger = 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            size = 500.dp
        )

        // Product ID
        Text(
            text = "ID: ${mainItem.id}",
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

        Text(
            text = mainItem.nom,
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

    }
}
