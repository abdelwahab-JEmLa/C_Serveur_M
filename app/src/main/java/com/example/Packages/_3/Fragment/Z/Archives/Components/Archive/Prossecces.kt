package com.example.Packages._3.Fragment.Z.Archives.Components.Archive
//
//import android.util.Log
//import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.TAG_Snap
//import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ancien_SoldArticlesTabelle
//import com.example.Packages._3.Fragment.Models.UiState
//import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.getClientData
//import com.google.firebase.database.DataSnapshot
//import kotlinx.coroutines.async
//import kotlinx.coroutines.coroutineScope
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//
//suspend fun process_Cree_Product(
//    productSnapshot: DataSnapshot,
//    soldArticlesSnapshot: DataSnapshot
//): UiState.Produit_DataBase {
//    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L
//    val idSupplierSu = productSnapshot.child("idSupplierSu").getValue(Long::class.java) ?: 0L
//
//    Log.d(TAG_Snap, "Processing product $idArticle with supplier $idSupplierSu")
//
//    val (supplierInfosData, supplierData) = coroutineScope {
//        val supplierInfosDeferred = async { get_Grossist_Choisi_Pour_Acheter_Ce_Produit(idSupplierSu) }
//        val supplierDataDeferred = async { getSupplierArticlesData(idArticle) }
//        Pair(supplierInfosDeferred.await(), supplierDataDeferred.await())
//    }
//
//    // Convert the single supplier info into a list
//    val supplierInfosList = supplierInfosData?.let { listOf(it) } ?: emptyList()
//
//    val colorsList = buildList {
//        for (i in 1..4) {
//            val colorField = "couleur$i"
//            val idColorField = "idcolor$i"
//
//            val color = productSnapshot.child(colorField).getValue(String::class.java)
//            if (!color.isNullOrEmpty()) {
//                val colorId = productSnapshot.child(idColorField).getValue(Long::class.java) ?: 0L
//                val quantity = supplierData?.child("color${i}SoldQuantity")?.getValue(Int::class.java) ?: 0
//
//                val colorData = getColorData(colorId)
//                Log.d(TAG_Snap, "Processing color $i: ID=$colorId, name=$color")
//
//                add(
//                    UiState.Produit_DataBase.Colours_Et_Gouts_Commende_Au_Supplier(
//                        position_Du_Couleur_Au_Produit = i.toLong(),
//                        id_Don_Tout_Couleurs = colorId,
//                        nom = color,
//                        quantity_Achete = quantity,
//                        imogi = colorData?.iconColore ?: ""
//                    )
//                )
//            }
//        }
//    }
//
//    val ventesData = process_Ventes_Data_Createur(soldArticlesSnapshot, idArticle)
//    Log.d(TAG_Snap, "Processed ${ventesData.size} sales records for product $idArticle")
//
//    return UiState.Produit_DataBase(
//        id = idArticle,
//        nom = productSnapshot.child("nomArticleFinale").getValue(String::class.java) ?: "",
//        initialColours_Et_Gouts_Commende_Au_Supplier = colorsList,
//        initialDemmende_Achate_De_Cette_Produit = ventesData,
//        initialGrossist_Choisi_Pour_Acheter_CeProduit = supplierInfosList // Now passing a List
//    )
//}
//
//private suspend fun process_Ventes_Data_Createur(
//    soldArticlesSnapshot: DataSnapshot,
//    productId: Long
//): List<UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit> {
//    return soldArticlesSnapshot.children.mapNotNull { soldArticle ->
//        try {
//            val soldData = soldArticle.getValue(Ancien_SoldArticlesTabelle::class.java)
//                ?: return@mapNotNull null
//            if (soldData.idArticle != productId) return@mapNotNull null
//
//            val clientName = getClientData(soldData.clientSoldToItId)
//            Log.d(TAG_Snap, "Processing sale for product $productId to client $clientName")
//
//            // Create a mutable list to store colors
//            val colorsList = mutableListOf<UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client>()
//
//            // Process each color and add to the list if it exists and has quantity
//            processColorAndAddToList(soldData.color1IdPicked, soldData.color1SoldQuantity, 1L, colorsList)
//            processColorAndAddToList(soldData.color2IdPicked, soldData.color2SoldQuantity, 2L, colorsList)
//            processColorAndAddToList(soldData.color3IdPicked, soldData.color3SoldQuantity, 3L, colorsList)
//            processColorAndAddToList(soldData.color4IdPicked, soldData.color4SoldQuantity, 4L, colorsList)
//
//            val now = LocalDateTime.now()
//            UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit(
//                vid = soldData.vid,
//                id_Acheteur = soldData.clientSoldToItId,
//                nom_Acheteur = clientName ?: "",
//                inseartion_Temp = now.toInstant(ZoneOffset.UTC).toEpochMilli(),
//                inceartion_Date = now.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()
//                    .toEpochMilli(),
//                initial_Colours_Et_Gouts_Acheter_Depuit_Client = colorsList
//            )
//        } catch (e: Exception) {
//            Log.e(TAG_Snap, "Error processing sale data for product $productId", e)
//            null
//        }
//    }
//}
//
//// Helper function to process colors
//private suspend fun processColorAndAddToList(
//    colorId: Long,
//    quantity: Int,
//    position: Long,
//    colorsList: MutableList<UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client>
//) {
//    if (colorId != 0L && quantity > 0) {
//        val colorData = getColorData(colorId)
//        colorData?.let {
//            colorsList.add(
//                UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client(
//                    vidPosition = position,
//                    id_Don_Tout_Couleurs = colorId,
//                    nom = it.nameColore,
//                    quantity_Achete = quantity,
//                    imogi = it.iconColore
//                )
//            )
//        }
//    }
//}
