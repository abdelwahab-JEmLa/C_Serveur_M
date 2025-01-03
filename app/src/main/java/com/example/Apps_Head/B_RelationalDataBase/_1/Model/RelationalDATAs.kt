package com.example.Apps_Head.B_RelationalDataBase._1.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.FirebaseDatabase

class RelationalDatabase {
    // State holders for the UI
    var clientAcheteurEtCesProduits: Map<ClientInformationsModel, List<ProduitModel>> by mutableStateOf(emptyMap())
    var produitsMainList: SnapshotStateList<ProduitModel> = mutableStateListOf()

    /**
     * Represents a product in the database
     */
    data class ProduitModel(
        var id: Long = 0,
        val nom: String = "",
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ProduitModel) return false
            return id == other.id && nom == other.nom
        }

        override fun hashCode(): Int {
            return id.hashCode() * 31 + nom.hashCode()
        }
    }

    /**
     * Represents client information in the database
     */
    data class ClientInformationsModel(
        val id: Long = 0,
        val nom: String = "",
        val couleur: String = ""
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ClientInformationsModel) return false
            return id == other.id &&
                    nom == other.nom &&
                    couleur == other.couleur
        }

        override fun hashCode(): Int {
            return id.hashCode() * 31 + nom.hashCode() * 31 + couleur.hashCode()
        }
    }
    companion object {
        private const val CHEMIN_BASE = "0_1Jan3ProtoUiStateRelationalDATAs"
        val refFireBase = FirebaseDatabase.getInstance().getReference(CHEMIN_BASE)
    }

}
