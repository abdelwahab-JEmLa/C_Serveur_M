package com.example.c_serveur.ViewModel.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.Packages._3.Fragment.Models.UiState.Produit_DataBase
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await


class App_Initialize_Model internal constructor(
    initial_Produit_Main_DataBase: List<Produit_Main_DataBase> = emptyList()
) {
    var produit_Main_DataBase: SnapshotStateList<Produit_Main_DataBase> =
        initial_Produit_Main_DataBase.toMutableStateList()

    class Produit_Main_DataBase(
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
            var position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int by mutableStateOf(
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

        private val ref_Produit_Main_DataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("produit_DataBase")

        suspend fun updateSelfInFirebaseDataBase() {
            try {
                val groupRef = Firebase.database
                ref_Produit_Main_DataBase
                    .child(this.id.toString())

                groupRef.setValue(this).await()     //->
                //FIXME: ("Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:
                //public inline operator fun MutableDoubleState.setValue(thisObj: Any?, property: KProperty<*>, value: Double): Unit defined in androidx.compose.runtime
                //public inline operator fun MutableFloatState.setValue(thisObj: Any?, property: KProperty<*>, value: Float): Unit defined in androidx.compose.runtime
                //public inline operator fun MutableIntState.setValue(thisObj: Any?, property: KProperty<*>, value: Int): Unit defined in androidx.compose.runtime
                //public inline operator fun MutableLongState.setValue(thisObj: Any?, property: KProperty<*>, value: Long): Unit defined in androidx.compose.runtime
                //public inline operator fun <T> MutableState<TypeVariable(T)>.setValue(thisObj: Any?, property: KProperty<*>, value: TypeVariable(T)): Unit defined in androidx.compose.runtime")
            } catch (e: Exception) {
                throw Exception("Failed to update group in Firebase: ${e.message}")
            }
        }

        suspend fun load_Self_FromFirebaseDataBase() {
            try {
                val snapshot = ref_Produit_Main_DataBase.get().await()
                snapshot.getValue<Produit_Main_DataBase>()?.let { produits ->

                    this.addAll(this)       //->
                    //FIXME: ("
                    //Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:
                    //public fun <T> MutableCollection<in TypeVariable(T)>.addAll(elements: Array<out TypeVariable(T)>): Boolean defined in kotlin.collections
                    //public fun <T> MutableCollection<in TypeVariable(T)>.addAll(elements: Iterable<TypeVariable(T)>): Boolean defined in kotlin.collections
                    //public fun <T> MutableCollection<in TypeVariable(T)>.addAll(elements: Sequence<TypeVariable(T)>): Boolean defined in kotlin.collections")
                }
            } catch (e: Exception) {
                throw Exception("Failed to load state from Firebase: ${e.message}")
            }
        }

    }
}
