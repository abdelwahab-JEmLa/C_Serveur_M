package com.example.Packages._3.Fragment.Z.Archives.Components
//
//import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
//import com.example.Packages._3.Fragment.Models.UiState
//import com.example.Packages._3.Fragment.ViewModel._2.Init.generate_Random_Supplier2
//
//private suspend fun P3_ViewModel.phase3_update_Finale() {
//    _uiState.referencesFireBaseGroup.find { it.id == 1L }?.let { group ->
//        group.produit_Update_Ref.clear()
//        group.items_Need_To_Be_Updated_From_it =
//            if (group.items_Need_To_Be_Updated_From_it == UiState.ReferencesFireBaseGroup.Items_Need_To_Be_Updated_From_it.ALL)
//                UiState.ReferencesFireBaseGroup.Items_Need_To_Be_Updated_From_it.TOTAL_QUANTITY_GROSSIST_COMMAND_ABOVE_ZERO
//            else
//                UiState.ReferencesFireBaseGroup.Items_Need_To_Be_Updated_From_it.ALL
//        group.setSelfInFirebaseDataBase()
//    }
//    // Update or add products
//    productsData.forEach { produitUpdate ->
//        val existingIndex = _uiState.produit_DataBase.indexOfFirst { it.id == produitUpdate.id }
//        if (existingIndex != -1) {
//            _uiState.produit_DataBase[existingIndex] = produitUpdate
//            _uiState.produit_DataBase[existingIndex].grossist_Choisi_Pour_Acheter_CeProduit =
//                generate_Random_Supplier2()
//            _uiState.produit_DataBase[existingIndex].updateSelfInFirebaseDataBase()
//        } else {
//            produitUpdate.updateSelfInFirebaseDataBase()
//        }
//    }
//}
//}
