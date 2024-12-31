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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel

@Composable
internal fun ListMain(
    currentItems: List<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onClick: (AppsHeadModel.ProduitModel, Int) -> Unit,
) {
    // Séparation des produits en deux catégories using derived state
    val produitsPositionnes by remember(currentItems) {
        derivedStateOf {
            currentItems.filter { produit ->
                produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
                    position > 0
                } ?: false
            }.sortedBy {
                it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            }
        }
    }

    val produitsNonPositionnes by remember(currentItems) {
        derivedStateOf {
            currentItems.filter { produit ->
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
        // Gestion de la liste vide
        if (currentItems.isEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Aucun produit disponible",
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return@LazyVerticalGrid
        }

        // Section des produits positionnés
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
                    onCLickOnMain = {
                        val maxPosition = produitsPositionnes.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        onClick(produit, maxPosition + 1)
                    },
                    onClickDelete = {
                        onClick(produit, 0)
                    }
                )
            }
        }

        // Section des produits non positionnés
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
                    onCLickOnMain = {
                        val maxPosition = produitsPositionnes.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        onClick(produit, maxPosition + 1)
                    },
                    onClickDelete = {
                        onClick(produit, 0)
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
}
