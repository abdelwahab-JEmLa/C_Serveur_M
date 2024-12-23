package com.example.App_Produits_Main._1.Model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AppInitializeModel(
    initial_Produits_Main_DataBase: List<Produit_Model> = emptyList()
) {
    var produits_Main_DataBase: SnapshotStateList<Produit_Model> =
        initial_Produits_Main_DataBase.toMutableStateList()

    val ref_Produits_Main_DataBase = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produit_DataBase")

    class Produit_Model(
        var id: Long = 0,
        val it_ref_Id_don_FireBase: Long = 0,
        val it_ref_don_FireBase: String = "",
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        init_it_Image_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_colours_Et_Gouts: List<Colours_Et_Gouts> = emptyList(),
        init_bon_Commend_De_Cette_Cota: GrossistBonCommandesModel? = null,
        init_bonS_Vent_De_Cette_Cota: List<Client_Bon_Vent_Model> = emptyList(),

        init_historique_BonS_Vent: List<Client_Bon_Vent_Model> = emptyList(),
        init_historique_BonS_Commend: List<GrossistBonCommandesModel> = emptyList(),
        init_mutable_App_Produit_Statues: Mutable_App_Produit_Statues = Mutable_App_Produit_Statues(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var it_Image_besoin_To_Be_Updated: Boolean by mutableStateOf(init_it_Image_besoin_To_Be_Updated)
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var colours_Et_Gouts: SnapshotStateList<Colours_Et_Gouts> =
            init_colours_Et_Gouts.toMutableStateList()

        var bon_Commend_De_Cette_Cota: GrossistBonCommandesModel?
        by mutableStateOf(init_bon_Commend_De_Cette_Cota)
        var bonS_Vent_De_Cette_Cota: SnapshotStateList<Client_Bon_Vent_Model> =
            init_bonS_Vent_De_Cette_Cota.toMutableStateList()

        var historique_bonS_Vents: SnapshotStateList<Client_Bon_Vent_Model> =
            init_historique_BonS_Vent.toMutableStateList()
        var historique_BonS_Commend: SnapshotStateList<GrossistBonCommandesModel> =
            init_historique_BonS_Commend.toMutableStateList()

        var mutable_App_Produit_Statues: Mutable_App_Produit_Statues by mutableStateOf(init_mutable_App_Produit_Statues)

        class Mutable_App_Produit_Statues(
            var init_dernier_Vent_date_time_String: String = "", //"yyyy-MM-dd HH:mm:ss"
            var init_its_Filtre_Au_Grossists_Buttons: Boolean = false,
            var init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction: GrossistBonCommandesModel? = null
            ){
            var dernier_Vent_date_time_String: String by mutableStateOf(init_dernier_Vent_date_time_String)
            var its_Filtre_Au_Grossists_Buttons: Boolean by mutableStateOf(init_its_Filtre_Au_Grossists_Buttons)
            var son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction: GrossistBonCommandesModel? by mutableStateOf(init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction)
        }

        class Colours_Et_Gouts(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var nom: String = "",
            var imogi: String = ""
        )

        class GrossistBonCommandesModel(
            var vid: Long = 0,
            var supplier_id: Long = 0,
            var nom: String = "",
            var date: String = "", //"yyyy-MM-dd HH:mm:ss"
            var date_String_Divise: String = "", //"yyyy-MM-dd"
            var time_String_Divise: String = "", //"HH:mm:ss"
            var couleur: String = "#FFFFFF",
            var currentCreditBalance: Double = 0.0,
            init_position_Grossist_Don_Parent_Grossists_List: Int = 0,
            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int = 0,
            initialColours_Et_Gouts_Commende_Au_Supplier: List<Colours_Et_Gouts_Commende_Au_Supplier> = emptyList(),
        ) {
            var position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int by mutableStateOf(
                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            )
            var position_Grossist_Don_Parent_Grossists_List: Int by mutableStateOf(
                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            )
            var colours_Et_Gouts_Commende: SnapshotStateList<Colours_Et_Gouts_Commende_Au_Supplier> =
                initialColours_Et_Gouts_Commende_Au_Supplier.toMutableStateList()

            class Colours_Et_Gouts_Commende_Au_Supplier(
                var position_Du_Couleur_Au_Produit: Long = 0,
                var id_Don_Tout_Couleurs: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            )
        }

        class Client_Bon_Vent_Model(
            var vid: Long = 0,
            var id_Acheteur: Long = 0,
            var nom_Acheteur: String = "",
            var time_String: String = "", //"yyyy-MM-dd HH:mm:ss"
            var inseartion_Temp: Long = 0,
            var inceartion_Date: Long = 0,
            init_colours_achete: List<Color_Achat_Model> = emptyList(),
        ) {
            var colours_Achete: SnapshotStateList<Color_Achat_Model> =
                init_colours_achete.toMutableStateList()

            class Color_Achat_Model(
                var vidPosition: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            )
        }
    }

    suspend fun update_Produits_FireBase() {
        try {
            val ref_Produit_Main_DataBase = Firebase.database
                .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
                .child("produit_DataBase")

            // Use supervisorScope to prevent cancellation from propagating
            withContext(Dispatchers.IO) {
                ref_Produit_Main_DataBase.setValue(produits_Main_DataBase).await()
            }
        } catch (e: Exception) {
            Log.e("AppInitializeModel", "Failed to update group in Firebase", e)
            throw Exception("Failed to update group in Firebase: ${e.message}")
        }
    }
}
