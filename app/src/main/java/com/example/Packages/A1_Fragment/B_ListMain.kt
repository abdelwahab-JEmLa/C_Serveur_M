package com.example.Packages.A1_Fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.update_produitsViewModelEtFireBases
import com.example.Apps_Head._2.ViewModel.InitViewModel
import kotlinx.coroutines.delay

@Composable
fun B_ListMainFragment_1(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    initViewModel: InitViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val (positioned, unpositioned) =
        visibleItems
            .partition {
                it.bonCommendDeCetteCota
                    ?.cPositionCheyCeGrossit == true
            }

    if (showSearchDialog) {
        SearchDialog(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            unpositionedItems = unpositioned,
            onDismiss = {
                showSearchDialog = false
                searchText = ""
            },
            onItemSelected = { selectedProduct ->
                val newPositione =
                    (positioned.maxOfOrNull {
                        it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                            ?: 0
                    } ?: 0) + 1

                visibleItems[visibleItems.indexOfFirst { it.id == selectedProduct.id }] =
                    selectedProduct.apply {
                        if (selectedProduct.itsTempProduit) {
                            statuesBase.prePourCameraCapture = true
                        }
                        bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit = newPositione
                        bonCommendDeCetteCota?.cPositionCheyCeGrossit = true
                    }

                visibleItems.toMutableStateList()
                    .update_produitsViewModelEtFireBases(initViewModel)

                showSearchDialog = false
                searchText = ""
            },
            initViewModel = initViewModel
        )
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
        if (positioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    "Produits avec position (${positioned.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(
                items = positioned.sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    initViewModel = initViewModel,
                    itemMain = product,
                    onCLickOnMain = {
                        visibleItems[visibleItems.indexOfFirst { it.id == product.id }] =
                            product.apply {
                                bonCommendDeCetteCota?.cPositionCheyCeGrossit = false
                            }

                        visibleItems.toMutableStateList()
                            .update_produitsViewModelEtFireBases(initViewModel)
                    }
                )
            }
        }

        if (unpositioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextButton(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(
                            "Produits sans position (${unpositioned.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            items(
                items = unpositioned.sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    initViewModel = initViewModel,
                    itemMain = product,
                    onCLickOnMain = {
                        val newPositione =
                            (positioned.maxOfOrNull {
                                it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                                    ?: 0
                            } ?: 0) + 1

                        visibleItems[visibleItems.indexOfFirst { it.id == product.id }] =
                            product.apply {
                                if (product.itsTempProduit) {
                                    statuesBase.prePourCameraCapture = true
                                }
                                bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit = newPositione
                                bonCommendDeCetteCota?.cPositionCheyCeGrossit = true
                            }

                        visibleItems.toMutableStateList()
                            .update_produitsViewModelEtFireBases(initViewModel)
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchDialog(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    unpositionedItems: List<AppsHeadModel.ProduitModel>,
    onDismiss: () -> Unit,
    onItemSelected: (AppsHeadModel.ProduitModel) -> Unit,
    initViewModel: InitViewModel
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(100) // Small delay to ensure the TextField is composed
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Handle potential focus request failure gracefully
        }
    }

    Dialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Rechercher un produit",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    label = { Text("Nom du produit") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                val filteredItems = unpositionedItems.filter {
                    it.nom.contains(searchText, ignoreCase = true)
                }

                if (searchText.length >= 2) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredItems,
                            key = { it.id }
                        ) { product ->
                            C_ItemMainFragment_1(
                                initViewModel = initViewModel,
                                itemMain = product,
                                onCLickOnMain = { onItemSelected(product) }
                            )
                        }
                    }
                }

                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}
