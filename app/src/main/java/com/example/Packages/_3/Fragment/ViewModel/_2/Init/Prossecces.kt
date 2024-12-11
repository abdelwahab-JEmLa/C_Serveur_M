package com.example.Packages._3.Fragment.ViewModel._2.Init

import android.util.Log
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.ColorArticle
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.SoldArticlesTabelle
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.getClientData
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.getSupplierInfosData
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.ZoneOffset

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

suspend fun process_Cree_Product(
    productSnapshot: DataSnapshot,
    soldArticlesSnapshot: DataSnapshot
): Ui_Mutable_State.Produits_Commend_DataBase {
    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L
    val idSupplierSu = productSnapshot.child("idSupplierSu").getValue(Long::class.java) ?: 0L

    Log.d(
        TAG_Snap,
        "Processing product $idArticle with supplier $idSupplierSu"
    )

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
                Log.d(
                    TAG_Snap,
                    "Processing color $i: ID=$colorId, name=$color"
                )

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
    Log.d(
        TAG_Snap,
        "Processed ${ventesData.size} sales records for product $idArticle"
    )

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
            Log.d(
                TAG_Snap,
                "Processing sale for product $productId to client $clientName"
            )

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
            Log.e(
                TAG_Snap,
                "Error processing sale data for product $productId",
                e
            )
            null
        }
    }
}



