package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel.ProduitModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ArticleInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ColourEtGoutInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ViewModel_Head

@Composable
fun B_ListMainFragment_1(
    viewModel_Head: ViewModel_Head,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var showSearchDialog by remember { mutableStateOf(false) }

    val positionedArticles = viewModel_Head.maps.positionedArticles
    val nonPositionedArticles = viewModel_Head.maps.nonPositionedArticles

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        productSection(
            title = "Produits avec position (${positionedArticles.size})",
            products = positionedArticles,
            viewModel = viewModel_Head,
            showSearch = false,
            onProductClick = { product ->
            }
        )

        productSection(
            title = "Produits sans position (${nonPositionedArticles.size})",
            products =  nonPositionedArticles,
            viewModel = viewModel_Head,
            showSearch = true,
            onSearchClick = { showSearchDialog = true },
            onProductClick = { product ->
            }
        )
    }
}

private fun LazyGridScope.productSection(
    title: String,
    products: Map<ArticleInfosModel, Map<ColourEtGoutInfosModel, Double>>,
    viewModel: ViewModel_Head,
    showSearch: Boolean = false,
    onSearchClick: () -> Unit = {},
    onProductClick: (ProduitModel) -> Unit
) {
    if (products.isNotEmpty()) {
        item(span = { GridItemSpan(5) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showSearch) {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        items(items = products) { product ->
            C_ItemMainFragment_1( //->
                //TODO(FIXME):Fix erreur @Composable invocations can only happen from the context of a @Composable function
                viewModel_Head = viewModel,
                itemMainId = product,
                onCLickOnMain = { onProductClick(product) },
                position = if (!showSearch) products.indexOf(product) + 1 else null,
                modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
            )
        }
    }
}


