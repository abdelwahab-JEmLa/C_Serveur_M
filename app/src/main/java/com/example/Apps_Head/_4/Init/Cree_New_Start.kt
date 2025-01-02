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
        initializationProgress = 0.1f
        isInitializing = true

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        val ancienData = get_Ancien_DataBases_Main()
        val grossistes = listOf(
            Triple(1L, "Grossist Alpha", "#FF5733"),
            Triple(2L, "Grossist Beta", "#33FF57"),
            Triple(3L, "Grossist Gamma", "#5733FF")
        )

        ancienData.produitsDatabase.forEachIndexed { index, ancien ->
            // Créer nouveau produit
            val nouveauProduit = AppsHeadModel.ProduitModel(
                id = ancien.idArticle,
                init_nom = ancien.nomArticleFinale,
                init_besoin_To_Be_Updated = true
            )

            // Ajouter les couleurs
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

            // Générer l'historique des ventes
            repeat((1..5).random()) { ventIndex ->
                val saleDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -(1..30).random())
                }.time

                val bonVent = AppsHeadModel.ProduitModel.ClientBonVent_Model(
                    vid = ventIndex.toLong(),
                    id_Acheteur = ventIndex.toLong(),
                    nom_Acheteur = "Client $ventIndex",
                    time_String = dateFormat.format(saleDate),
                    inseartion_Temp = currentTime,
                    inceartion_Date = currentTime
                ).apply {
                    nouveauProduit.coloursEtGouts.take((1..3).random()).forEach { couleur ->
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
                nouveauProduit.historiqueBonsVents.add(bonVent)
            }

            // Générer les ventes actuelles
            repeat((1..3).random()) { ventIndex ->
                val bonVent = AppsHeadModel.ProduitModel.ClientBonVent_Model(
                    vid = ventIndex.toLong(),
                    id_Acheteur = ventIndex.toLong(),
                    nom_Acheteur = "Client $ventIndex",
                    time_String = dateFormat.format(Calendar.getInstance().time),
                    inseartion_Temp = currentTime,
                    inceartion_Date = currentTime
                ).apply {
                    nouveauProduit.coloursEtGouts.take((1..3).random()).forEach { couleur ->
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
                nouveauProduit.bonsVentDeCetteCota.add(bonVent)
            }

            // Générer données grossiste pour les 30 premiers produits
            if (index < 30) {
                val (id, nom, couleur) = grossistes.random()
                val currentDate = dateFormat.format(Calendar.getInstance().time)

                val grossiste = AppsHeadModel.ProduitModel.GrossistBonCommandes(
                    vid = id,
                    init_grossistInformations = AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations(
                        id = id,
                        nom = nom,
                        couleur = couleur
                    ),
                    date = currentDate,
                    date_String_Divise = currentDate.split(" ")[0],
                    time_String_Divise = currentDate.split(" ")[1],
                    currentCreditBalance = (1000..2000).random().toDouble(),
                    init_position_Grossist_Don_Parent_Grossists_List = id.toInt() - 1,
                    init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                    if (Math.random() < 0.4) 0 else (1..10).random()
                ).apply {
                    nouveauProduit.coloursEtGouts.firstOrNull()?.let { couleur ->
                        coloursEtGoutsCommendee.add(
                            AppsHeadModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                                id = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                couleur = couleur.nom,
                                init_quantityAchete = (10..50).random()
                            )
                        )
                    }
                }
                nouveauProduit.bonCommendDeCetteCota = grossiste
                nouveauProduit.historiqueBonsCommend.add(grossiste)
            }

            _appsHeadModel.produitsMainDataBase.add(nouveauProduit)
            initializationProgress = 0.1f + (0.8f * (index + 1) / ancienData.produitsDatabase.size)
        }

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
