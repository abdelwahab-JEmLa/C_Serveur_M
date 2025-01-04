package com.example.Apps_Head._4.Init

import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Apps_Head._4.Init.Z.Components.get_Ancien_DataBases_Main
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

suspend fun InitViewModel.cree_New_Start() {
    try {
        val NOMBRE_ENTRE = 40

        initializationProgress = 0.1f
        isInitializing = true


        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        val ancienData = get_Ancien_DataBases_Main()

        // Predefined clients for consistent data
        val clients = listOf(
            Triple(1L, "Client Alpha", "#FF5733"),
            Triple(2L, "Client Beta", "#33FF57"),
            Triple(3L, "Client Gamma", "#5733FF"),
            Triple(4L, "Client Delta", "#FF33E6"),
            Triple(5L, "Client Epsilon", "#33FFF3")
        )

        // Predefined grossists
        val grossists = listOf(
            Triple(1L, "Grossist Alpha", "#FF5733"),
            Triple(2L, "Grossist Beta", "#33FF57"),
            Triple(3L, "Grossist Gamma", "#5733FF")
        )

        ancienData.produitsDatabase.take(NOMBRE_ENTRE).forEachIndexed { index, ancien ->
            // Create new product
            val nouveauProduit = AppsHeadModel.ProduitModel(
                id = ancien.idArticle,
                init_nom = ancien.nomArticleFinale,
                init_besoin_To_Be_Updated = true
            )

            // Add colors/tastes
            listOf(
                ancien.idcolor1 to 1L,
                ancien.idcolor2 to 2L,
                ancien.idcolor3 to 3L,
                ancien.idcolor4 to 4L
            ).forEach { (colorId, position) ->
                ancienData.couleurs_List.find { it.idColore == colorId }?.let { couleur ->
                    nouveauProduit.coloursEtGouts.add(
                        AppsHeadModel.ProduitModel.ColourEtGout_Model(
                            position_Du_Couleur_Au_Produit = position,
                            nom = couleur.nameColore,
                            imogi = couleur.iconColore
                        )
                    )
                }
            }

            // Generate sales history
            repeat((1..5).random()) { historyIndex ->
                val saleDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -(1..30).random())
                }.time

                val (clientId, clientName, clientColor) = clients.random()

                val bonVent = AppsHeadModel.ProduitModel.ClientBonVentModel(
                    init_clientInformations = AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = clientId,
                        nom = clientName,
                        couleur = clientColor
                    ),
                    init_colours_achete = nouveauProduit.coloursEtGouts.take((1..3).random()).map { couleur ->
                        AppsHeadModel.ProduitModel.ClientBonVentModel.ColorAchatModel(
                            vidPosition = couleur.position_Du_Couleur_Au_Produit,
                            nom = couleur.nom,
                            quantity_Achete = (1..10).random(),
                            imogi = couleur.imogi
                        )
                    }
                )
                nouveauProduit.historiqueBonsVents.add(bonVent)
            }

            // Generate current sales
            repeat((1..3).random()) { currentIndex ->
                val (clientId, clientName, clientColor) = clients.random()

                val bonVent = AppsHeadModel.ProduitModel.ClientBonVentModel(
                    init_clientInformations = AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = clientId,
                        nom = clientName,
                        couleur = clientColor
                    ),
                    init_colours_achete = nouveauProduit.coloursEtGouts.take((1..3).random()).map { couleur ->
                        AppsHeadModel.ProduitModel.ClientBonVentModel.ColorAchatModel(
                            vidPosition = couleur.position_Du_Couleur_Au_Produit,
                            nom = couleur.nom,
                            quantity_Achete = (1..10).random(),
                            imogi = couleur.imogi
                        )
                    }
                )
                nouveauProduit.bonsVentDeCetteCota.add(bonVent)
            }

            // Generate grossist data for first 30 products
            if (index < 30) {
                val (grossistId, grossistName, grossistColor) = grossists.random()
                val currentDate = dateFormat.format(Calendar.getInstance().time)

                val grossiste = AppsHeadModel.ProduitModel.GrossistBonCommandes(
                    vid = grossistId,
                    init_grossistInformations = AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations(
                        id = grossistId,
                        nom = grossistName,
                        couleur = grossistColor
                    ),
                    date = currentDate,
                    date_String_Divise = currentDate.split(" ")[0],
                    time_String_Divise = currentDate.split(" ")[1],
                    currentCreditBalance = (1000..2000).random().toDouble(),
                    init_position_Grossist_Don_Parent_Grossists_List = grossistId.toInt() - 1,
                    init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                    if (Math.random() < 0.4) 0 else (1..10).random(),
                    init_coloursEtGoutsCommendee = nouveauProduit.coloursEtGouts.take(1).map { couleur ->
                        AppsHeadModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                            id = couleur.position_Du_Couleur_Au_Produit,
                            nom = couleur.nom,
                            emoji = couleur.imogi,
                            init_quantityAchete = (10..50).random()
                        )
                    }
                )

                nouveauProduit.bonCommendDeCetteCota = grossiste
                nouveauProduit.historiqueBonsCommend.add(grossiste)
            }

            _appsHeadModel.produitsMainDataBase.add(nouveauProduit)
            initializationProgress = 0.1f + (0.8f * (index + 1) / ancienData.produitsDatabase.size)
        }

        // Clear and update Firebase database
        AppsHeadModel.ref_produitsDataBase.removeValue()
        _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()

        initializationProgress = 1.0f
        initializationComplete = true

    } catch (e: Exception) {
        throw e
    } finally {
        isInitializing = false
    }
}
