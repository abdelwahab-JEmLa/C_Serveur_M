package com.example.Packages.P3.E.ViewModel.B.Components

import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.Model.Commende_Produits_Au_Grossissts_DataBase
import com.example.Models.Grossissts_DataBAse
import com.example.Packages.P3.Historique_D_Achate_Grossisst_DataBase
import com.example.Packages.P3.E.ViewModel.ViewModelFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun ViewModelFragment.Insert_Historical_PurchaseData(
    grossisst: Grossissts_DataBAse,
    productsToBeMoved: List<Commende_Produits_Au_Grossissts_DataBase>,) {
    viewModelScope.launch {
            // Validate grossist ID
            if (grossisst.idSupplierSu <= 0) {
                return@launch // Invalid grossist ID
            }

            val currentTime = SimpleDateFormat("yyyy-MM-dd/HH:mm", Locale.getDefault()).format(Date())
            
            val historicalEntries = productsToBeMoved.map { produit ->
                Historique_D_Achate_Grossisst_DataBase(
                    vid = System.currentTimeMillis(), // Generate a unique identifier
                    produit_id = produit.a_c_idarticle_c,
                    produit_nom = produit.nameArticle,
                    time_Achat = currentTime,  // Using the formatted time with hours and minutes
                    grossisst_id = grossisst.idSupplierSu
                )
            }
            dataBase.historique_D_Achate_Grossisst_DataBase_Dao().insertAll(historicalEntries)
    }
}


