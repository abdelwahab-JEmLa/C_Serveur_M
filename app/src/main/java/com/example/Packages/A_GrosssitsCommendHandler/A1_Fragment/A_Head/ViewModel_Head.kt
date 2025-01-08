package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._4.Init.initializer
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Companion.mapsFireBaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ViewModel_Head : ViewModel() {
    private var _appsHeadModel by mutableStateOf(AppsHeadModel())
    var _mapsModel by mutableStateOf(Model_CodingWithMaps())
    val mapsModel: Model_CodingWithMaps get() = _mapsModel

    private var isInitializing by mutableStateOf(false)
    private var initializationComplete by mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                initializer(_appsHeadModel)
                isInitializing = true

                val startImplementation = true
                if (startImplementation) {
                    val filteredAndGroupedData = _appsHeadModel.produitsMainDataBase
                        .filter { it.bonCommendDeCetteCota?.grossistInformations != null }
                        .groupBy { it.bonCommendDeCetteCota!!.grossistInformations!! }
                        .toList()

                    // Update Firebase
                    mapsFireBaseRef
                        .child("filteredAndGroupedData")
                        .setValue(filteredAndGroupedData)
                }

                // Listen for changes
                mapsFireBaseRef
                    .child("filteredAndGroupedData")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val updatedList = snapshot.children.mapNotNull { grossistSnapshot ->
                                try {
                                    val grossist = grossistSnapshot.child("first")
                                        .getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)
                                    val produits =
                                        grossistSnapshot.child("second").children.mapNotNull { produitSnapshot ->
                                            produitSnapshot.getValue(AppsHeadModel.ProduitModel::class.java)
                                        }
                                    if (grossist != null) {
                                        grossist to produits
                                    } else null
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            _mapsModel.maps.grossistList = updatedList
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                            println("Firebase Error: ${error.message}")
                        }
                    })
                initializationComplete = true
            } finally {
                isInitializing = false
            }
        }
    }

}
