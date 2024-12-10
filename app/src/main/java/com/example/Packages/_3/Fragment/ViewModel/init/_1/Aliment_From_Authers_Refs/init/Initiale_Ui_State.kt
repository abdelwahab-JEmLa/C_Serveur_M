package com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.init

import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.Models.addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.Models.clear_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.Models.toMap
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val TAG = "InitialeUiState"
private const val TAG_COLOR = "ColorLoading"

internal suspend fun getSupplierArticlesData(idArticle: Long): DataSnapshot? {
    return try {
        Firebase.database.getReference("K_SupplierArticlesRecived")
            .orderByChild("a_c_idarticle_c")
            .equalTo(idArticle.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching supplier data for article $idArticle", e)
        null
    }
}

private suspend fun getColorData(colorId: Long): ColorArticle? {
    return try {
        val colorSnapshot = Firebase.database.getReference("H_ColorsArticles")
            .orderByChild("idColore")
            .equalTo(colorId.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        val colorData = colorSnapshot?.getValue(ColorArticle::class.java)

        when {
            colorData == null -> Log.w(TAG_COLOR, "No color data found for ID $colorId")
            colorData.iconColore.isNullOrEmpty() -> Log.w(TAG_COLOR, "Empty emoji for colorId: $colorId")
            else -> Log.d(TAG_COLOR, "Successfully loaded emoji for colorId: $colorId, emoji: ${colorData.iconColore}")
        }

        colorData ?: colorSnapshot?.let {
            ColorArticle(
                idColore = it.child("idColore").getValue(Long::class.java) ?: colorId,
                nameColore = it.child("nameColore").getValue(String::class.java) ?: "",
                iconColore = it.child("iconColore").getValue(String::class.java) ?: "",
                classementColore = it.child("classementColore").getValue(Int::class.java) ?: 0
            )
        }
    } catch (e: Exception) {
        Log.e(TAG_COLOR, "Error fetching color data for color $colorId", e)
        null
    }
}

suspend fun getSupplierInfosData(idSupplierSu: Long): Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit? {
    return try {
        val supplierSnapshot = Firebase.database.getReference("F_Suppliers")
            .orderByChild("idSupplierSu")
            .equalTo(idSupplierSu.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        supplierSnapshot?.let { snapshot ->
            Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit(
                id = idSupplierSu,
                position_Grossist_Don_Parent_Grossists_List = snapshot.child("position")
                    .getValue(Int::class.java) ?: 0,
                nom = snapshot.child("nomSupplierSu").getValue(String::class.java) ?: "",
                couleur = snapshot.child("couleurSu").getValue(String::class.java) ?: "#FFFFFF",
                currentCreditBalance = snapshot.child("currentCreditBalance")
                    .getValue(Double::class.java) ?: 0.0,
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching supplier data for ID $idSupplierSu", e)
        null
    }
}

private fun createColorsEtGoutsAcheter(
    colorId: Long,
    colorName: String,
    quantity: Int,
    position: Long,
    colorData: ColorArticle?
): Ui_Mutable_State.Produits_Commend_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter {
    return Ui_Mutable_State.Produits_Commend_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter(
        vidPosition = position,
        id_Don_Tout_Couleurs = colorId,
        nom = colorName,
        quantity_Achete = quantity,
        imogi = colorData?.iconColore ?: ""
    )
}

private suspend fun getClientData(clientId: Long): String {
    return try {
        val clientSnapshot = Firebase.database.getReference("G_Clients")
            .orderByChild("idClientsSu")
            .equalTo(clientId.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        clientSnapshot?.child("nomClientsSu")?.getValue(String::class.java) ?: ""
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching client data for ID $clientId", e)
        ""
    }
}

// Update processVentesData function to include client name
private suspend fun processVentesData(
    soldArticlesSnapshot: DataSnapshot,
    productId: Long
): List<Ui_Mutable_State.Produits_Commend_DataBase.Demmende_Achate_De_Cette_Produit> {
    return soldArticlesSnapshot.children.mapNotNull { soldArticle ->
        val soldData =
            soldArticle.getValue(SoldArticlesTabelle::class.java) ?: return@mapNotNull null
        if (soldData.idArticle != productId) return@mapNotNull null

        // Fetch client name
        val clientName = getClientData(soldData.clientSoldToItId)

        val colorsList = buildList {
            // Process color1
            if (soldData.color1IdPicked != 0L && soldData.color1SoldQuantity > 0) {
                val colorData = getColorData(soldData.color1IdPicked)
                add(
                    createColorsEtGoutsAcheter(
                        soldData.color1IdPicked,
                        colorData?.nameColore ?: "",
                        soldData.color1SoldQuantity,
                        1L,
                        colorData
                    )
                )
            }
            // Process color2
            if (soldData.color2IdPicked != 0L && soldData.color2SoldQuantity > 0) {
                val colorData = getColorData(soldData.color2IdPicked)
                add(
                    createColorsEtGoutsAcheter(
                        soldData.color2IdPicked,
                        colorData?.nameColore ?: "",
                        soldData.color2SoldQuantity,
                        2L,
                        colorData
                    )
                )
            }
            // Process color3
            if (soldData.color3IdPicked != 0L && soldData.color3SoldQuantity > 0) {
                val colorData = getColorData(soldData.color3IdPicked)
                add(
                    createColorsEtGoutsAcheter(
                        soldData.color3IdPicked,
                        colorData?.nameColore ?: "",
                        soldData.color3SoldQuantity,
                        3L,
                        colorData
                    )
                )
            }
            // Process color4
            if (soldData.color4IdPicked != 0L && soldData.color4SoldQuantity > 0) {
                val colorData = getColorData(soldData.color4IdPicked)
                add(
                    createColorsEtGoutsAcheter(
                        soldData.color4IdPicked,
                        colorData?.nameColore ?: "",
                        soldData.color4SoldQuantity,
                        4L,
                        colorData
                    )
                )
            }
        }

        // Get current timestamp
        val now = LocalDateTime.now()
        val currentTimeMillis = now.toInstant(ZoneOffset.UTC).toEpochMilli()
        val currentDateMillis =
            now.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        Ui_Mutable_State.Produits_Commend_DataBase.Demmende_Achate_De_Cette_Produit(
            vid = soldData.vid,
            id_Acheteur = soldData.clientSoldToItId,
            nom_Acheteur = clientName,
            inseartion_Temp = currentTimeMillis,
            inceartion_Date = currentDateMillis,
            colours_Et_Gouts_Acheter = colorsList
        )
    }
}
private suspend fun processProduct(
    productSnapshot: DataSnapshot,
    soldArticlesSnapshot: DataSnapshot
): Ui_Mutable_State.Produits_Commend_DataBase {
    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L
    val idSupplierSu = productSnapshot.child("idSupplierSu").getValue(Long::class.java) ?: 0L

    // Fetch supplier and article data concurrently
    val (supplierInfosData, supplierData) = coroutineScope {
        val supplierInfosDeferred = async { getSupplierInfosData(idSupplierSu) }
        val supplierDataDeferred = async { getSupplierArticlesData(idArticle) }
        Pair(supplierInfosDeferred.await(), supplierDataDeferred.await())
    }

    // Process colors
    val colorsList = buildList {
        for (i in 1..4) {
            val colorField = "couleur$i"
            val idColorField = "idcolor$i"

            val color = productSnapshot.child(colorField).getValue(String::class.java)
            if (!color.isNullOrEmpty()) {
                val colorId = productSnapshot.child(idColorField).getValue(Long::class.java) ?: 0L
                val quantity =
                    supplierData?.child("color${i}SoldQuantity")?.getValue(Int::class.java) ?: 0

                val colorData = getColorData(colorId)

                add(
                    Ui_Mutable_State.Produits_Commend_DataBase.Colours_Et_Gouts_Commende(
                        position_Du_Couleur_Au_Produit = i.toLong(),
                        id_Don_Tout_Couleurs = colorId,
                        nom = color,
                        quantity_Achete = quantity,
                        imogi = colorData?.iconColore ?: ""
                    )
                )
            }
        }
    }

    // Process sales data
    val ventesData = processVentesData(soldArticlesSnapshot, idArticle)

    return Ui_Mutable_State.Produits_Commend_DataBase(
        id = idArticle.toInt(),
        nom = productSnapshot.child("nomArticleFinale").getValue(String::class.java) ?: "",
        colours_Et_Gouts_Commende = colorsList,
        vent_List_DataBase = ventesData,
        grossist_Choisi_Pour_Acheter_CeProduit = supplierInfosData
    )
}

// Update the Aliment_Fragment3_Ui_State function to use processProduct
internal suspend fun Aliment_Fragment3_Ui_State(
    onProgressUpdate: (Float) -> Unit
): List<Ui_Mutable_State.Produits_Commend_DataBase> = coroutineScope {
    try {
        onProgressUpdate(0.1f)

        // Fetch products from Firebase
        val productsSnapshot = Firebase.database.getReference("e_DBJetPackExport")
            .get()
            .await()

        onProgressUpdate(0.3f)

        // Fetch sold articles data once
        val soldArticlesSnapshot = Firebase.database.getReference("ArticlesAcheteModele")
            .get()
            .await()

        onProgressUpdate(0.5f)

        val totalProducts = productsSnapshot.children.count()
        var processedProducts = 0

        productsSnapshot.children.map { productSnapshot ->
            async {
                val result = processProduct(productSnapshot, soldArticlesSnapshot)
                processedProducts++
                onProgressUpdate(0.5f + (processedProducts.toFloat() / totalProducts * 0.5f))
                result
            }
        }.awaitAll()
    } catch (e: Exception) {
        Log.e(TAG, "Error processing products", e)
        emptyList()
    }
}

// Updated Init_ImportCalcules_Ui_Stat.kt
internal suspend fun P3_ViewModel.Init_ImportCalcules_Ui_Stat(
    onProgressUpdate: (Float) -> Unit
) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f) // Started

            // Fetch product data
            onProgressUpdate(0.2f)
            val productsData = Aliment_Fragment3_Ui_State { progress ->
                // Map inner progress (0-1) to range 0.2-0.8
                onProgressUpdate(0.2f + (progress * 0.6f))
            }

            onProgressUpdate(0.8f) // Data fetched

            // Get device name
            val phoneName = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

            // Create a copy of products with potential future-proofing
            onProgressUpdate(0.9f)
            val updatedProducts = productsData.map { product ->
                product.copy(
                    grossist_Choisi_Pour_Acheter_CeProduit = product.grossist_Choisi_Pour_Acheter_CeProduit?.copy()
                )
            }

            // Update UI state
            _ui_Mutable_State.apply {
                clear_Ui_Mutable_State_C_produits_Commend_DataBase()
                addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(updatedProducts)
                namePhone = phoneName
            }

            // Sync with Firebase
            refFirebase.setValue(_ui_Mutable_State.toMap())

            onProgressUpdate(1.0f) // Completed

        } catch (e: Exception) {
            Log.e(TAG, "Initialization error", e)
            onProgressUpdate(1.0f) // Ensure progress bar completes even on error
        }
    }
}


