package com.example.Packages.A_GrosssitsCommendHandler.A2_Fragment

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
fun B_ListMainFragment_2(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    initViewModel: InitViewModel,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (visibleItems.isNotEmpty()) {
            items(visibleItems, key = {
                    "${ it.bonCommendDeCetteCota
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit}->${it.id}"}) { item ->
                C_ItemMainFragment_2(
                    initViewModel=initViewModel,
                    itemMain = item,
                )
            }
        }
    }
}
