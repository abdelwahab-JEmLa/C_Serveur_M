package com.example.Packages._3.Fragment.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import java.util.logging.Level
import java.util.logging.Logger

enum class Grossists_Buttons_Modes {
    NONE, REORDER, EDIT, DELETE
}

@IgnoreExtraProperties
class Ui_Mutable_State {
    private val database = Firebase.database
    private val databaseReference = database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
    private val logger = Logger.getLogger(this::class.java.name)

    var produits_Commend_DataBase: List<Produits_Commend_DataBase> by mutableStateOf(emptyList())
    var namePhone: String by mutableStateOf("")
    var selectedSupplierId: Long by mutableLongStateOf(0L)
    var mode_Update_Produits_Non_Defini_Grossist: Boolean by mutableStateOf(false)
    var mode_Trie_Produit_Non_Trouve: Boolean by mutableStateOf(false)
    var currentMode: Grossists_Buttons_Modes by mutableStateOf(Grossists_Buttons_Modes.NONE)

    @IgnoreExtraProperties
    data class Produits_Commend_DataBase(
        val id: Int = 0,
        val nom: String = "",
        var non_Trouve: Boolean = false,
        var grossist_Choisi_Pour_Acheter_CeProduit: Grossist_Choisi_Pour_Acheter_CeProduit? = null,
        var colours_Et_Gouts_Commende: List<Colours_Et_Gouts_Commende>? = emptyList()
    ) {
        constructor() : this(0)

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
        data class Colours_Et_Gouts_Commende(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var id_Don_Tout_Couleurs: Long = 0,
            var nom: String = "",
            var quantity_Achete: Int = 0,
            var imogi: String = ""
        ) {
            constructor() : this(0)
        }

        fun updateSelf(uiMutableState: Ui_Mutable_State) {
            uiMutableState.update_Ui_Mutable_State_C_produits_Commend_DataBase(this)
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

    fun toMap(): Map<String, Any?> = mapOf(
        "produits_Commend_DataBase" to produits_Commend_DataBase,
        "namePhone" to namePhone,
        "selectedSupplierId" to selectedSupplierId,
        "mode_Update_Produits_Non_Defini_Grossist" to mode_Update_Produits_Non_Defini_Grossist,
        "mode_Trie_Produit_Non_Trouve" to mode_Trie_Produit_Non_Trouve,
        "currentMode" to currentMode
    )

    private fun safeFirebaseUpdate(action: () -> Unit) {
        try {
            action()
            databaseReference.setValue(toMap())
                .addOnSuccessListener { logger.log(Level.INFO, "Firebase update successful") }
                .addOnFailureListener { e -> logger.log(Level.SEVERE, "Firebase update failed: ${e.message}") }
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Firebase operation error: ${e.message}")
        }
    }

    fun clear_Ui_Mutable_State_C_produits_Commend_DataBase() {
        safeFirebaseUpdate { produits_Commend_DataBase = emptyList() }
    }

    fun addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(items: List<Produits_Commend_DataBase>) {
        safeFirebaseUpdate { produits_Commend_DataBase = items }
    }

    fun update_Ui_Mutable_State_C_produits_Commend_DataBase(updatedItem: Produits_Commend_DataBase) {
        safeFirebaseUpdate {
            produits_Commend_DataBase = produits_Commend_DataBase.map {
                if (it.id == updatedItem.id) updatedItem else it
            }
        }
    }

    fun Update_Parent_Ui_State_Var(
        updated_Ui: Ui_Mutable_State? = null,
        produits_Commend_DataBase: List<Produits_Commend_DataBase>? = null,
        namePhone: String? = null,
        selectedSupplierId: Long? = null,
        mode_Update_Produits_Non_Defini_Grossist: Boolean? = null,
        mode_Trie_Produit_Non_Trouve: Boolean? = null,
        currentMode: Grossists_Buttons_Modes? = null
    ) {
        safeFirebaseUpdate {
            updated_Ui?.let {
                this.produits_Commend_DataBase = it.produits_Commend_DataBase
                this.namePhone = it.namePhone
                this.selectedSupplierId = it.selectedSupplierId
                this.mode_Update_Produits_Non_Defini_Grossist = it.mode_Update_Produits_Non_Defini_Grossist
                this.mode_Trie_Produit_Non_Trouve = it.mode_Trie_Produit_Non_Trouve
                this.currentMode = it.currentMode
            }

            produits_Commend_DataBase?.let { this.produits_Commend_DataBase = it }
            namePhone?.let { this.namePhone = it }
            selectedSupplierId?.let { this.selectedSupplierId = it }
            mode_Update_Produits_Non_Defini_Grossist?.let { this.mode_Update_Produits_Non_Defini_Grossist = it }
            mode_Trie_Produit_Non_Trouve?.let { this.mode_Trie_Produit_Non_Trouve = it }
            currentMode?.let { this.currentMode = it }
        }
    }

    fun logGroupingDetails(tag: String = "No Tag", repeteList: Int = 0) {
        logger.info("=== Grouping Details for $tag ===")
        logger.info("Repeated List Count: $repeteList")
        logger.info("Total Products: ${produits_Commend_DataBase.size}")

        produits_Commend_DataBase.forEachIndexed { index, product ->
            logger.info("""
                Product $index:
                  ID: ${product.id}
                  Label: ${product.nom}
                  Not Found: ${product.non_Trouve}
                  ${product.grossist_Choisi_Pour_Acheter_CeProduit?.let { supplier ->
                """
                    Supplier:
                      Name: ${supplier.nom}
                      Credit Balance: ${supplier.currentCreditBalance}
                    """.trimIndent()
            } ?: "No Supplier"}
            """.trimIndent())
        }

        logger.info("Status: $namePhone")
        logger.info("=== End Details ===")
    }
}
