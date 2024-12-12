package com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial

import android.content.ContentValues.TAG
import android.util.Log
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ui_Mutable_State
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await


suspend fun getSupplierInfosData(idSupplierSu: Long): Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit? {
    return try {
        val supplierSnapshot = Firebase.database.getReference("F_Suppliers")
            .orderByChild("idSupplierSu")
            .equalTo(idSupplierSu.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        supplierSnapshot?.let { snapshot ->
            Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit(
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

suspend fun getClientData(clientId: Long): String {
    return try {
        val clientSnapshot = Firebase.database.getReference("G_Clients")
            .orderByChild("idClientsSu")
            .equalTo(clientId.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        clientSnapshot?.child("nomClientsSu")?.getValue(String::class.java) ?: ""
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching client data for ID $clientId", e)
        ""
    }
}
