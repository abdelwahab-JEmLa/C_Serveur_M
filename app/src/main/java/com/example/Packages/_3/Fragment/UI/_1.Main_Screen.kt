package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.V.FABs.Modules.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ui_Mutable_State

// Fragment3_Main_Screen.kt updates
@Composable
internal fun Fragment3_Main_Screen(
    modifier: Modifier = Modifier,
    p3_ViewModel: P3_ViewModel = viewModel()
) {
   // val allGroupedProducts = groupProductsBySupplier(p3_ViewModel)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                Produits_Main_List(
                    ui_Mutable_State = p3_ViewModel.uiState,
                    viewModel = p3_ViewModel,
                    contentPadding = paddingValues
                )
            }

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_Mutable_State = p3_ViewModel.uiState,
            )
        }
    }
}


// New helper function to filter products by total quantity
private fun filterProductsByTotalQuantity(
    grouped_Produits_Par_Id_Grossist: Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>>
): Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>> {
    return grouped_Produits_Par_Id_Grossist.mapValues { (_, products) ->
        products.filter { produit ->
            val totalQuantity = produit.colours_Et_Gouts_Commende?.sumOf { it.quantity_Achete } ?: 0
            totalQuantity > 0
        }
    }.filter { (_, products) -> products.isNotEmpty() }
}

//// Existing helper functions remain unchanged
//private fun groupProductsBySupplier(
//    p3_ViewModel: P3_ViewModel
//): Map<Long, List<UiState.Produit_DataBase>> {
//    return p3_ViewModel.uiState.produit_DataBase
//        .groupBy { it.grossist_Choisi_Pour_Acheter_CeProduit.vid ?: 0L }
//        .toSortedMap()
//        .mapValues { (_, produitsGroup) ->
//            produitsGroup.sortedBy {
//                it.grossist_Choisi_Pour_Acheter_CeProduit?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
//            }
//        }
//}

private fun filterProductGroups(
    allGroupedProducts: Map<Long, List<UiState.Produit_DataBase>>,
    p3_ViewModel: P3_ViewModel
): Map<Long, List<UiState.Produit_DataBase>> {
    return if (p3_ViewModel.uiState.mode_Trie_Produit_Non_Trouve) {
        allGroupedProducts.mapValues { (_, products) ->
            products.filter { it.non_Trouve }
        }.filter { (_, products) ->
            products.isNotEmpty()
        }
    } else {
        allGroupedProducts
    }
}

//private fun filterProductsBySupplier(
//    grouped_Produits_Par_Id_Grossist: Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>>,
//    p3_ViewModel: P3_ViewModel
//): Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>> {
//    return if (p3_ViewModel.uiState.selectedSupplierId != 0L) {
//        grouped_Produits_Par_Id_Grossist.filter { it.key == p3_ViewModel.uiState.selectedSupplierId }
//    } else {
//        grouped_Produits_Par_Id_Grossist
//    }
//}

@Preview
@Composable
private fun MainScreenPreview() {
    Fragment3_Main_Screen(modifier = Modifier.fillMaxSize())
}
