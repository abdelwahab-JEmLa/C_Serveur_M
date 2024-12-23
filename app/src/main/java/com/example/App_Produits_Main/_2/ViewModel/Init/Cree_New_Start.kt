package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.example.App_Produits_Main._2.ViewModel.Init.Z.Components.get_Ancien_DataBases_Main
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

suspend fun Apps_Produits_Main_DataBase_ViewModel.cree_New_Start() {
    val TAG = "Cree_New_Start"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    try {
        // Démarrage de l'initialisation
        Log.d(TAG, "Démarrage de l'initialisation")
            initializationProgress = 0.1f
            isInitializing = true

        // Récupération des anciennes données
        val ancienData = get_Ancien_DataBases_Main()

        // Traitement de chaque produit
        ancienData.produitsDatabase.forEach { ancien ->
            // Création du produit de base
            val nouveauProduit = AppInitializeModel.ProduitModel(
                id = ancien.idArticle,
                it_ref_Id_don_FireBase = 1L,
                it_ref_don_FireBase = "produit_DataBase"
            )

            // Ajout des détails au produit
            nouveauProduit.apply {
                nom = ancien.nomArticleFinale
             
                // Ajout des couleurs
                val couleursIds = listOf(
                    ancien.idcolor1 to 1L,
                    ancien.idcolor2 to 2L,
                    ancien.idcolor3 to 3L,
                    ancien.idcolor4 to 4L
                )

                couleursIds.forEach { (colorId, position) ->
                    ancienData.couleurs_List
                        .find { it.idColore == colorId }
                        ?.let { couleur ->
                            coloursEtGouts.add(
                                AppInitializeModel.ProduitModel.ColourEtGout_Model(
                                    position_Du_Couleur_Au_Produit = position,
                                    nom = couleur.nameColore,
                                    imogi = couleur.iconColore
                                )
                            )
                        }
                }

                // Ajout des données de vente
                historiqueBonsVents.clear()
                bonsVentDeCetteCota.clear()

                // Ventes historiques
                repeat((1..5).random()) { ventIndex ->
                    val vente = AppInitializeModel.ProduitModel.ClientBonVent_Model(
                        vid = ventIndex.toLong(),
                        id_Acheteur = ventIndex.toLong(),
                        nom_Acheteur = "Client $ventIndex",
                        time_String = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_MONTH, -(1..30).random())
                        }.time.let { dateFormat.format(it) },
                        inseartion_Temp = System.currentTimeMillis(),
                        inceartion_Date = System.currentTimeMillis()
                    )

                    // Ajout des couleurs à la vente historique
                    coloursEtGouts.take((1..3).random()).forEach { couleur ->
                        vente.colours_Achete.add(
                            AppInitializeModel.ProduitModel.ClientBonVent_Model.Color_Achat_Model(
                                vidPosition = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                quantity_Achete = (1..10).random(),
                                imogi = couleur.imogi
                            )
                        )
                    }

                    historiqueBonsVents.add(vente)
                }

                // Ventes actuelles
                repeat((1..3).random()) { ventIndex ->
                    val vente = AppInitializeModel.ProduitModel.ClientBonVent_Model(
                        vid = ventIndex.toLong(),
                        id_Acheteur = ventIndex.toLong(),
                        nom_Acheteur = "Client $ventIndex",
                        time_String = dateFormat.format(Calendar.getInstance().time),
                        inseartion_Temp = System.currentTimeMillis(),
                        inceartion_Date = System.currentTimeMillis()
                    )

                    // Ajout des couleurs à la vente actuelle
                    coloursEtGouts.take((1..3).random()).forEach { couleur ->
                        vente.colours_Achete.add(
                            AppInitializeModel.ProduitModel.ClientBonVent_Model.Color_Achat_Model(
                                vidPosition = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                quantity_Achete = (1..10).random(),
                                imogi = couleur.imogi
                            )
                        )
                    }

                    bonsVentDeCetteCota.add(vente)
                }

                // Ajout des données grossiste
                val grossistes = listOf(
                    Triple(1L, "Grossist Alpha", "#FF5733"),
                    Triple(2L, "Grossist Beta", "#33FF57"),
                    Triple(3L, "Grossist Gamma", "#5733FF")
                )
                val (grossisteId, grossisteNom, grossisteCouleur) = grossistes.random()

                val grossiste = AppInitializeModel.ProduitModel.GrossistBonCommandes(
                    vid = grossisteId,
                    supplier_id = grossisteId,
                    nom = grossisteNom,
                    init_position_Grossist_Don_Parent_Grossists_List = grossisteId.toInt() - 1,
                    couleur = grossisteCouleur,
                    currentCreditBalance = (1000..2000).random().toDouble(),
                    date = System.currentTimeMillis().toString()
                )

                coloursEtGouts.firstOrNull()?.let { couleur ->
                    grossiste.coloursEtGoutsCommendee.add(
                        AppInitializeModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                            init_coloursEtGouts = listOf(
                                AppInitializeModel.ProduitModel.ColourEtGout_Model(
                                    position_Du_Couleur_Au_Produit = couleur.position_Du_Couleur_Au_Produit,
                                    nom = couleur.nom,
                                    imogi = couleur.imogi
                                )
                            ),
                            init_quantityAchete = (1..10).random() // Random quantity between 1 and 10
                        )
                    )
                }

                historiqueBonsCommend.clear()
                bonCommendDeCetteCota = grossiste
                historiqueBonsCommend.add(grossiste)

                besoin_To_Be_Updated = false
            }

            _app_Initialize_Model.produits_Main_DataBase.add(nouveauProduit)
        }

        // Mise à jour Firebase
        _app_Initialize_Model.update_Produits_FireBase()

        // Finalisation
        apply {
            initializationProgress = 1.0f
            initializationComplete = true
        }
        Log.d(TAG, "Initialisation terminée")

    } catch (e: Exception) {
        Log.e(TAG, "Erreur d'initialisation", e)
        throw e
    } finally {
        isInitializing = false
    }
}
