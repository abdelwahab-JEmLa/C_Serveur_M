package com.example.App_Produits_Main._1.Model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await

class App_Initialize_Model(
    initial_Produit_Main_DataBase: List<Produit_Main_DataBase> = emptyList()
) {
    var produit_Main_DataBase: SnapshotStateList<Produit_Main_DataBase> =
        initial_Produit_Main_DataBase.toMutableStateList()

    val ref_Produit_Main_DataBase = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produit_DataBase")

    class Produit_Main_DataBase(
        val id: Long = 0,
        val it_ref_Id_don_FireBase: Long = 0,
        val it_ref_don_FireBase: String = "",
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        init_it_Image_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_colours_Et_Gouts: List<Colours_Et_Gouts> = emptyList(),
        initialDemmende_Achate_De_Cette_Produit: List<Demmende_Achate_De_Cette_Produit> = emptyList(),
        initialGrossist_Choisi_Pour_Acheter_CeProduit: List<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var it_Image_besoin_To_Be_Updated: Boolean by mutableStateOf(init_it_Image_besoin_To_Be_Updated)     
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var colours_Et_Gouts: SnapshotStateList<Colours_Et_Gouts> =
            init_colours_Et_Gouts.toMutableStateList()
        var demmende_Achate_De_Cette_Produit: SnapshotStateList<Demmende_Achate_De_Cette_Produit> =
            initialDemmende_Achate_De_Cette_Produit.toMutableStateList()
        var grossist_Choisi_Pour_Acheter_CeProduit: SnapshotStateList<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> =
            initialGrossist_Choisi_Pour_Acheter_CeProduit.toMutableStateList()

        class Colours_Et_Gouts(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var nom: String = "",
            var imogi: String = ""
        )

        class mutable_App_Statues(
            var date_String_Divise: String = "", //"yyyy-MM-dd"
            var time_String_Divise: String = "", //"HH:mm:ss"
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

        class Demmende_Achate_De_Cette_Produit(
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

            ref_Produit_Main_DataBase.setValue(produit_Main_DataBase).await()
        } catch (e: Exception) {
            throw Exception("Failed to update group in Firebase: ${e.message}")
        }
    }

    suspend fun load_Produits_FireBase() {
        try {

            val snapshot = ref_Produit_Main_DataBase.get().await()
            val rawData = snapshot.getValue<List<Map<String, Any?>>>()

            if (rawData != null) {
                Log.d("App_Initialize_Model", "Raw data loaded successfully with ${rawData.size} entries")

                val convertedProduits = rawData.map { productMap ->
                    // Map colors and tastes
                    val coloursEtGouts = (productMap["colours_Et_Gouts"] as? List<Map<String, Any?>>)?.map { color ->
                        Produit_Main_DataBase.Colours_Et_Gouts(
                            position_Du_Couleur_Au_Produit = (color["position_Du_Couleur_Au_Produit"] as? Long)
                                ?: 0,
                            nom = (color["nom"] as? String) ?: "",
                            imogi = (color["imogi"] as? String) ?: ""
                        )
                    } ?: emptyList()

                    // Map purchase demands
                    val demandes = (productMap["demmende_Achate_De_Cette_Produit"] as? List<Map<String, Any?>>)?.map { demand ->
                        val clientColors = (demand["colours_Et_Gouts_Acheter_Depuit_Client"] as? List<Map<String, Any?>>)?.map { clientColor ->
                            Produit_Main_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client(
                                vidPosition = (clientColor["vidPosition"] as? Long) ?: 0,
                                nom = (clientColor["nom"] as? String) ?: "",
                                quantity_Achete = (clientColor["quantity_Achete"] as? Long)?.toInt()
                                    ?: 0,
                                imogi = (clientColor["imogi"] as? String) ?: ""
                            )
                        } ?: emptyList()

                        Produit_Main_DataBase.Demmende_Achate_De_Cette_Produit(
                            vid = (demand["vid"] as? Long) ?: 0,
                            id_Acheteur = (demand["id_Acheteur"] as? Long) ?: 0,
                            nom_Acheteur = (demand["nom_Acheteur"] as? String) ?: "",
                            time_String = (demand["time_String"] as? String) ?: "",
                            inseartion_Temp = (demand["inseartion_Temp"] as? Long) ?: 0,
                            inceartion_Date = (demand["inceartion_Date"] as? Long) ?: 0,
                            initial_Colours_Et_Gouts_Acheter_Depuit_Client = clientColors
                        )
                    } ?: emptyList()

                    // Map supplier choices
                    val suppliers = (productMap["grossist_Choisi_Pour_Acheter_CeProduit"] as? List<Map<String, Any?>>)?.map { supplier ->
                        val supplierColors = (supplier["colours_Et_Gouts_Commende"] as? List<Map<String, Any?>>)?.map { colorMap ->
                            Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction.Colours_Et_Gouts_Commende_Au_Supplier(
                                position_Du_Couleur_Au_Produit = (colorMap["position_Du_Couleur_Au_Produit"] as? Long)
                                    ?: 0,
                                id_Don_Tout_Couleurs = (colorMap["id_Don_Tout_Couleurs"] as? Long)
                                    ?: 0,
                                nom = (colorMap["nom"] as? String) ?: "",
                                quantity_Achete = (colorMap["quantity_Achete"] as? Long)?.toInt()
                                    ?: 0,
                                imogi = (colorMap["imogi"] as? String) ?: ""
                            )
                        } ?: emptyList()

                        Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
                            vid = (supplier["vid"] as? Long) ?: 0,
                            supplier_id = (supplier["supplier_id"] as? Long) ?: 0,
                            nom = (supplier["nom"] as? String) ?: "",
                            date = (supplier["date"] as? String) ?: "",
                            couleur = (supplier["couleur"] as? String) ?: "#FFFFFF",
                            currentCreditBalance = (supplier["currentCreditBalance"] as? Double)
                                ?: 0.0,
                            init_position_Grossist_Don_Parent_Grossists_List = (supplier["position_Grossist_Don_Parent_Grossists_List"] as? Long)?.toInt()
                                ?: 0,
                            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = (supplier["position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit"] as? Long)?.toInt()
                                ?: 0,
                            initialColours_Et_Gouts_Commende_Au_Supplier = supplierColors
                        )
                    } ?: emptyList()

                    // Create the final product object
                    Produit_Main_DataBase(
                        id = (productMap["id"] as? Long) ?: 0,
                        it_ref_Id_don_FireBase = (productMap["it_ref_Id_don_FireBase"] as? Long) ?: 0,
                        it_ref_don_FireBase = (productMap["it_ref_don_FireBase"] as? String) ?: "",
                        init_nom = (productMap["nom"] as? String) ?: "",
                        init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
                        init_it_Image_besoin_To_Be_Updated = (productMap["it_Image_besoin_To_Be_Updated"] as? Boolean) ?: false,
                        initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                        init_colours_Et_Gouts = coloursEtGouts,
                        initialDemmende_Achate_De_Cette_Produit = demandes,
                        initialGrossist_Choisi_Pour_Acheter_CeProduit = suppliers
                    )
                }

                // Log statistics for debugging
                Log.d("App_Initialize_Model", "Converted ${convertedProduits.size} products")
                Log.d("App_Initialize_Model", "Products with suppliers: ${convertedProduits.count { it.grossist_Choisi_Pour_Acheter_CeProduit.isNotEmpty() }}")
                Log.d("App_Initialize_Model", "Products with demands: ${convertedProduits.count { it.demmende_Achate_De_Cette_Produit.isNotEmpty() }}")
                Log.d("App_Initialize_Model", "Products with colors: ${convertedProduits.count { it.colours_Et_Gouts.isNotEmpty() }}")

                // Update the state list
                produit_Main_DataBase.clear()
                produit_Main_DataBase.addAll(convertedProduits)

            } else {
                Log.w("App_Initialize_Model", "No data found in Firebase")
                produit_Main_DataBase.clear()
            }
        } catch (e: Exception) {
            Log.e("App_Initialize_Model", "Failed to load state from Firebase", e)
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }
}
