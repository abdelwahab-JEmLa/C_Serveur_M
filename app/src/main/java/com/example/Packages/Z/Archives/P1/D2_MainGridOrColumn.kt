package com.example.Packages.Z.Archives.P1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import i_SupplierArticlesRecivedManager.Item

@Composable
internal fun MainGridOrColumn(
    state: UiStat,
    actions: FragmentsActions
) {
    val sortedProducts = state.produitsDataBase
        .groupBy { it.idCategorieNewMetode }
        // Sort the grouped products based on the category classification order
        .toSortedMap { a, b ->
            // Find the corresponding category classification for each category ID
            val categoryA = state.productsCategoriesDataBase
                .find { it.idCategorieInCategoriesTabele == a.toLong() }
                ?.idClassementCategorieInCategoriesTabele ?: Int.MAX_VALUE

            val categoryB = state.productsCategoriesDataBase
                .find { it.idCategorieInCategoriesTabele == b.toLong() }
                ?.idClassementCategorieInCategoriesTabele ?: Int.MAX_VALUE

            // Compare categories based on their classification order
            categoryA.compareTo(categoryB)
        }
        .flatMap { (_, products) ->
            // Within each category, sort products by their internal classification
            products.sortedBy { it.articleItIdClassementInItCategorieInHVM }
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // Add red shadow effect using background color
            .background(Color.Red.copy(alpha = 0.1F)),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        items(sortedProducts) { product ->
            // Add additional padding to create spacing between items
            Item(
                modifier = Modifier.padding(vertical = 8.dp),
                actions = actions,
                product = product,
                clientsDataBase = state.clientsDataBase,
                diviseurDeDisplayProductForEachClient = state.diviseurDeDisplayProductForEachClient
            )
        }
    }
}
