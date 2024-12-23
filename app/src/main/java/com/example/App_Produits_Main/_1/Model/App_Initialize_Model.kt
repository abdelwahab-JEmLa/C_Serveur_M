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

class App_Initialize_Model(
    initial_Produits_Main_DataBase: List<Produit_Main_DataBase> = emptyList()
) {
    var produits_Main_DataBase: SnapshotStateList<Produit_Main_DataBase> =
        initial_Produits_Main_DataBase.toMutableStateList()

    val ref_Produits_Main_DataBase = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produit_DataBase")

    class Produit_Main_DataBase(
        var id: Long = 0,
        val it_ref_Id_don_FireBase: Long = 0,
        val it_ref_don_FireBase: String = "",
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        init_it_Image_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_mutable_App_Produit_Statues: Mutable_App_Produit_Statues = Mutable_App_Produit_Statues(),
        init_Grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota: Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction? = null,
        init_acheteurs_pour_Cette_Cota: List<Acheteurs_pour_Cette_Cota> = emptyList(),

        init_colours_Et_Gouts: List<Colours_Et_Gouts> = emptyList(),
        initialDemmende_Achate_De_Cette_Produit: List<Acheteurs_pour_Cette_Cota> = emptyList(),
        initialGrossist_Choisi_Pour_Acheter_CeProduit: List<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var it_Image_besoin_To_Be_Updated: Boolean by mutableStateOf(init_it_Image_besoin_To_Be_Updated)     
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var mutable_App_Produit_Statues: Mutable_App_Produit_Statues by mutableStateOf(init_mutable_App_Produit_Statues)
        var grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota: Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction? by mutableStateOf(init_Grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota)
        var acheteurs_pour_Cette_Cota: SnapshotStateList<Acheteurs_pour_Cette_Cota> =
            init_acheteurs_pour_Cette_Cota.toMutableStateList()

        var colours_Et_Gouts: SnapshotStateList<Colours_Et_Gouts> =
            init_colours_Et_Gouts.toMutableStateList()
        var demmende_Achate_De_Cette_Produit: SnapshotStateList<Acheteurs_pour_Cette_Cota> =
            initialDemmende_Achate_De_Cette_Produit.toMutableStateList()
        var grossist_Choisi_Pour_Acheter_CeProduit: SnapshotStateList<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> =
            initialGrossist_Choisi_Pour_Acheter_CeProduit.toMutableStateList()

        class Mutable_App_Produit_Statues(
            var init_dernier_Vent_date_time_String: String = "", //"yyyy-MM-dd HH:mm:ss"
            var init_its_Filtre_Au_Grossists_Buttons: Boolean = false,
            var init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction: Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction? = null
            ){
            var dernier_Vent_date_time_String: String by mutableStateOf(init_dernier_Vent_date_time_String)
            var its_Filtre_Au_Grossists_Buttons: Boolean by mutableStateOf(init_its_Filtre_Au_Grossists_Buttons)
            var son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction: Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction? by mutableStateOf(init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction)
        }

        class Colours_Et_Gouts(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var nom: String = "",
            var imogi: String = ""
        )

        class Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
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

        class Acheteurs_pour_Cette_Cota(
            var vid: Long = 0,
            var id_Acheteur: Long = 0,
            var nom_Acheteur: String = "",
            var time_String: String = "", //"yyyy-MM-dd HH:mm:ss"
            var inseartion_Temp: Long = 0,
            var inceartion_Date: Long = 0,
            initial_Colours_Et_Gouts_Acheter_Depuit_Client: List<Colours_Et_Gouts_Acheter_Depuit_Client> = emptyList(),
        ) {
            var colours_Et_Gouts_Acheter_Depuit_Client: SnapshotStateList<Colours_Et_Gouts_Acheter_Depuit_Client> =
                initial_Colours_Et_Gouts_Acheter_Depuit_Client.toMutableStateList()

            class Colours_Et_Gouts_Acheter_Depuit_Client(
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
            Log.e("App_Initialize_Model", "Failed to update group in Firebase", e)
            throw Exception("Failed to update group in Firebase: ${e.message}")
        }
    }
}
