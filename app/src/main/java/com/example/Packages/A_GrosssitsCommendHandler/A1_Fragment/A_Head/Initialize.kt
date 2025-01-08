package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import android.util.Log
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.Companion.mapsFireBaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

fun processAndUploadData(viewmodelHead: ViewModel_Head): List<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>> {
    val filteredAndGroupedData = viewmodelHead._appsHeadModel.produitsMainDataBase
        .filter { it.bonCommendDeCetteCota?.grossistInformations != null }
        .groupBy { it.bonCommendDeCetteCota!!.grossistInformations!! }
        .toList()

    mapsFireBaseRef
        .child("filteredAndGroupedData")
        .setValue(filteredAndGroupedData)

    return filteredAndGroupedData
}

fun setupFirebaseListener(viewmodelHead: ViewModel_Head) {
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
                        grossist?.let { it to produits }
                    } catch (e: Exception) {
                        null
                    }
                }
                viewmodelHead._mapsModel.maps.grossistList = updatedList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewModel_Head", "Firebase Error: ${error.message}")
            }
        })
}
