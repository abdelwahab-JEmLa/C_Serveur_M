// AnimationModifier.kt
package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.Maps.Companion.updateMapFromPositionedLists
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.Modules.ArticleLoggingUtil
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.ViewModel.ViewModel_Head

// B_ListMain.kt
@Composable
fun B_ListMainFragment_1(
    viewModel_Head: ViewModel_Head,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var showSearchDialog by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section des produits positionnés
        item(span = { GridItemSpan(5) }) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Produits avec position (${viewModel_Head.maps.positionedArticles.size})".also {
                        ArticleLoggingUtil.logDisplayUpdate(
                            positionedCount = viewModel_Head.maps.positionedArticles.size,
                            nonPositionedCount = viewModel_Head.maps.nonPositionedArticles.size
                        )

                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        items(
            items = viewModel_Head.maps.positionedArticles.toList(),
            key = { it.key.id }
        ) { article ->
            C_ItemMainFragment_1(
                itemMainId = article,
                modifier = Modifier
                    .padding(4.dp)
                    .animateItem(
                        fadeInSpec = tween(durationMillis = 300),
                        fadeOutSpec = tween(durationMillis = 300)
                    ),
                position = viewModel_Head.maps.positionedArticles.indexOf(article) + 1,
                onCLickOnMain = {
                    viewModel_Head.maps.positionedArticles.remove(article)
                    viewModel_Head.maps.nonPositionedArticles.add(article)
                    viewModel_Head.selectedGrossistIndex?.let { updateMapFromPositionedLists(it,viewModel_Head) }
                }
            )
        }

        // Section des produits non positionnés
        item(span = { GridItemSpan(5) }) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showSearchDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Produits sans position (${viewModel_Head.maps.nonPositionedArticles.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        items(
            items = viewModel_Head.maps.nonPositionedArticles.toList(),
            key = { it.key.id }
        ) { article ->
            C_ItemMainFragment_1(
                itemMainId = article,
                modifier = Modifier
                    .padding(4.dp)
                    .animateItem(
                        fadeInSpec = tween(durationMillis = 300),
                        fadeOutSpec = tween(durationMillis = 300)
                    ),
                onCLickOnMain = {
                    viewModel_Head.maps.positionedArticles.add(article)
                    viewModel_Head.maps.nonPositionedArticles.remove(article)
                    viewModel_Head.selectedGrossistIndex?.let { updateMapFromPositionedLists(it,viewModel_Head) }

                }
            )
        }
    }

    if (showSearchDialog) {
        SearchDialog(
            viewModel_Head = viewModel_Head,
            onDismiss = { showSearchDialog = false },
            onItemSelected = { selectedArticle ->
                viewModel_Head.maps.positionedArticles.add(selectedArticle)
                viewModel_Head.maps.nonPositionedArticles.remove(selectedArticle)
                showSearchDialog = false
                viewModel_Head.selectedGrossistIndex?.let { updateMapFromPositionedLists(it,viewModel_Head) }

            }
        )
    }
}
