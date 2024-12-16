package com.example.Packages._4.Fragment.ViewModel._1.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.getValue  // Keep only this getValue import
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

class Ui_State_4_Fragment internal constructor(
    initialLastUpdateTime: String? = getCurrentFormattedTime(),
) {
    private val ref_4_Fragment_Ui_State = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("Packages")
        .child("_4_Fragment_Ui_State")

    var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
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

    companion object {
        private fun getCurrentFormattedTime(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
        }
    }

    suspend fun update_UiStateFirebaseDataBase() {
        try {
            ref_4_Fragment_Ui_State.setValue(this).await()
            lastUpdateTimeFormatted = getCurrentFormattedTime()
        } catch (e: Exception) {
            throw Exception("Failed to update state in Firebase: ${e.message}")
        }
    }

    suspend fun load_Self_FromFirebaseDataBase() {
        try {
            val snapshot = ref_4_Fragment_Ui_State.get().await()
            snapshot.getValue<Ui_State_4_Fragment>()?.let { state ->
                lastUpdateTimeFormatted = state.lastUpdateTimeFormatted
                selectedSupplierId = state.selectedSupplierId
                currentMode = state.currentMode
            }
        } catch (e: Exception) {
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }
}
