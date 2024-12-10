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
import java.time.format.DateTimeFormatter

const val TAG = "InitialeUiState"
const val TAG_COLOR = "ColorLoading"

private suspend fun createNewReference(
): Ui_Mutable_State.Groupeur_References_FireBase_DataBase {
    val now = LocalDateTime.now()

    // Format the timestamp in yyyy/MM/dd-HH:mm:ss
    val formattedTimestamp = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss"))

    // Get the next available ID and position
    val referencesSnapshot = Firebase.database
        .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
        .child("1_Groupeur_References_FireBase_DataBase")
        .get()
        .await()

    val maxId = referencesSnapshot.children
        .mapNotNull { it.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)?.id }
        .maxOrNull() ?: 0L

    val maxPosition = referencesSnapshot.children
        .mapNotNull { it.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)?.position }
        .maxOrNull() ?: 0

    return Ui_Mutable_State.Groupeur_References_FireBase_DataBase(
        id = maxId + 1,
        position = maxPosition + 1,
        nom = "Produits_Commend_DataBase",
        description = "Produits_Commend_DataBase",
        ref = "Produits_Commend_DataBase",
        last_Update_Time_Formatted = formattedTimestamp
    )
}

// Updated fetchFirebaseReferences function
private suspend fun fetchFirebaseReferences(products: List<Ui_Mutable_State.Produits_Commend_DataBase>): List<Ui_Mutable_State.Groupeur_References_FireBase_DataBase> {
    val referencesRef = Firebase.database
        .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
        .child("1_References_FireBase_DataBase")

    val existingReferences = try {
        referencesRef.get()
            .await()
            .children
            .mapNotNull { snapshot ->
                snapshot.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)
            }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching Firebase references", e)
        emptyList()
    }

    val updatedReferences = mutableListOf<Ui_Mutable_State.Groupeur_References_FireBase_DataBase>()
    updatedReferences.addAll(existingReferences)

    // Create references for products that don't have them
    products.forEach { product ->
        val hasReference = existingReferences.any { ref ->
            ref.produits_A_Update?.any { it.id == product.id.toLong() } == true
        }

        if (!hasReference) {
            try {
                val newReference = createNewReference()

                // Save the new reference to Firebase
                referencesRef.child(newReference.id.toString()).setValue(newReference).await()
                updatedReferences.add(newReference)

                Log.d(TAG, "Created new reference for product ${product.nom}")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating reference for product ${product.nom}", e)
            }
        }
    }

    return updatedReferences
}

// Updated Aliment_Fragment3_Ui_State function
internal suspend fun Aliment_Fragment3_Ui_State(
    onProgressUpdate: (Float) -> Unit
): List<Ui_Mutable_State.Produits_Commend_DataBase> = coroutineScope {
    try {
        onProgressUpdate(0.1f)

        // First fetch products
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

        val totalProducts = productsSnapshot.children.count()
        var processedProducts = 0

        // Process products first
        val processedProductsList = productsSnapshot.children.map { productSnapshot ->
            async {
                val result = processProduct(productSnapshot, soldArticlesSnapshot)
                processedProducts++
                onProgressUpdate(0.3f + (processedProducts.toFloat() / totalProducts * 0.4f))
                result
            }
        }.awaitAll()

        onProgressUpdate(0.7f)

        // Then fetch/create references based on processed products
        val references = fetchFirebaseReferences(processedProductsList)

        onProgressUpdate(0.8f)

        // Update products with reference data
        val updatedProducts = updateProductReferences(processedProductsList, references)

        onProgressUpdate(1.0f)
        updatedProducts

    } catch (e: Exception) {
        Log.e(TAG, "Error processing products", e)
        emptyList()
    }
}


// Function to update product references
private suspend fun updateProductReferences(
    products: List<Ui_Mutable_State.Produits_Commend_DataBase>,
    references: List<Ui_Mutable_State.Groupeur_References_FireBase_DataBase>
): List<Ui_Mutable_State.Produits_Commend_DataBase> {
    val now = LocalDateTime.now()
    val currentTimeMillis = now.toInstant(ZoneOffset.UTC).toEpochMilli()


    return products.map { product ->
        val matchingRef = references.find { ref ->
            ref.produits_A_Update?.any { it.id == product.id.toLong() } == true
        }

        if (matchingRef != null) {
            val productUpdate = matchingRef.produits_A_Update?.find { it.id == product.id.toLong() }
            product.copy(
                nom = productUpdate?.nom ?: product.nom
                // Add additional fields that need updating based on references
            )
        } else {
            product
        }
    }
}


// Updated Init_ImportCalcules_Ui_Stat function remains the same as in your original code
internal suspend fun P3_ViewModel.Init_ImportCalcules_Ui_Stat(
    onProgressUpdate: (Float) -> Unit
) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            val productsData = Aliment_Fragment3_Ui_State { progress ->
                onProgressUpdate(0.2f + (progress * 0.6f))
            }

            onProgressUpdate(0.8f)

            val phoneName = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

            onProgressUpdate(0.9f)
            val updatedProducts = productsData.map { product ->
                product.copy(
                    grossist_Choisi_Pour_Acheter_CeProduit = product.grossist_Choisi_Pour_Acheter_CeProduit?.copy()
                )
            }

            _ui_Mutable_State.apply {
                clear_Ui_Mutable_State_C_produits_Commend_DataBase()
                addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(updatedProducts)
                namePhone = phoneName
            }

            refFirebase.setValue(_ui_Mutable_State.toMap())

            onProgressUpdate(1.0f)

        } catch (e: Exception) {
            Log.e(TAG, "Initialization error", e)
            onProgressUpdate(1.0f)
        }
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

