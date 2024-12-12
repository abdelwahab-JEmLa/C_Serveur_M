package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ancien_Produits_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ancien_SoldArticlesTabelle
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial.TAG
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class AncienData(
    val produitsDatabase: List<Ancien_Produits_DataBase>,
    val soldArticles: List<Ancien_SoldArticlesTabelle>
)

suspend fun get_Datas(): AncienData {
    try {
        val produitsSnapshot = Firebase.database
            .getReference("e_DBJetPackExport")
            .get()
            .await()

        val soldArticlesSnapshot = Firebase.database
            .getReference("O_SoldArticlesTabelle")
            .get()
            .await()

        val produitsList = produitsSnapshot.children.mapNotNull {
            it.getValue(Ancien_Produits_DataBase::class.java)
        }

        val soldArticlesList = soldArticlesSnapshot.children.mapNotNull {
            it.getValue(Ancien_SoldArticlesTabelle::class.java)
        }

        return AncienData(produitsList, soldArticlesList)
    } catch (e: Exception) {
        Log.e("GetDatas", "Error fetching data from Firebase", e)
        throw e
    }
}
