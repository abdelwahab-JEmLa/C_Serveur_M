package com.example.Packages._3.Fragment.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UiState internal constructor(
    initialLastUpdateTime: String? = getCurrentFormattedTime(),
    initialReferencesFireBaseGroup: List<ReferencesFireBaseGroup> = emptyList(),
    initial_Produits_DataBase: List<Produit_DataBase> = emptyList()
) {

    var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
    var referencesFireBaseGroup: SnapshotStateList<ReferencesFireBaseGroup> =
        initialReferencesFireBaseGroup.toMutableStateList()
    var produit_DataBase: SnapshotStateList<Produit_DataBase> =
        initial_Produits_DataBase.toMutableStateList()
    var mode_Trie_Produit_Non_Trouve: Boolean by mutableStateOf(false)

    var selectedSupplierId: Long by mutableStateOf(0)

    var currentMode: Affichage_Et_Click_Modes by mutableStateOf(Affichage_Et_Click_Modes.MODE_Affiche_Achteurs)

    enum class Affichage_Et_Click_Modes {
        MODE_Click_Change_Position,
        MODE_Affiche_Achteurs,
        MODE_Affiche_Produits;

        companion object {
            fun toggle(current: Affichage_Et_Click_Modes): Affichage_Et_Click_Modes {
                return when (current) {
                    MODE_Click_Change_Position -> MODE_Affiche_Achteurs
                    MODE_Affiche_Produits -> MODE_Click_Change_Position
                    MODE_Affiche_Achteurs -> MODE_Affiche_Produits
                }
            }
        }
    }

    private val uiStateFireBaseDatabaseRef = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")

    class Produit_DataBase(
        val id: Long = 0,
        val it_ref_Id_don_FireBase: Long = 0,
        val it_ref_don_FireBase: String = "",
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_colours_Et_Gouts: List<Colours_Et_Gouts> = emptyList(),
        initialDemmende_Achate_De_Cette_Produit: List<Demmende_Achate_De_Cette_Produit> = emptyList(),
        initialGrossist_Choisi_Pour_Acheter_CeProduit: List<Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
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

        class Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
            var vid: Long = 0,
            var supplier_id: Long = 0,
            var nom: String = "",
            var date: String = "", //"yyyy-MM-dd HH:mm:ss"
            var position_Grossist_Don_Parent_Grossists_List: Int = 0,
            var couleur: String = "#FFFFFF",
            var currentCreditBalance: Double = 0.0,
            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int = 0,
            initialColours_Et_Gouts_Commende_Au_Supplier: List<Colours_Et_Gouts_Commende_Au_Supplier> = emptyList(),
            ) {
            var position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int by mutableStateOf(init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit)

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

        suspend fun updateSelfInFirebaseDataBase() {
            try {
                val groupRef = Firebase.database
                    .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
                    .child("produit_DataBase")
                    .child(this.id.toString())

                groupRef.setValue(this).await()
            } catch (e: Exception) {
                throw Exception("Failed to update group in Firebase: ${e.message}")
            }
        }
    }

    class ReferencesFireBaseGroup(
        var id: Long = 0L,
        var position: Int = 0,
        var nom: String = "",
        var reference_key: String = "",
        var description: String = "",
        var parent_Id: Long = 0L,
        var parent_key: String = "",
        initialUpdateAllTrigger: Boolean = false,
        initialLastUpdateTime: String? = getCurrentFormattedTime(),
        initialProduits_Update_Ref: List<Produit_Update_Ref> = emptyList()
    ) {
        var updateAllTrigger: Boolean by mutableStateOf(initialUpdateAllTrigger)
        var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
        var produit_Update_Ref: SnapshotStateList<Produit_Update_Ref> =
            initialProduits_Update_Ref.toMutableStateList()

        class Produit_Update_Ref(
            var id: Long = 0L,
            initialTriggerTime: Long = System.currentTimeMillis()
        ) {
            var triggerTime: Long by mutableStateOf(initialTriggerTime)

            fun updateTriggerTime() {
                triggerTime = System.currentTimeMillis()
            }
        }

        suspend fun setSelfInFirebaseDataBase() {
            try {
                this.lastUpdateTimeFormatted = getCurrentFormattedTime()
                val groupRef = Firebase.database
                    .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
                    .child("referencesFireBaseGroup")
                    .child(this.id.toString())

                groupRef.setValue(this).await()
            } catch (e: Exception) {
                throw Exception("Failed to update group in Firebase: ${e.message}")
            }
        }
    }

    companion object {
        private fun getCurrentFormattedTime(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
        }
    }

    suspend fun update_UiStateFirebaseDataBase() {
        try {
            uiStateFireBaseDatabaseRef.setValue(this).await()
            lastUpdateTimeFormatted = getCurrentFormattedTime()
        } catch (e: Exception) {
            throw Exception("Failed to update state in Firebase: ${e.message}")
        }
    }

    suspend fun loadFromFirebaseDataBase() {
        try {
            val snapshot = uiStateFireBaseDatabaseRef.get().await()
            snapshot.getValue<UiState>()?.let { state ->
                lastUpdateTimeFormatted = state.lastUpdateTimeFormatted

                produit_DataBase.clear()
                produit_DataBase.addAll(state.produit_DataBase)
            }
        } catch (e: Exception) {
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }

}
