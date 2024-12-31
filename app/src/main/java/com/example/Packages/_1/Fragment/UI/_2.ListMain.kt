package com.example.Packages._1.Fragment.UI

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
internal fun ListMain(
    items: List<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val db = Firebase.database.getReference("0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase")

    // Trie les produits en deux groupes
    val (withPos, withoutPos) = remember(items) {
        items.partition {
            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { pos -> pos > 0 } ?: false
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Affiche message si liste vide
        if (items.isEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "No products available",
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return@LazyVerticalGrid
        }

        // Produits avec position
        if (withPos.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Products with Position (${withPos.size})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(withPos.sortedBy { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }) { item ->
                ItemMain(
                    itemMain = item,
                    onCLickOnMain = {
                        val maxPos = withPos.maxOf {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        }
                        item.bonCommendDeCetteCota = item.bonCommendDeCetteCota ?: AppsHeadModel.ProduitModel.GrossistBonCommandes()
                        item.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = maxPos + 1
                        db.setValue(item)
                    },
                    onClickDelete = {
                        item.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                        db.setValue(item)
                    }
                )
            }
        }

        // Produits sans position
        if (withoutPos.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Products without Position (${withoutPos.size})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(withoutPos.sortedBy { it.nom }) { item ->
                ItemMain(
                    itemMain = item,
                    onCLickOnMain = {
                        val maxPos = withPos.maxOf {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        }
                        item.bonCommendDeCetteCota = item.bonCommendDeCetteCota ?: AppsHeadModel.ProduitModel.GrossistBonCommandes()
                        item.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = maxPos + 1
                        db.setValue(item)
                    },
                    onClickDelete = {
                        item.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                        db.setValue(item)
                    }
                )
            }
        }
    }
}
