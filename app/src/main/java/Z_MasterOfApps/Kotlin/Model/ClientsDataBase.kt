package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@IgnoreExtraProperties
data class ClientsDataBase(
    var id: Long = 1,
    var nom: String = "Non Defini",
    var statueDeBase: StatueDeBase = StatueDeBase(),
    var gpsLocation: GpsLocation = GpsLocation(),
) {
    @IgnoreExtraProperties
    data class StatueDeBase(
        var couleur: String = "#FFFFFF",
        var positionDonClientsList: Int = 0,
        var caRefDonAncienDataBase: String = "G_Clients",
        var cUnClientTemporaire: Boolean = true,
        var auFilterFAB: Boolean = false
    )

    @IgnoreExtraProperties
    data class GpsLocation(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var title: String = "",
        var snippet: String = "",
        var actuelleEtat: DernierEtatAAffiche? = null
    ) {
        @IgnoreExtraProperties
        enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
            ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "نشط / متصل"),
            VENDU_A_LUI(android.R.color.holo_purple, ""),
            Cible(android.R.color.holo_red_light, "Cible"),
            CLIENT_ABSENT(android.R.color.darker_gray, "غائب الشاري"),
            AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
            FERME(android.R.color.darker_gray, "مغلق")
        }
    }

    // ClientsDataBase.kt - Updated companion object
    companion object {
        val refClientsDataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("ClientsDataBase")

        fun ClientsDataBase.updateClientsDataBase(
            viewModel: ViewModelInitApp
        ) {
            viewModel.viewModelScope.launch {
                try {
                    // Create a snapshot of the current state
                    val currentState = this@updateClientsDataBase.copy()

                    // Update local state
                    val clientsList = viewModel._modelAppsFather.clientDataBaseSnapList
                    val index = clientsList.indexOfFirst { it.id == currentState.id }

                    if (index != -1) {
                        clientsList[index] = currentState
                    } else {
                        // If client doesn't exist, add them
                        clientsList.add(currentState)
                    }

                    // Update Firebase with error handling
                    try {
                        refClientsDataBase.child(currentState.id.toString())
                            .setValue(currentState)
                            .await()
                    } catch (e: Exception) {
                        // Revert local state if Firebase update fails
                        if (index != -1) {
                            clientsList[index] = this@updateClientsDataBase
                        } else {
                            clientsList.removeAt(clientsList.lastIndex)
                        }
                        throw e
                    }

                } catch (e: Exception) {
                    Log.e("ClientsDataBase", "Failed to update client", e)

                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientsDataBase) return false

        return id == other.id &&
                nom == other.nom &&
                statueDeBase == other.statueDeBase &&
                gpsLocation == other.gpsLocation
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + nom.hashCode()
        result = 31 * result + statueDeBase.hashCode()
        result = 31 * result + gpsLocation.hashCode()
        return result
    }
}
