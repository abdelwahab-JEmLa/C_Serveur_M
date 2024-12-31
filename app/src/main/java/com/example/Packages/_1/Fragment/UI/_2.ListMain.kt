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
    // Référence à la base de données
    val dbRef = remember {
        Firebase.database.getReference("0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase")
    }

    // Séparation des produits en deux catégories
    val (produitsPositionnes, produitsNonPositionnes) = remember(items) {
        items.partition { produit ->
            produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
                position > 0
            } ?: false
        }
    }

    // Fonction de mise à jour de la position d'un produit
    val updateProductPosition = { produit: AppsHeadModel.ProduitModel, nouvellePosition: Int ->
        produit.apply {
            bonCommendDeCetteCota = bonCommendDeCetteCota ?: AppsHeadModel.ProduitModel.GrossistBonCommandes()
            bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = nouvellePosition
        }
        items.find { it.id == produit.id }?.let { item ->
            item.bonCommendDeCetteCota = produit.bonCommendDeCetteCota
            dbRef.setValue(item)
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
        if (items.isEmpty()) {
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
                items = produitsPositionnes.sortedBy {
                    it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                }
            ) { produit ->
                ItemMain(
                    itemMain = produit,
                    onCLickOnMain = {
                        val nouvellePosition = produitsPositionnes.maxOf {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } + 1
                        updateProductPosition(produit, nouvellePosition)
                    },
                    onClickDelete = {
                        updateProductPosition(produit, 0)
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
                items = produitsNonPositionnes.sortedBy { it.nom }
            ) { produit ->
                ItemMain(
                    itemMain = produit,
                    onCLickOnMain = {
                        val nouvellePosition = produitsPositionnes.maxOf {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } + 1
                        updateProductPosition(produit, nouvellePosition)
                    },
                    onClickDelete = {
                        updateProductPosition(produit, 0)
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
