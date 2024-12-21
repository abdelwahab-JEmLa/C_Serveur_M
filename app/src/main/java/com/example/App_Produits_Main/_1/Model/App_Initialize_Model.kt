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
        val id: Long = 0,
        val it_ref_Id_don_FireBase: Long = 0,
        val it_ref_don_FireBase: String = "",
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        init_it_Image_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_mutable_App_Produit_Statues: Mutable_App_Produit_Statues = Mutable_App_Produit_Statues(),


        init_colours_Et_Gouts: List<Colours_Et_Gouts> = emptyList(),
        initialDemmende_Achate_De_Cette_Produit: List<Demmende_Achate_De_Cette_Produit> = emptyList(),
        initialGrossist_Choisi_Pour_Acheter_CeProduit: List<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var it_Image_besoin_To_Be_Updated: Boolean by mutableStateOf(init_it_Image_besoin_To_Be_Updated)     
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var mutable_App_Produit_Statues: Mutable_App_Produit_Statues by mutableStateOf(init_mutable_App_Produit_Statues)
        
        var colours_Et_Gouts: SnapshotStateList<Colours_Et_Gouts> =
            init_colours_Et_Gouts.toMutableStateList()
        var demmende_Achate_De_Cette_Produit: SnapshotStateList<Demmende_Achate_De_Cette_Produit> =
            initialDemmende_Achate_De_Cette_Produit.toMutableStateList()
        var grossist_Choisi_Pour_Acheter_CeProduit: SnapshotStateList<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> =
            initialGrossist_Choisi_Pour_Acheter_CeProduit.toMutableStateList()

        class Mutable_App_Produit_Statues(
            var init_dernier_Vent_date_time_String: String = "", //"yyyy-MM-dd HH:mm:ss"
            var init_its_Filtre_Au_Grossists_Buttons: Boolean = false,
            var init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction: Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction = Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(),//change a null
            ){
            var dernier_Vent_date_time_String: String by mutableStateOf(init_dernier_Vent_date_time_String)
            var its_Filtre_Au_Grossists_Buttons: Boolean by mutableStateOf(init_its_Filtre_Au_Grossists_Buttons)
            var son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction: Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction by mutableStateOf(init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction)
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

            // Use supervisorScope to prevent cancellation from propagating
            withContext(Dispatchers.IO) {
                ref_Produit_Main_DataBase.setValue(produits_Main_DataBase).await()
            }
        } catch (e: Exception) {
            Log.e("App_Initialize_Model", "Failed to update group in Firebase", e)
            throw Exception("Failed to update group in Firebase: ${e.message}")
        }
    }

    suspend fun load_Produits_FireBase() {
        try {
            val snapshot = ref_Produits_Main_DataBase.get().await()

            // Clear existing data first
            produits_Main_DataBase.clear()

            // Handle null or empty snapshot
            if (!snapshot.exists()) {
                Log.w("App_Initialize_Model", "No data found in Firebase")
                return
            }

            // Try to get data as a List first
            val dataList = snapshot.getValue<List<Map<String, Any?>>>()

            // If list parsing fails, try as a Map (in case data is stored with keys)
            val rawData = when {
                dataList != null -> dataList
                snapshot.value is Map<*, *> -> {
                    // Convert map of products to list
                    (snapshot.value as Map<*, *>).values.mapNotNull { it as? Map<String, Any?> }
                }
                else -> {
                    Log.w("App_Initialize_Model", "Unexpected data format in Firebase")
                    return
                }
            }

            Log.d("App_Initialize_Model", "Raw data loaded successfully with ${rawData.size} entries")

            val convertedProduits = rawData.mapNotNull { productMap ->
                try {
                    // Safely extract colors and tastes
                    val coloursEtGouts = (productMap["colours_Et_Gouts"] as? List<*>)?.mapNotNull { color ->
                        (color as? Map<String, Any?>)?.let {
                            Produit_Main_DataBase.Colours_Et_Gouts(
                                position_Du_Couleur_Au_Produit = (it["position_Du_Couleur_Au_Produit"] as? Number)?.toLong() ?: 0,
                                nom = (it["nom"] as? String) ?: "",
                                imogi = (it["imogi"] as? String) ?: ""
                            )
                        }
                    } ?: emptyList()

                    // Safely extract mutable status
                    val mutableStatus = (productMap["mutable_App_Produit_Statues"] as? Map<String, Any?>)?.let { statusMap ->
                        Produit_Main_DataBase.Mutable_App_Produit_Statues(
                            init_dernier_Vent_date_time_String = (statusMap["dernier_Vent_date_time_String"] as? String) ?: "",
                            init_its_Filtre_Au_Grossists_Buttons = (statusMap["its_Filtre_Au_Grossists_Buttons"] as? Boolean) ?: false
                        )
                    } ?: Produit_Main_DataBase.Mutable_App_Produit_Statues()

                    // Create the product object with basic properties
                    Produit_Main_DataBase(
                        id = (productMap["id"] as? Number)?.toLong() ?: 0,
                        it_ref_Id_don_FireBase = (productMap["it_ref_Id_don_FireBase"] as? Number)?.toLong() ?: 0,
                        it_ref_don_FireBase = (productMap["it_ref_don_FireBase"] as? String) ?: "",
                        init_nom = (productMap["nom"] as? String) ?: "",
                        init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
                        init_it_Image_besoin_To_Be_Updated = (productMap["it_Image_besoin_To_Be_Updated"] as? Boolean) ?: false,
                        initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                        init_colours_Et_Gouts = coloursEtGouts,
                        init_mutable_App_Produit_Statues = mutableStatus
                    ).apply {
                        // Safely add purchase demands
                        (productMap["demmende_Achate_De_Cette_Produit"] as? List<*>)?.forEach { demand ->
                            (demand as? Map<String, Any?>)?.let { demandMap ->
                                val clientColors = (demandMap["colours_Et_Gouts_Acheter_Depuit_Client"] as? List<*>)?.mapNotNull { clientColor ->
                                    (clientColor as? Map<String, Any?>)?.let {
                                        Produit_Main_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client(
                                            vidPosition = (it["vidPosition"] as? Number)?.toLong() ?: 0,
                                            nom = (it["nom"] as? String) ?: "",
                                            quantity_Achete = (it["quantity_Achete"] as? Number)?.toInt() ?: 0,
                                            imogi = (it["imogi"] as? String) ?: ""
                                        )
                                    }
                                } ?: emptyList()

                                demmende_Achate_De_Cette_Produit.add(
                                    Produit_Main_DataBase.Demmende_Achate_De_Cette_Produit(
                                        vid = (demandMap["vid"] as? Number)?.toLong() ?: 0,
                                        id_Acheteur = (demandMap["id_Acheteur"] as? Number)?.toLong() ?: 0,
                                        nom_Acheteur = (demandMap["nom_Acheteur"] as? String) ?: "",
                                        time_String = (demandMap["time_String"] as? String) ?: "",
                                        inseartion_Temp = (demandMap["inseartion_Temp"] as? Number)?.toLong() ?: 0,
                                        inceartion_Date = (demandMap["inceartion_Date"] as? Number)?.toLong() ?: 0,
                                        initial_Colours_Et_Gouts_Acheter_Depuit_Client = clientColors
                                    )
                                )
                            }
                        }

                        // Safely add supplier choices
                        (productMap["grossist_Choisi_Pour_Acheter_CeProduit"] as? List<*>)?.forEach { supplier ->
                            (supplier as? Map<String, Any?>)?.let { supplierMap ->
                                val supplierColors = (supplierMap["colours_Et_Gouts_Commende"] as? List<*>)?.mapNotNull { colorMap ->
                                    (colorMap as? Map<String, Any?>)?.let {
                                        Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction.Colours_Et_Gouts_Commende_Au_Supplier(
                                            position_Du_Couleur_Au_Produit = (it["position_Du_Couleur_Au_Produit"] as? Number)?.toLong() ?: 0,
                                            id_Don_Tout_Couleurs = (it["id_Don_Tout_Couleurs"] as? Number)?.toLong() ?: 0,
                                            nom = (it["nom"] as? String) ?: "",
                                            quantity_Achete = (it["quantity_Achete"] as? Number)?.toInt() ?: 0,
                                            imogi = (it["imogi"] as? String) ?: ""
                                        )
                                    }
                                } ?: emptyList()

                                grossist_Choisi_Pour_Acheter_CeProduit.add(
                                    Produit_Main_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
                                        vid = (supplierMap["vid"] as? Number)?.toLong() ?: 0,
                                        supplier_id = (supplierMap["supplier_id"] as? Number)?.toLong() ?: 0,
                                        nom = (supplierMap["nom"] as? String) ?: "",
                                        date = (supplierMap["date"] as? String) ?: "",
                                        couleur = (supplierMap["couleur"] as? String) ?: "#FFFFFF",
                                        currentCreditBalance = (supplierMap["currentCreditBalance"] as? Number)?.toDouble() ?: 0.0,
                                        init_position_Grossist_Don_Parent_Grossists_List = (supplierMap["position_Grossist_Don_Parent_Grossists_List"] as? Number)?.toInt() ?: 0,
                                        init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = (supplierMap["position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit"] as? Number)?.toInt() ?: 0,
                                        initialColours_Et_Gouts_Commende_Au_Supplier = supplierColors
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("App_Initialize_Model", "Error processing product data", e)
                    null
                }
            }

            // Log statistics for debugging
            Log.d("App_Initialize_Model", "Converted ${convertedProduits.size} products")
            Log.d("App_Initialize_Model", "Products with suppliers: ${convertedProduits.count { it.grossist_Choisi_Pour_Acheter_CeProduit.isNotEmpty() }}")
            Log.d("App_Initialize_Model", "Products with demands: ${convertedProduits.count { it.demmende_Achate_De_Cette_Produit.isNotEmpty() }}")
            Log.d("App_Initialize_Model", "Products with colors: ${convertedProduits.count { it.colours_Et_Gouts.isNotEmpty() }}")

            // Update the state list
            produits_Main_DataBase.addAll(convertedProduits)

        } catch (e: Exception) {
            Log.e("App_Initialize_Model", "Failed to load state from Firebase", e)
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }
}
