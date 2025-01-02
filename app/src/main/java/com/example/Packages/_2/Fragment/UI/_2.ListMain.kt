package com.example.Packages._2.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ListMain(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModelScope: CoroutineScope
) {
    var itemsFiltre by remember(visibleItems) { mutableStateOf(visibleItems) }

    val updateProductPosition: (AppsHeadModel.ProduitModel, Int) -> Unit = remember {
        { produit, nouvellePosition ->
            if (produit.bonCommendDeCetteCota == null) {
                produit.bonCommendDeCetteCota = AppsHeadModel.ProduitModel.GrossistBonCommandes()
            }

            produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                nouvellePosition

            // Mark product for update
            produit.besoin_To_Be_Updated = true

            // Update local state immediately
            itemsFiltre = itemsFiltre.map {
                if (it.id == produit.id) produit else it
            }.toMutableStateList()

            // Update Firebase
            viewModelScope.launch {
                itemsFiltre.updateProduitsFireBase()
            }
        }
    }

    // Separate products into positioned and unpositioned using derived state
    val produitsPositionnes by remember(itemsFiltre) {
        derivedStateOf {
            itemsFiltre.filter { produit ->
                produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
                    position > 0
                } ?: false
            }.sortedBy {
                it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            }
        }
    }

    val produitsNonPositionnes by remember(itemsFiltre) {
        derivedStateOf {
            itemsFiltre.filter { produit ->
                produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
                    position <= 0
                } ?: true
            }.sortedBy { it.nom }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xE3C85858).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Handle empty list
        if (itemsFiltre.isEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Aucun produit disponible",
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return@LazyVerticalGrid
        }

        // Positioned products section
        if (produitsPositionnes.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                SectionHeader(
                    text = "Produits avec position (${produitsPositionnes.size})"
                )
            }

            items(
                items = produitsPositionnes,
                key = { it.id }
            ) { produit ->
                ItemMain(
                    itemMain = produit,
                    onCLickOnMain ={
                        val maxPosition = produitsPositionnes.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        updateProductPosition(produit, maxPosition + 1)
                    },
                    onClickDelete = {
                        updateProductPosition(produit, 0)
                    }
                )
            }
        }

        // Unpositioned products section
        if (produitsNonPositionnes.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                SectionHeader(
                    text = "Produits sans position (${produitsNonPositionnes.size})"
                )
            }

            items(
                items = produitsNonPositionnes,
                key = { it.id }
            ) { produit ->
                ItemMain(
                    itemMain = produit,
                    onCLickOnMain ={
                        val maxPosition = produitsPositionnes.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        updateProductPosition(produit, maxPosition + 1)
                    },
                    onClickDelete = {
                        updateProductPosition(produit, 0)
                    }
                )
            }
        }
    }
}


