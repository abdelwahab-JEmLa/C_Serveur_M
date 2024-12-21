package com.example.Packages._3.Fragment.Z.Archives.Model.Archives

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.Packages._3.Fragment.Models.Archives.Grossists_Buttons_Modes
import com.example.Packages._3.Fragment.Models.UiState
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database


@IgnoreExtraProperties
class Ui_Mutable_State {
    private val database = Firebase.database
    val databaseReference = database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
    var produits_Commend_DataBase: List<Produits_Commend_DataBase> by mutableStateOf(emptyList())
    var groupeur_References_FireBase_DataBase: List<Groupeur_References_FireBase_DataBase> by mutableStateOf(
        emptyList()
    )
    val groupeur_References_FireBase_DataBaseSnap =
        mutableListOf<Groupeur_References_FireBase_DataBase>().toMutableStateList()

    var namePhone: String by mutableStateOf("")
    var selectedSupplierId: Long by mutableLongStateOf(0L)
    var mode_Update_Produits_Non_Defini_Grossist: Boolean by mutableStateOf(false)
    var mode_Trie_Produit_Non_Trouve: Boolean by mutableStateOf(false)
    var currentMode: Grossists_Buttons_Modes by mutableStateOf(Grossists_Buttons_Modes.NONE)

    class Groupeur_References_FireBase_DataBaseSnap(
        var id: Long = 0,
        var position: Int = 0,
        var nom: String = "",
        last_Update_Time_Formatted: String? = null,
        val update_All: Boolean = false,
        var produits_A_Update: SnapshotStateList<Groupeur_References_FireBase_DataBase> = mutableListOf<Groupeur_References_FireBase_DataBase>().toMutableStateList()
    ) {
        constructor() : this(0)

        @IgnoreExtraProperties
        data class Produits_A_Update(
            var id: Long = 0,
            var position: Int = 0,
            var ref: String = "",
            var nom: String = "",
            var tiggr_Time: Long = 0,
        ) {
            constructor() : this(0)
        }

        fun updateFirebaseSelfF(groupRef: Groupeur_References_FireBase_DataBase) {
            val referencesRef = Firebase.database
                .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
                .child("1_Groupeur_References_FireBase_DataBase")
                .child(groupRef.id.toString())
            referencesRef.removeValue()
            referencesRef.setValue(groupRef)
        }

    }
    @IgnoreExtraProperties
    data class Produits_Commend_DataBase(
        val id: Int = 0,
        val nom: String = "",
        var non_Trouve: Boolean = false,
        var colours_Et_Gouts_Commende: List<Colours_Et_Gouts_Commende>? = emptyList(),
        var vent_List_DataBase: List<Demmende_Achate_De_Cette_Produit>? = emptyList(),
        var grossist_Choisi_Pour_Acheter_CeProduit: Grossist_Choisi_Pour_Acheter_CeProduit? = null
    ) {
        constructor() : this(0)

        @IgnoreExtraProperties
        data class Colours_Et_Gouts_Commende(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var id_Don_Tout_Couleurs: Long = 0,
            var nom: String = "",
            var quantity_Achete: Int = 0,
            var imogi: String = ""
        ) {
            constructor() : this(0)
        }
        @IgnoreExtraProperties
        data class Grossist_Choisi_Pour_Acheter_CeProduit(
            var id: Long = 0,
            var position_Grossist_Don_Parent_Grossists_List: Int = 0,
            var nom: String = "",
            var couleur: String = "#FFFFFF",
            var currentCreditBalance: Double = 0.0,
            val position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int = 0
        ) {
            constructor() : this(0)
        }
        @IgnoreExtraProperties
        data class Demmende_Achate_De_Cette_Produit(
            var vid: Long = 0,
            var id_Acheteur: Long = 0,
            var nom_Acheteur: String = "",
            var inseartion_Temp: Long = 0, //HH:mm:ss,
            var inceartion_Date: Long = 0, //yyyy/mm/dd,
            var colours_Et_Gouts_Acheter: List<Colours_Et_Gouts_Acheter>? = emptyList(),
        ) {
            constructor() : this(0)

            @IgnoreExtraProperties
            data class Colours_Et_Gouts_Acheter(
                var vidPosition: Long = 0,
                var id_Don_Tout_Couleurs: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            ) {
                constructor() : this(0)
            }
        }

        fun updateSelf(uiMutableState: UiState) {
        }

    }

    @IgnoreExtraProperties
    data class Groupeur_References_FireBase_DataBase(
        var id: Long = 0,
        var position: Int = 0,
        var nom: String = "",
        var nom_2: String = "",
        var description: String = "",
        var ref: String = "",
        val last_Update_Time_Formatted: String? = null,
        val update_All: Boolean = false,
        var produits_A_Update: SnapshotStateList<Groupeur_References_FireBase_DataBase> = mutableListOf<Groupeur_References_FireBase_DataBase>().toMutableStateList()
    ) {
        constructor() : this(0)

        @IgnoreExtraProperties
        data class Produits_A_Update(
            var id: Long = 0,
            var position: Int = 0,
            var ref: String = "",
            var nom: String = "",
            var tiggr_Time: Long = 0,
        ) {
            constructor() : this(0)
        }

        fun updateFirebaseSelfF(groupRef: Groupeur_References_FireBase_DataBase) {
            val referencesRef = Firebase.database
                .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
                .child("1_Groupeur_References_FireBase_DataBase")
                .child(groupRef.id.toString())
            referencesRef.removeValue()
            referencesRef.setValue(groupRef)
        }

    }

    constructor()
    constructor(
        produits_Commend_DataBase: List<Produits_Commend_DataBase> = emptyList(),
        namePhone: String = ""
    ) {
        this.produits_Commend_DataBase = produits_Commend_DataBase
        this.namePhone = namePhone
    }
}
