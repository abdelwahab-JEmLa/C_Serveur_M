package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewModel_Head : ViewModel() {
    private var _appsHeadModel by mutableStateOf(AppsHeadModel())
    var _mapsModel by mutableStateOf(Model_CodingWithMaps())
    val mapsModel: Model_CodingWithMaps get() = _mapsModel

    private var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)
    var initializationProgress by mutableFloatStateOf(0f)


    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"


    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                isInitializing = true
                initializer(_appsHeadModel, initializationProgress) {
                    { index, ancienData ->
                        initializationProgress=  0.1f + (0.8f * (index + 1) / ancienData.produitsDatabase.size)
                    }
                }

                processAndUploadData()

                setupFirebaseListener()

                initializationComplete = true
            } finally {
                isInitializing = false
            }
        }
    }

    private fun processAndUploadData(): List<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>> {
        val filteredAndGroupedData = _appsHeadModel.produitsMainDataBase
            .filter { it.bonCommendDeCetteCota?.grossistInformations != null }
            .groupBy { it.bonCommendDeCetteCota!!.grossistInformations!! }
            .toList()

        mapsFireBaseRef
            .child("filteredAndGroupedData")
            .setValue(filteredAndGroupedData)

        return filteredAndGroupedData
    }

    private fun setupFirebaseListener() {
        mapsFireBaseRef
            .child("filteredAndGroupedData")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updatedList = snapshot.children.mapNotNull { grossistSnapshot ->
                        try {
                            val grossist = grossistSnapshot.child("first")
                                .getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)
                            val produits = grossistSnapshot.child("second").children.mapNotNull { produitSnapshot ->
                                produitSnapshot.getValue(AppsHeadModel.ProduitModel::class.java)
                            }
                            grossist?.let { it to produits }
                        } catch (e: Exception) {
                            null
                        }
                    }
                    _mapsModel.maps.grossistList = updatedList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ViewModel_Head", "Firebase Error: ${error.message}")
                }
            })
    }
}
