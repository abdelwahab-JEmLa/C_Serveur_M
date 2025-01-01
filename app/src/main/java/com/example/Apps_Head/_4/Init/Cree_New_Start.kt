package com.example.Apps_Head._4.Init

import android.util.Log
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Apps_Head._4.Init.Z.Components.get_Ancien_DataBases_Main
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

suspend fun InitViewModel.cree_New_Start() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    try {
        initializationProgress = 0.1f
        isInitializing = true

        val ancienData = get_Ancien_DataBases_Main()

        ancienData.produitsDatabase.forEachIndexed { index, ancien ->
            try {
                val nouveauProduit = AppsHeadModel.ProduitModel(
                    id = ancien.idArticle,
                    it_ref_Id_don_FireBase = 1L,
                    it_ref_don_FireBase = "produit_DataBase"
                )

                nouveauProduit.apply {
                    nom = ancien.nomArticleFinale

                    val couleursIds = listOf(
                        ancien.idcolor1 to 1L,
                        ancien.idcolor2 to 2L,
                        ancien.idcolor3 to 3L,
                        ancien.idcolor4 to 4L
                    )

                    couleursIds.forEach { (colorId, position) ->
                        val couleurTrouvee = ancienData.couleurs_List
                            .find { it.idColore == colorId }

                        if (couleurTrouvee != null) {
                            coloursEtGouts.add(
                                AppsHeadModel.ProduitModel.ColourEtGout_Model(
                                    position_Du_Couleur_Au_Produit = position,
                                    nom = couleurTrouvee.nameColore,
                                    imogi = couleurTrouvee.iconColore
                                )
                            )
                        }
                    }

                    historiqueBonsVents.clear()
                    val nombreVentesHistoriques = (1..5).random()
                    repeat(nombreVentesHistoriques) { ventIndex ->
                        try {
                            val vente = generateHistoricalSale(ventIndex, dateFormat)
                            historiqueBonsVents.add(vente)
                        } catch (e: Exception) {
                            // Handle exception silently
                        }
                    }

                    bonsVentDeCetteCota.clear()
                    val nombreVentesActuelles = (1..3).random()
                    repeat(nombreVentesActuelles) { ventIndex ->
                        try {
                            val vente = generateCurrentSale(ventIndex, dateFormat)
                            bonsVentDeCetteCota.add(vente)
                        } catch (e: Exception) {
                            // Handle exception silently
                        }
                    }

                    // Only generate grossiste for the first 30 products
                    if (index < 30) {
                        try {
                            val grossiste = generateGrossiste()
                            bonCommendDeCetteCota = grossiste
                            historiqueBonsCommend.clear()
                            historiqueBonsCommend.add(grossiste)
                        } catch (e: Exception) {
                            // Handle exception silently
                        }
                    } else {
                        // For products after index 30, initialize empty lists
                        bonCommendDeCetteCota = null
                        historiqueBonsCommend.clear()
                    }

                    besoin_To_Be_Updated = false
                }

                _appsHead.produits_Main_DataBase.add(nouveauProduit)
            } catch (e: Exception) {
                // Handle exception silently
            }

            initializationProgress = 0.1f + (0.8f * (index + 1) / ancienData.produitsDatabase.size)
        }

        val ref_Produit_Main_DataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("produit_DataBase")

        try {
            withContext(Dispatchers.IO) {
                ref_Produit_Main_DataBase.setValue(
                    _appsHead.produits_Main_DataBase
                ).await()
            }
        } catch (e: Exception) {
            throw e
        }

        initializationProgress = 1.0f
        initializationComplete = true

    } catch (e: Exception) {
        throw e
    } finally {
        isInitializing = false
    }
}

private fun AppsHeadModel.ProduitModel.generateHistoricalSale(
    ventIndex: Int,
    dateFormat: SimpleDateFormat
): AppsHeadModel.ProduitModel.ClientBonVent_Model {
    return AppsHeadModel.ProduitModel.ClientBonVent_Model(
        vid = ventIndex.toLong(),
        id_Acheteur = ventIndex.toLong(),
        nom_Acheteur = "Client $ventIndex",
        time_String = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -(1..30).random())
        }.time.let { dateFormat.format(it) },
        inseartion_Temp = System.currentTimeMillis(),
        inceartion_Date = System.currentTimeMillis()
    ).apply {
        coloursEtGouts.take((1..3).random()).forEach { couleur ->
            colours_Achete.add(
                AppsHeadModel.ProduitModel.ClientBonVent_Model.Color_Achat_Model(
                    vidPosition = couleur.position_Du_Couleur_Au_Produit,
                    nom = couleur.nom,
                    quantity_Achete = (1..10).random(),
                    imogi = couleur.imogi
                )
            )
        }
    }
}

private fun AppsHeadModel.ProduitModel.generateCurrentSale(
    ventIndex: Int,
    dateFormat: SimpleDateFormat
): AppsHeadModel.ProduitModel.ClientBonVent_Model {
    return AppsHeadModel.ProduitModel.ClientBonVent_Model(
        vid = ventIndex.toLong(),
        id_Acheteur = ventIndex.toLong(),
        nom_Acheteur = "Client $ventIndex",
        time_String = dateFormat.format(Calendar.getInstance().time),
        inseartion_Temp = System.currentTimeMillis(),
        inceartion_Date = System.currentTimeMillis()
    ).apply {
        coloursEtGouts.take((1..3).random()).forEach { couleur ->
            colours_Achete.add(
                AppsHeadModel.ProduitModel.ClientBonVent_Model.Color_Achat_Model(
                    vidPosition = couleur.position_Du_Couleur_Au_Produit,
                    nom = couleur.nom,
                    quantity_Achete = (1..10).random(),
                    imogi = couleur.imogi
                )
            )
        }
    }
}

private fun AppsHeadModel.ProduitModel.generateGrossiste(): AppsHeadModel.ProduitModel.GrossistBonCommandes {
    val grossistes = listOf(
        Triple(1L, "Grossist Alpha", "#FF5733"),
        Triple(2L, "Grossist Beta", "#33FF57"),
        Triple(3L, "Grossist Gamma", "#5733FF")
    )
    val (grossisteId, grossisteNom, grossisteCouleur) = grossistes.random()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentDate = Calendar.getInstance().time
    val dateString = dateFormat.format(currentDate)

    val grossistInfo = AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations(
        id = grossisteId,
        nom = grossisteNom,
        couleur = grossisteCouleur
    )

    // Generate position with 40% chance of being 0, otherwise random between 1 and 10
    val randomPosition = if (Math.random() < 0.4) {
        0
    } else {
        (1..10).random()
    }

    Log.d("GenerateGrossiste", "Generated position $randomPosition for product with grossiste $grossisteId")

    return AppsHeadModel.ProduitModel.GrossistBonCommandes(
        vid = grossisteId,
        init_grossistInformations = grossistInfo,
        date = dateString,
        date_String_Divise = dateString.split(" ")[0],
        time_String_Divise = dateString.split(" ")[1],
        currentCreditBalance = (1000..2000).random().toDouble(),
        init_position_Grossist_Don_Parent_Grossists_List = grossisteId.toInt() - 1,
        init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = randomPosition
    ).apply {
        coloursEtGouts.firstOrNull()?.let { couleur ->
            coloursEtGoutsCommendee.add(
                AppsHeadModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                    id= couleur.position_Du_Couleur_Au_Produit,
                    nom= couleur.nom,
                    couleur= couleur.nom,
                    init_quantityAchete = (10..50).random()
                )
            )
        }
    }
}
