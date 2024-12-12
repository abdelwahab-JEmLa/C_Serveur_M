package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components.Archive
//
//import android.util.Log
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.runtime.toMutableStateList
//import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.TAG_Snap
//import com.example.Packages._3.Fragment.Models.UiState
//import com.google.firebase.Firebase
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.database
//import kotlinx.coroutines.async
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.tasks.await
//
//internal suspend fun processes_Organiseur(
//    referenceFireBas: UiState.ReferencesFireBaseGroup?,
//    onProgressUpdate: (Float) -> Unit,
//): SnapshotStateList<UiState.Produit_DataBase> = coroutineScope {
//    try {
//        onProgressUpdate(0.1f)
//
//        val (productsSnapshot, soldArticlesSnapshot) = coroutineScope {
//            val productsDeferred = async {
//                Firebase.database.getReference("e_DBJetPackExport")
//                    .get()
//                    .await()
//            }
//            val soldArticlesDeferred = async {
//                Firebase.database.getReference("ArticlesAcheteModele")
//                    .get()
//                    .await()
//            }
//            Pair(productsDeferred.await(), soldArticlesDeferred.await())
//        }
//
//        Log.d(TAG_Snap, "Retrieved data - Products: ${productsSnapshot.childrenCount}, Sold Articles: ${soldArticlesSnapshot.childrenCount}")
//        onProgressUpdate(0.3f)
//
//        val productsToProcess = when (referenceFireBas?.updateMode) {
//            UiState.ReferencesFireBaseGroup.UpdateMode.ALL -> {
//                productsSnapshot.children.toList()
//            }
//            UiState.ReferencesFireBaseGroup.UpdateMode.TOTAL_QUANTITY_GROSSIST_COMMAND_ABOVE_ZERO -> {
//                val productIds = referenceFireBas.produit_Update_Ref.map { it.id }
//                productsSnapshot.children.filter { productSnapshot ->
//                    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java)
//                    val totalQuantity = calculateTotalSupplierQuantity(productSnapshot)
//                    productIds.contains(idArticle) && totalQuantity > 0
//                }
//            }
//            null -> {
//                Log.w(TAG_Snap, "No update mode specified, processing all products")
//                productsSnapshot.children.toList()
//            }
//        }
//
//        val totalProducts = productsToProcess.size
//        Log.d(TAG_Snap, "Processing $totalProducts products")
//        var processedProducts = 0
//
//        val processedProductsList = productsToProcess.mapNotNull { productSnapshot ->
//            try {
//                val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java)
//                Log.d(TAG_Snap, "Processing product ID: $idArticle")
//
//                process_Cree_Product(productSnapshot, soldArticlesSnapshot).also {
//                    processedProducts++
//                    Log.d(TAG_Snap, "Completed processing product $idArticle ($processedProducts/$totalProducts)")
//                    onProgressUpdate(0.3f + (processedProducts.toFloat() / totalProducts * 0.4f))
//                }
//            } catch (e: Exception) {
//                Log.e(TAG_Snap, "Error processing product", e)
//                null
//            }
//        }.toMutableStateList()
//
//        Log.d(TAG_Snap, "Completed processing all products. Total processed: ${processedProductsList.size}")
//        onProgressUpdate(0.7f)
//        processedProductsList
//
//    } catch (e: Exception) {
//        Log.e(TAG_Snap, "Error in processes_Organiseur", e)
//        mutableStateListOf()
//    }
//}
//
//private fun calculateTotalSupplierQuantity(productSnapshot: DataSnapshot): Int {
//    return try {
//        productSnapshot.child("grossist_Choisi_Pour_Acheter_CeProduit")
//            .children
//            .sumOf { it.child("quantity_Achete").getValue(Int::class.java) ?: 0 }
//    } catch (e: Exception) {
//        Log.e(TAG_Snap, "Error calculating total supplier quantity", e)
//        0
//    }
//}
