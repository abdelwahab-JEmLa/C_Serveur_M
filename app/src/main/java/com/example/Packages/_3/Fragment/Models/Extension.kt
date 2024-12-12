package com.example.Packages._3.Fragment.Models

import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ui_Mutable_State
import okhttp3.internal.concurrent.TaskRunner.Companion.logger
import java.util.logging.Level

enum class Grossists_Buttons_Modes {
    NONE, REORDER, EDIT, DELETE
}

private fun Ui_Mutable_State.safeFirebaseUpdate(action: () -> Unit) {
    try {
        action()
        databaseReference.setValue(toMap())
            .addOnSuccessListener { logger.log(Level.INFO, "Firebase update successful") }
            .addOnFailureListener { e ->
                logger.log(
                    Level.SEVERE,
                    "Firebase update failed: ${e.message}"
                )
            }
    } catch (e: Exception) {
        logger.log(Level.SEVERE, "Firebase operation error: ${e.message}")
    }
}

fun Ui_Mutable_State.clear_Ui_Mutable_State_C_produits_Commend_DataBase() {
    safeFirebaseUpdate { produits_Commend_DataBase = emptyList() }
}

fun Ui_Mutable_State.addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(items: List<Ui_Mutable_State.Produits_Commend_DataBase>) {
    safeFirebaseUpdate { produits_Commend_DataBase = items }
}

fun Ui_Mutable_State.update_Ui_Mutable_State_C_produits_Commend_DataBase(updatedItem: Ui_Mutable_State.Produits_Commend_DataBase) {
    safeFirebaseUpdate {
        produits_Commend_DataBase = produits_Commend_DataBase.map {
            if (it.id == updatedItem.id) updatedItem else it
        }
    }
}

fun Ui_Mutable_State.Update_Parent_Ui_State_Var(
    updated_Ui: Ui_Mutable_State? = null,
    produits_Commend_DataBase: List<Ui_Mutable_State.Produits_Commend_DataBase>? = null,
    namePhone: String? = null,
    selectedSupplierId: Long? = null,
    mode_Update_Produits_Non_Defini_Grossist: Boolean? = null,
    mode_Trie_Produit_Non_Trouve: Boolean? = null,
    currentMode: Grossists_Buttons_Modes? = null,
    groupeur_References_FireBase_DataBase: List<Ui_Mutable_State.Groupeur_References_FireBase_DataBase>? = null,
) {
    safeFirebaseUpdate {
        updated_Ui?.let {
            this.produits_Commend_DataBase = it.produits_Commend_DataBase
            this.namePhone = it.namePhone
            this.selectedSupplierId = it.selectedSupplierId
            this.mode_Update_Produits_Non_Defini_Grossist =
                it.mode_Update_Produits_Non_Defini_Grossist
            this.mode_Trie_Produit_Non_Trouve = it.mode_Trie_Produit_Non_Trouve
            this.currentMode = it.currentMode
            this.groupeur_References_FireBase_DataBase = it.groupeur_References_FireBase_DataBase
        }

        produits_Commend_DataBase?.let { this.produits_Commend_DataBase = it }
        namePhone?.let { this.namePhone = it }
        selectedSupplierId?.let { this.selectedSupplierId = it }
        mode_Update_Produits_Non_Defini_Grossist?.let {
            this.mode_Update_Produits_Non_Defini_Grossist = it
        }
        mode_Trie_Produit_Non_Trouve?.let { this.mode_Trie_Produit_Non_Trouve = it }
        currentMode?.let { this.currentMode = it }
        groupeur_References_FireBase_DataBase?.let {
            this.groupeur_References_FireBase_DataBase = it
        }
    }
}


fun Ui_Mutable_State.toMap(): Map<String, Any?> = mapOf(
    "produits_Commend_DataBase" to produits_Commend_DataBase,
    "namePhone" to namePhone,
    "selectedSupplierId" to selectedSupplierId,
    "mode_Update_Produits_Non_Defini_Grossist" to mode_Update_Produits_Non_Defini_Grossist,
    "mode_Trie_Produit_Non_Trouve" to mode_Trie_Produit_Non_Trouve,
    "currentMode" to currentMode
)
