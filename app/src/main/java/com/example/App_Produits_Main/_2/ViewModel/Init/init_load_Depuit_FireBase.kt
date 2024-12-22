package com.example.App_Produits_Main._2.ViewModel.Init

import com.example.App_Produits_Main._1.Model.App_Initialize_Model
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

suspend fun Apps_Produits_Main_DataBase_ViewModel.init_load_Depuit_FireBase() {
    try {
        val ref_Produits_Main_DataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("produit_DataBase")

        val produitsSnapshot = ref_Produits_Main_DataBase
            .get()
            .await()

        val produitsList = produitsSnapshot.children.mapNotNull {
            it.getValue(App_Initialize_Model.Produit_Main_DataBase::class.java)
        }

        produitsList.forEach { produit ->
            _app_Initialize_Model.produits_Main_DataBase.add(
                App_Initialize_Model.Produit_Main_DataBase(
                    id = produit.id,
                    it_ref_Id_don_FireBase = produit.it_ref_Id_don_FireBase,
                    it_ref_don_FireBase = produit.it_ref_don_FireBase,
                    init_nom = produit.nom,
                    init_besoin_To_Be_Updated = produit.besoin_To_Be_Updated,
                    init_it_Image_besoin_To_Be_Updated = produit.it_Image_besoin_To_Be_Updated,
                    initialNon_Trouve = produit.non_Trouve,
                    init_mutable_App_Produit_Statues = App_Initialize_Model.Produit_Main_DataBase.Mutable_App_Produit_Statues(
                        init_dernier_Vent_date_time_String = produit.mutable_App_Produit_Statues.dernier_Vent_date_time_String,
                        init_its_Filtre_Au_Grossists_Buttons = produit.mutable_App_Produit_Statues.its_Filtre_Au_Grossists_Buttons,
                        init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction = produit.mutable_App_Produit_Statues.son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction
                    ),
                    init_Grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota = produit.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota,
                    init_colours_Et_Gouts = produit.colours_Et_Gouts.map { color ->
                        App_Initialize_Model.Produit_Main_DataBase.Colours_Et_Gouts(
                            position_Du_Couleur_Au_Produit = color.position_Du_Couleur_Au_Produit,
                            nom = color.nom,
                            imogi = color.imogi
                        )
                    },
                    initialDemmende_Achate_De_Cette_Produit = produit.demmende_Achate_De_Cette_Produit.map { demande ->
                        App_Initialize_Model.Produit_Main_DataBase.Demmende_Achate_De_Cette_Produit(
                            vid = demande.vid,
                            id_Acheteur = demande.id_Acheteur,
                            nom_Acheteur = demande.nom_Acheteur,
                            time_String = demande.time_String,
                            inseartion_Temp = demande.inseartion_Temp,
                            inceartion_Date = demande.inceartion_Date,
                            initial_Colours_Et_Gouts_Acheter_Depuit_Client = demande.colours_Et_Gouts_Acheter_Depuit_Client.map { couleur ->
                                App_Initialize_Model.Produit_Main_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client(
                                    vidPosition = couleur.vidPosition,
                                    nom = couleur.nom,
                                    quantity_Achete = couleur.quantity_Achete,
                                    imogi = couleur.imogi
                                )
                            }
                        )
                    },
                    initialGrossist_Choisi_Pour_Acheter_CeProduit = produit.grossist_Choisi_Pour_Acheter_CeProduit.map { grossist ->
                        App_Initialize_Model.Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
                            vid = grossist.vid,
                            supplier_id = grossist.supplier_id,
                            nom = grossist.nom,
                            date = grossist.date,
                            date_String_Divise = grossist.date_String_Divise,
                            time_String_Divise = grossist.time_String_Divise,
                            couleur = grossist.couleur,
                            currentCreditBalance = grossist.currentCreditBalance,
                            init_position_Grossist_Don_Parent_Grossists_List = grossist.position_Grossist_Don_Parent_Grossists_List,
                            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = grossist.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit,
                            initialColours_Et_Gouts_Commende_Au_Supplier = grossist.colours_Et_Gouts_Commende.map { commande ->
                                App_Initialize_Model.Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction.Colours_Et_Gouts_Commende_Au_Supplier(
                                    position_Du_Couleur_Au_Produit = commande.position_Du_Couleur_Au_Produit,
                                    id_Don_Tout_Couleurs = commande.id_Don_Tout_Couleurs,
                                    nom = commande.nom,
                                    quantity_Achete = commande.quantity_Achete,
                                    imogi = commande.imogi
                                )
                            }
                        )
                    }
                )
            )
        }
    } catch (e: Exception) {
        throw Exception("Failed to load products from Firebase: ${e.message}")
    }
}
