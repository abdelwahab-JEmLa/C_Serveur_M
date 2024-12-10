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
import kotlin.random.Random

const val TAG = "InitialeUiState"

internal suspend fun P3_ViewModel.Init_Cree_Ui_State(
    onProgressUpdate: (Float) -> Unit = {}
) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            val productsToUpdate = _ui_Mutable_State.groupeur_References_FireBase_DataBase
                .firstOrNull { it.id == 1L }
                ?.produits_A_Update
                ?.map { it.id }
                .orEmpty()
                .toSet()

            if (productsToUpdate.isNotEmpty()) {
                updateAlimentation(productsToUpdate, onProgressUpdate)
            } else {
                simpleAlimentation(onProgressUpdate)
            }

            onProgressUpdate(1.0f)

        } catch (e: Exception) {
            Log.e(TAG, "Initialization error", e)
            onProgressUpdate(1.0f)
        }
    }
}

internal suspend fun P3_ViewModel.updateAlimentation(
    productsToUpdate: Set<Long>,
    onProgressUpdate: (Float) -> Unit = {}
) {
    val productsData = if (productsToUpdate.isEmpty()) {
        val productsSnapshot = Firebase.database.getReference("e_DBJetPackExport").get().await()
        productsSnapshot.children.map { productSnapshot ->
            Ui_Mutable_State.Produits_Commend_DataBase(
                id = productSnapshot.child("idArticle").getValue(Long::class.java)?.toInt() ?: 0
            )
        }
    } else {
        processes_Organiseur(
            productsToUpdate = productsToUpdate,
            onProgressUpdate = { progress ->
                onProgressUpdate(0.2f + (progress * 0.6f))
            }
        )
    }

    val phoneName = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

    val updatedProducts = productsData.map { product ->
        product.copy(
            grossist_Choisi_Pour_Acheter_CeProduit = if (product.grossist_Choisi_Pour_Acheter_CeProduit == null)
                generateRandomSupplier() else product.grossist_Choisi_Pour_Acheter_CeProduit
        )
    }

    _ui_Mutable_State.apply {
        clear_Ui_Mutable_State_C_produits_Commend_DataBase()
        addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(updatedProducts)
        namePhone = phoneName
    }

    refFirebase.setValue(_ui_Mutable_State.toMap())
}

internal suspend fun P3_ViewModel.simpleAlimentation(onProgressUpdate: (Float) -> Unit = {}) {
    val uiStateSnapshot = Firebase.database
        .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
        .get()
        .await()

    val uiState = uiStateSnapshot.getValue(Ui_Mutable_State::class.java)

    val phoneName = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

    uiState?.let { state ->
        _ui_Mutable_State.apply {
            val updatedProducts = state.produits_Commend_DataBase.map { product ->
                product.copy(
                    grossist_Choisi_Pour_Acheter_CeProduit = if (product.grossist_Choisi_Pour_Acheter_CeProduit == null)
                        generateRandomSupplier() else product.grossist_Choisi_Pour_Acheter_CeProduit
                )
            }

            this.produits_Commend_DataBase = updatedProducts
            this.namePhone = phoneName
            this.selectedSupplierId = state.selectedSupplierId
            this.mode_Update_Produits_Non_Defini_Grossist =
                state.mode_Update_Produits_Non_Defini_Grossist
            this.mode_Trie_Produit_Non_Trouve = state.mode_Trie_Produit_Non_Trouve
            this.currentMode = state.currentMode
        }
    }

    onProgressUpdate(0.9f)
}

internal fun generateRandomSupplier(): Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit {
    val colors = listOf(
        "#FF5733", "#33FF57", "#3357FF", "#FF33F1",
        "#33FFF1", "#F1FF33", "#8E44AD", "#3498DB"
    )

    val randomNum = Random.nextInt(0, 6)
    val position = when {
        randomNum == 0 -> 0
        Random.nextBoolean() -> -Random.nextInt(1, 5)
        else -> Random.nextInt(1, 5)
    }

    return Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit(
        id = randomNum.toLong(),
        nom = if (randomNum == 0) "Undefined Supplier" else "Grossiste $randomNum",
        position_Grossist_Don_Parent_Grossists_List = position,
        couleur = colors.random(),
        currentCreditBalance = Random.nextDouble(0.0, 10000.0),
        position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = position
    )
}

internal suspend fun processes_Organiseur(
    productsToUpdate: Set<Long>,
    onProgressUpdate: (Float) -> Unit
): List<Ui_Mutable_State.Produits_Commend_DataBase> = coroutineScope {
    try {
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

        onProgressUpdate(0.3f)

        val productsToProcess = if (productsToUpdate.isEmpty()) {
            productsSnapshot.children
        } else {
            productsSnapshot.children.filter { productSnapshot ->
                val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L
                productsToUpdate.contains(idArticle)
            }
        }

        val totalProducts = productsToProcess.count()
        var processedProducts = 0

        val processedProductsList = productsToProcess.map { productSnapshot ->
            async {
                val result = process_Cree_Product(productSnapshot, soldArticlesSnapshot)
                processedProducts++
                onProgressUpdate(0.3f + (processedProducts.toFloat() / totalProducts * 0.4f))
                result
            }
        }.awaitAll()

        onProgressUpdate(0.7f)
        processedProductsList

    } catch (e: Exception) {
        Log.e(TAG, "Error processing products", e)
        emptyList()
    }
}

private suspend fun process_Cree_Product(
    productSnapshot: DataSnapshot,
    soldArticlesSnapshot: DataSnapshot
): Ui_Mutable_State.Produits_Commend_DataBase {
    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L
    val idSupplierSu = productSnapshot.child("idSupplierSu").getValue(Long::class.java) ?: 0L

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
        val soldData =
            soldArticle.getValue(SoldArticlesTabelle::class.java) ?: return@mapNotNull null
        if (soldData.idArticle != productId) return@mapNotNull null

        val clientName = getClientData(soldData.clientSoldToItId)

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
