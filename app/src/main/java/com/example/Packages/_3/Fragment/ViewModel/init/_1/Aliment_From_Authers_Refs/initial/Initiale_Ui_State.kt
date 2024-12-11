package com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial

import android.os.Build
import android.util.Log
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.Models.update_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.random.Random

const val TAG = "InitialeUiState"

internal suspend fun P3_ViewModel.Init_Cree_Ui_State(
    onProgressUpdate: (Float) -> Unit = {},
) {
    try {
        Log.d(TAG, "Starting Init_Cree_Ui_State")
        onProgressUpdate(0.1f)
        onProgressUpdate(0.2f)
        val randomProducts = List(200) { index ->
            Ui_Mutable_State.Groupeur_References_FireBase_DataBase.Produits_A_Update(
                id = Random.nextInt(500, 700).toLong(),
                position = index + 1,
                ref = "product_${index + 1}",
                nom = "Test Product ${index + 1}",
                tiggr_Time = System.currentTimeMillis()
            )
        }
        val defaultGroupRef = Ui_Mutable_State.Groupeur_References_FireBase_DataBase(
            id = 1L,
            position = 1,
            ref = "Produits_Commend_DataBase",
            nom = "Produits_Commend_DataBase",
            description = "Default group for Ref products",
            last_Update_Time_Formatted = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            update_All = false
        )





        _ui_Mutable_State.groupeur_References_FireBase_DataBaseSnap.find { it.id == 1L }?.let {
            it.id = 1L
            it.position = 1
            it.ref = "Produits_Commend_DataBase"
            it.nom = "Produits_Commend_DataBase"
            it.description = "Default group for Ref products"
            it.updateFirebaseSelfF(it)
        }
        val uiStateSnapshot = Firebase.database
            .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
            .get()
            .await()

        val uiState = uiStateSnapshot.getValue(Ui_Mutable_State::class.java)
        Log.d(TAG, "Retrieved UI state from Firebase")

        val phoneName = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

        uiState?.let { state ->
            val productsToUpdate = state.groupeur_References_FireBase_DataBase
                .firstOrNull { it.id == 1L }
                ?.produits_A_Update
                ?.map { it.id }
                .orEmpty()
                .toSet()

            val updateAll = state.groupeur_References_FireBase_DataBase
                .find { it.id == 1L }?.update_All == true

            val productsData = processes_Organiseur(
                updateAll = updateAll,
                productsToUpdate = productsToUpdate,
                onProgressUpdate = { progress ->
                    onProgressUpdate(0.2f + (progress * 0.6f))
                }
            )

            Log.d(TAG, "Processed ${productsData.size} products")

            _ui_Mutable_State.apply {
                namePhone = phoneName
                produits_Commend_DataBase = productsData
            }

            productsData.forEach { product ->
                state.update_Ui_Mutable_State_C_produits_Commend_DataBase(product)
            }

            val defaultGroupRef = Ui_Mutable_State.Groupeur_References_FireBase_DataBase(
                id = 1L,
                position = 1,
                ref = "Produits_Commend_DataBase",
                nom = "Produits_Commend_DataBase",
                description = "Default group for Ref products",
                last_Update_Time_Formatted = LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                update_All = false,
            )

            defaultGroupRef.updateFirebaseSelfF(defaultGroupRef)
            Log.d(TAG, "Updated Firebase references")

            _ui_Mutable_State.groupeur_References_FireBase_DataBase =
                state.groupeur_References_FireBase_DataBase.map { ref ->
                    if (ref.id == 1L) {
                        ref.copy(update_All = false)
                    } else ref
                }
        }

        onProgressUpdate(0.9f)
        onProgressUpdate(0.0f)
        Log.d(TAG, "Completed Init_Cree_Ui_State")
    } catch (e: Exception) {
        Log.e(TAG, "Error initializing UI state", e)
        throw e
    }
}

internal suspend fun processes_Organiseur(
    productsToUpdate: Set<Long>,
    onProgressUpdate: (Float) -> Unit,
    updateAll: Boolean
): List<Ui_Mutable_State.Produits_Commend_DataBase> = coroutineScope {
    try {
        Log.d(TAG, "Starting processes_Organiseur with updateAll=$updateAll, productsToUpdate=${productsToUpdate.size} items")
        onProgressUpdate(0.1f)

        val (productsSnapshot, soldArticlesSnapshot) = coroutineScope {
            val productsDeferred = async {
                Firebase.database.getReference("e_DBJetPackExport")
                    .get()
                    .await()
            }
            val soldArticlesDeferred = async {
                Firebase.database.getReference("ArticlesAcheteModele")
                    .get()
                    .await()
            }
            Pair(productsDeferred.await(), soldArticlesDeferred.await())
        }

        Log.d(TAG, "Retrieved data - Products: ${productsSnapshot.childrenCount}, Sold Articles: ${soldArticlesSnapshot.childrenCount}")
        onProgressUpdate(0.3f)

        val productsToProcess = productsSnapshot.children.toList()
        //TODO(1): fait que si updateAll
        val totalProducts = productsToProcess.size
        Log.d(TAG, "Processing $totalProducts products")
        var processedProducts = 0

        val processedProductsList = productsToProcess.mapNotNull { productSnapshot ->
            try {
                val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java)
                Log.d(TAG, "Processing product ID: $idArticle")

                process_Cree_Product(productSnapshot, soldArticlesSnapshot).also {
                    processedProducts++
                    Log.d(
                        TAG,
                        "Completed processing product $idArticle ($processedProducts/$totalProducts)"
                    )
                    onProgressUpdate(0.3f + (processedProducts.toFloat() / totalProducts * 0.4f))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing product", e)
                null
            }
        }

        Log.d(TAG, "Completed processing all products. Total processed: ${processedProductsList.size}")
        onProgressUpdate(0.7f)
        processedProductsList

    } catch (e: Exception) {
        Log.e(TAG, "Error in processes_Organiseur", e)
        emptyList()
    }
}

