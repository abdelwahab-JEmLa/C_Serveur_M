package com.example.Packages._1.Fragment.ViewModel.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UiState internal constructor(
    initialLastUpdateTime: String? = getCurrentFormattedTime(),
) {
    var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)

    var mode_Trie_Produit_Non_Trouve: Boolean by mutableStateOf(false)

    var selectedSupplierId: Long by mutableStateOf(2)

    var currentMode: Affichage_Et_Click_Modes by mutableStateOf(Affichage_Et_Click_Modes.MODE_Click_Change_Position)

    enum class Affichage_Et_Click_Modes {
        MODE_Click_Change_Position,
        MODE_Affiche_Achteurs,
        MODE_Affiche_Produits;

        companion object {
            fun toggle(current: Affichage_Et_Click_Modes): Affichage_Et_Click_Modes {
                return when (current) {
                    MODE_Affiche_Produits -> MODE_Click_Change_Position
                    MODE_Click_Change_Position -> MODE_Affiche_Achteurs
                    MODE_Affiche_Achteurs -> MODE_Affiche_Produits
                }
            }
        }
    }

    private val uiStateFireBaseDatabaseRef = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")

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
}
