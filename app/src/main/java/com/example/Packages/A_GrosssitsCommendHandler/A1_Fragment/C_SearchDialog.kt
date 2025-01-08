package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ViewModel_Head
import kotlinx.coroutines.delay

@Composable
fun SearchDialog(
    viewModel_Head: ViewModel_Head,
    unpositionedItems: SnapshotStateList<Model_CodingWithMaps.Mapping.Grossist.Produits>,
    onDismiss: () -> Unit,
    onItemSelected: (Model_CodingWithMaps.Mapping.Grossist.Produits) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
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
                    onValueChange = { searchText = it },
                    label = { Text("Nom du produit") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                val filteredItems = unpositionedItems.filter {
                    it.produitId.toString().contains(searchText, ignoreCase = true)
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
                        ) { product ->
                            /*C_ItemMainFragment_1(
                                viewModel_Head = viewModel_Head,
                                itemMainId = product,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null
                                ),
                                onCLickOnMain = { onItemSelected(product) },
                            )      */
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
