package com.example.Packages._3.Fragment.ViewModel._2.Init

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.ColorArticle
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.TAG
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
suspend fun getSupplierInfosDataUi(idSupplierSu: Long): UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit? {
    return try {
        val supplierSnapshot = Firebase.database.getReference("F_Suppliers")
            .orderByChild("idSupplierSu")
            .equalTo(idSupplierSu.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        supplierSnapshot?.let { snapshot ->
            UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit(
                id = idSupplierSu,
                position_Grossist_Don_Parent_Grossists_List = snapshot.child("position")
                    .getValue(Int::class.java) ?: 0,
                nom = snapshot.child("nomSupplierSu").getValue(String::class.java) ?: "",
                couleur = snapshot.child("couleurSu").getValue(String::class.java) ?: "#FFFFFF",
                currentCreditBalance = snapshot.child("currentCreditBalance")
                    .getValue(Double::class.java) ?: 0.0,
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching supplier data for ID $idSupplierSu", e)
        null
    }
}
suspend fun getColorData(colorId: Long): ColorArticle? {
    return try {
        val colorSnapshot = Firebase.database.getReference("H_ColorsArticles")
            .orderByChild("idColore")
            .equalTo(colorId.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        colorSnapshot?.getValue(ColorArticle::class.java) ?: colorSnapshot?.let {
            ColorArticle(
                idColore = it.child("idColore").getValue(Long::class.java) ?: colorId,
                nameColore = it.child("nameColore").getValue(String::class.java) ?: "",
                iconColore = it.child("iconColore").getValue(String::class.java) ?: "",
                classementColore = it.child("classementColore").getValue(Int::class.java) ?: 0
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Error fetching color data for color $colorId",
            e
        )
        null
    }
}

// Helper functions from your existing code
internal suspend fun getSupplierArticlesData(idArticle: Long): DataSnapshot? {
    return try {
        Firebase.database.getReference("K_SupplierArticlesRecived")
            .orderByChild("a_c_idarticle_c")
            .equalTo(idArticle.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()
    } catch (e: Exception) {
        Log.e(
            TAG,
            "Error fetching supplier data for article $idArticle",
            e
        )
        null
    }
}