private suspend fun process_Cree_Product(
    productSnapshot: DataSnapshot,
    soldArticlesSnapshot: DataSnapshot
): Ui_Mutable_State.Produits_Commend_DataBase {
    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L
    val idSupplierSu = productSnapshot.child("idSupplierSu").getValue(Long::class.java) ?: 0L

    Log.d(TAG, "Processing product $idArticle with supplier $idSupplierSu")

    val (supplierInfosData, supplierData) = coroutineScope {
        val supplierInfosDeferred = async { getSupplierInfosData(idSupplierSu) }
        val supplierDataDeferred = async { getSupplierArticlesData(idArticle) }
        Pair(supplierInfosDeferred.await(), supplierDataDeferred.await())
    }

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
                Log.d(TAG, "Processing color $i: ID=$colorId, name=$color")

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

    val ventesData = process_Ventes_Data_Createur(soldArticlesSnapshot, idArticle)
    Log.d(TAG, "Processed ${ventesData.size} sales records for product $idArticle")

    return Ui_Mutable_State.Produits_Commend_DataBase(
        id = idArticle.toInt(),
        nom = productSnapshot.child("nomArticleFinale").getValue(String::class.java) ?: "",
        colours_Et_Gouts_Commende = colorsList,
        vent_List_DataBase = ventesData,
        grossist_Choisi_Pour_Acheter_CeProduit = supplierInfosData
    )
}

private suspend fun process_Ventes_Data_Createur(
    soldArticlesSnapshot: DataSnapshot,
    productId: Long
): List<Ui_Mutable_State.Produits_Commend_DataBase.Demmende_Achate_De_Cette_Produit> {
    return soldArticlesSnapshot.children.mapNotNull { soldArticle ->
        try {
            val soldData = soldArticle.getValue(SoldArticlesTabelle::class.java)
                ?: return@mapNotNull null
            if (soldData.idArticle != productId) return@mapNotNull null

            val clientName = getClientData(soldData.clientSoldToItId)
            Log.d(TAG, "Processing sale for product $productId to client $clientName")

        val colorsList = buildList {
            if (soldData.color1IdPicked != 0L && soldData.color1SoldQuantity > 0) {
                val colorData = getColorData(soldData.color1IdPicked)
                add(
                    procces_Create_Colors_Acheter(
                        soldData.color1IdPicked, colorData?.nameColore ?: "",
                        soldData.color1SoldQuantity, 1L, colorData
                    )
                )
            }
            if (soldData.color2IdPicked != 0L && soldData.color2SoldQuantity > 0) {
                val colorData = getColorData(soldData.color2IdPicked)
                add(
                    procces_Create_Colors_Acheter(
                        soldData.color2IdPicked, colorData?.nameColore ?: "",
                        soldData.color2SoldQuantity, 2L, colorData
                    )
                )
            }
            if (soldData.color3IdPicked != 0L && soldData.color3SoldQuantity > 0) {
                val colorData = getColorData(soldData.color3IdPicked)
                add(
                    procces_Create_Colors_Acheter(
                        soldData.color3IdPicked, colorData?.nameColore ?: "",
                        soldData.color3SoldQuantity, 3L, colorData
                    )
                )
            }
            if (soldData.color4IdPicked != 0L && soldData.color4SoldQuantity > 0) {
                val colorData = getColorData(soldData.color4IdPicked)
                add(
                    procces_Create_Colors_Acheter(
                        soldData.color4IdPicked, colorData?.nameColore ?: "",
                        soldData.color4SoldQuantity, 4L, colorData
                    )
                )
            }
        }

            val now = LocalDateTime.now()
            Ui_Mutable_State.Produits_Commend_DataBase.Demmende_Achate_De_Cette_Produit(
                vid = soldData.vid,
                id_Acheteur = soldData.clientSoldToItId,
                nom_Acheteur = clientName,
                inseartion_Temp = now.toInstant(ZoneOffset.UTC).toEpochMilli(),
                inceartion_Date = now.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()
                    .toEpochMilli(),
                colours_Et_Gouts_Acheter = colorsList
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing sale data for product $productId", e)
            null
        }
    }
}

private fun procces_Create_Colors_Acheter(
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

// Helper functions from your existing code
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

suspend fun getColorData(colorId: Long): ColorArticle? {
    return try {
        val colorSnapshot = Firebase.database.getReference("H_ColorsArticles")
            .orderByChild("idColore")
            .equalTo(colorId.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        colorSnapshot?.getValue(ColorArticle::class.java) ?: colorSnapshot?.let {
            ColorArticle(
                idColore = it.child("idColore").getValue(Long::class.java) ?: colorId,
                nameColore = it.child("nameColore").getValue(String::class.java) ?: "",
                iconColore = it.child("iconColore").getValue(String::class.java) ?: "",
                classementColore = it.child("classementColore").getValue(Int::class.java) ?: 0
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching color data for color $colorId", e)
        null
    }
}
