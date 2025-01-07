package com.example.Packages.A3_DiviseProduitsAuCamionFragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel

@Composable
fun ListMain_Fragment_3(
    produitsMainDataBase: SnapshotStateList<AppsHeadModel.ProduitModel>,
    initViewModel: InitViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val visibleSortedItems =
        produitsMainDataBase
            .filter { it.isVisible }
            .sortedWith(
                compareBy(
                    { it.bonCommendDeCetteCota?.grossistInformations?.positionInGrossistsList },
                    { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit }
                )
            )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (visibleSortedItems.isNotEmpty()) {
            items(visibleSortedItems ) { item ->
                ItemMain_Fragment_3(
                    itemMain = item,
                    initViewModel=initViewModel,
                )
            }
        }
    }
}
