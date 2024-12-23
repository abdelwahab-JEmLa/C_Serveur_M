package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.App_Produits_Main._1.Model.Components.Produits_Ancien_DataBase_Main
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.example.App_Produits_Main._2.ViewModel.Init.Z.Components.Ancien_Resources_DataBase_Main
import com.example.App_Produits_Main._2.ViewModel.Init.Z.Components.get_Ancien_DataBases_Main
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Cree_New_Start(private val viewModel: Apps_Produits_Main_DataBase_ViewModel) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val TAG = "Cree_New_Start"

    // Point d entree principal
    internal suspend fun pointEntreePrincipal() {
        try {
            demarrerInitialisation()
            traiterDonnees()
            finaliserInitialisation()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur d'initialisation", e)
            throw e
        } finally {
            viewModel.isInitializing = false
        }
    }

    // Étape 1: Démarrage
    private fun demarrerInitialisation() {
        Log.d(TAG, "Démarrage de l'initialisation")
        viewModel.apply {
            initializationProgress = 0.1f
            isInitializing = true
        }
    }

    // Étape 2: Traitement des données
    private suspend fun traiterDonnees() {
        val ancienData = get_Ancien_DataBases_Main()

        // Créer et ajouter chaque produit
        ancienData.produitsDatabase.forEach { ancien ->
            val nouveauProduit = creeProduitBase(ancien.idArticle)
            ajouterDetailsProduit(nouveauProduit, ancien, ancienData)
            viewModel._app_Initialize_Model.produits_Main_DataBase.add(nouveauProduit)
        }

        // Mettre à jour Firebase
        viewModel._app_Initialize_Model.update_Produits_FireBase()
    }

    // Étape 3: Finalisation
    private fun finaliserInitialisation() {
        viewModel.apply {
            initializationProgress = 1.0f
            initializationComplete = true
        }
        Log.d(TAG, "Initialisation terminée")
    }

    // Création d'un produit de base
    private fun creeProduitBase(id: Long) = AppInitializeModel.Produit_Model(
        id = id,
        it_ref_Id_don_FireBase = 1L,
        it_ref_don_FireBase = "produit_DataBase"
    )

    // Ajout des détails au produit
    private fun ajouterDetailsProduit(
        produit: AppInitializeModel.Produit_Model,
        ancien: Produits_Ancien_DataBase_Main,
        ancienData: Ancien_Resources_DataBase_Main
    ) {
        produit.apply {
            // Informations de base
            nom = ancien.nomArticleFinale
            mutable_App_Produit_Statues.dernier_Vent_date_time_String = creerDateVente()

            // Couleurs
            ajouterCouleurs(this, ancien, ancienData)

            // Ventes et commandes
            ajouterDonneesVente(this)
            ajouterDonneesGrossiste(this)

            besoin_To_Be_Updated = false
        }
    }

    // Création de la date de vente
    private fun creerDateVente(): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 5)
            set(Calendar.SECOND, 0)
        }
        return dateFormat.format(calendar.time)
    }

    // Ajout des couleurs
    private fun ajouterCouleurs(
        produit: AppInitializeModel.Produit_Model,
        ancien: Produits_Ancien_DataBase_Main,
        ancienData: Ancien_Resources_DataBase_Main
    ) {
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
                    produit.colours_Et_Gouts.add(
                        AppInitializeModel.Produit_Model.Colours_Et_Gouts(
                            position_Du_Couleur_Au_Produit = position,
                            nom = couleur.nameColore,
                            imogi = couleur.iconColore
                        )
                    )
                }
        }
    }

    // Ajout des données de vente
    private fun ajouterDonneesVente(produit: AppInitializeModel.Produit_Model) {
        produit.apply {
            historique_bonS_Vents.clear()
            bonS_Vent_De_Cette_Cota.clear()

            // Ventes historiques
            repeat((1..5).random()) {
                historique_bonS_Vents.add(creerVente(it.toLong(), this))
            }

            // Ventes actuelles
            repeat((1..3).random()) {
                bonS_Vent_De_Cette_Cota.add(
                    creerVente(it.toLong(), this, true)
                )
            }
        }
    }

    // Création d une vente
    private fun creerVente(
        id: Long,
        produit: AppInitializeModel.Produit_Model,
        periodeActuelle: Boolean = false
    ) = AppInitializeModel.Produit_Model.Client_Bon_Vent_Model(
        vid = id,
        id_Acheteur = id,
        nom_Acheteur = "Client $id",
        time_String = creerDateVenteAleatoire(periodeActuelle),
        inseartion_Temp = System.currentTimeMillis(),
        inceartion_Date = System.currentTimeMillis()
    ).apply {
        ajouterCouleursVente(this, produit)
    }

    // Création d'une date de vente aléatoire
    private fun creerDateVenteAleatoire(periodeActuelle: Boolean): String {
        return Calendar.getInstance().apply {
            if (!periodeActuelle) {
                add(Calendar.DAY_OF_MONTH, -(1..30).random())
            }
        }.time.let { dateFormat.format(it) }
    }

    // Ajout des couleurs à une vente
    private fun ajouterCouleursVente(
        vente: AppInitializeModel.Produit_Model.Client_Bon_Vent_Model,
        produit: AppInitializeModel.Produit_Model
    ) {
        produit.colours_Et_Gouts
            .take((1..3).random())
            .forEach { couleur ->
                vente.colours_Achete.add(
                    AppInitializeModel.Produit_Model.Client_Bon_Vent_Model.Color_Achat_Model(
                        vidPosition = couleur.position_Du_Couleur_Au_Produit,
                        nom = couleur.nom,
                        quantity_Achete = (1..10).random(),
                        imogi = couleur.imogi
                    )
                )
            }
    }

    // Ajout des données grossiste
    private fun ajouterDonneesGrossiste(produit: AppInitializeModel.Produit_Model) {
        val grossiste = creerGrossiste()
        ajouterCouleursGrossiste(grossiste, produit)

        produit.apply {
            historique_BonS_Commend.clear()
            bon_Commend_De_Cette_Cota = grossiste
            historique_BonS_Commend.add(grossiste)
        }
    }

    // Création d'un grossiste
    private fun creerGrossiste(): AppInitializeModel.Produit_Model.GrossistBonCommandesModel {
        val grossistes = listOf(
            Triple(1L, "Grossist Alpha", "#FF5733"),
            Triple(2L, "Grossist Beta", "#33FF57"),
            Triple(3L, "Grossist Gamma", "#5733FF")
        )
        val (id, nom, couleur) = grossistes.random()

        return AppInitializeModel.Produit_Model.GrossistBonCommandesModel(
            vid = id,
            supplier_id = id,
            nom = nom,
            init_position_Grossist_Don_Parent_Grossists_List = id.toInt() - 1,
            couleur = couleur,
            currentCreditBalance = (1000..2000).random().toDouble(),
            date = System.currentTimeMillis().toString()
        )
    }

    // Ajout des couleurs au grossiste
    private fun ajouterCouleursGrossiste(
        grossiste: AppInitializeModel.Produit_Model.GrossistBonCommandesModel,
        produit: AppInitializeModel.Produit_Model
    ) {
        produit.colours_Et_Gouts.firstOrNull()?.let { couleur ->
            grossiste.colours_Et_Gouts_Commende.add(
                AppInitializeModel.Produit_Model.GrossistBonCommandesModel.Colours_Et_Gouts_Commende_Au_Supplier(
                    position_Du_Couleur_Au_Produit = couleur.position_Du_Couleur_Au_Produit,
                    id_Don_Tout_Couleurs = couleur.position_Du_Couleur_Au_Produit,
                    nom = couleur.nom,
                    quantity_Achete = 1,
                    imogi = couleur.imogi
                )
            )
        }
    }
}
