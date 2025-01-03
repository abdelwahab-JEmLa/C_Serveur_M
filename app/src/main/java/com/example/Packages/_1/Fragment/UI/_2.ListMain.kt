package com.example.Packages._1.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import kotlin.random.Random

@Composable
fun ListMain(
    mapVisibleItemeSetEtleurClient: Map<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, Set<AppsHeadModel.ProduitModel>>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier.fillMaxWidth().background(
            color = Color(0xE3C85858).copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mapVisibleItemeSetEtleurClient.forEach { (client, itemSet) ->
            if (itemSet.isEmpty()) {
                item(span = { GridItemSpan(5) }) {
                    Text(
                        text = "Aucun produit disponible pour ${client.nom}",
                        modifier = Modifier.padding(32.dp)
                    )
                }
                return@forEach
            }

            // En-tête client
            item(span = { GridItemSpan(5) }) {
                Surface(
                    color = Color(android.graphics.Color.parseColor(client.couleur)).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = client.nom,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(android.graphics.Color.parseColor(client.couleur))
                        )
                        Text(text = "Nombre de produits: ${itemSet.size}")
                    }
                }
            }

            item(span = { GridItemSpan(5) }) {
                val positionedItems = remember(itemSet) {
                    mutableStateOf(itemSet.filter { Random.nextBoolean() }.toSet())
                }

                Column {
                    // Produits avec position
                    val positioned = itemSet.filter { it in positionedItems.value }
                    if (positioned.isNotEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Produits avec position (${positioned.size})",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        positioned.forEach { produit ->
                            ItemMain(
                                itemMain = produit,
                                onCLickOnMain = { positionedItems.value -= produit },
                                onClickDelete = { positionedItems.value -= produit }
                            )
                        }
                    }

                    // Produits sans position
                    val unpositioned = itemSet.filter { it !in positionedItems.value }
                    if (unpositioned.isNotEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Produits sans position (${unpositioned.size})",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        unpositioned.forEach { produit ->
                            ItemMain(
                                itemMain = produit,
                                onCLickOnMain = { positionedItems.value += produit },
                                onClickDelete = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
