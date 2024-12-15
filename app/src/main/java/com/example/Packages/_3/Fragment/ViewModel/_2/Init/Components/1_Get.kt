package com.example.Packages._3.Fragment.ViewModel._2.Init.Components

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Components.Ancien_ClientsDataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Components.Ancien_ColorArticle
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Components.Ancien_Produits_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Components.Ancien_SoldArticlesTabelle
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class Ancien_Datas_Resources(
    val produitsDatabase: List<Ancien_Produits_DataBase>,
    val soldArticles: List<Ancien_SoldArticlesTabelle>,
    val couleurs_List: List<Ancien_ColorArticle>  ,
    val clients_List: List<Ancien_ClientsDataBase>
)

suspend fun get_Ancien_Datas(): Ancien_Datas_Resources {
    try {
        val produitsSnapshot = Firebase.database
            .getReference("e_DBJetPackExport")
            .get()
            .await()

        val soldArticlesSnapshot = Firebase.database
            .getReference("O_SoldArticlesTabelle")
            .get()
            .await()

        val couleurs_Snapshot = Firebase.database
            .getReference("H_ColorsArticles")
            .get()
            .await()

        val clients_Snapshot = Firebase.database
            .getReference("G_Clients")
            .get()
            .await()

        val produitsList = produitsSnapshot.children.mapNotNull {
            it.getValue(Ancien_Produits_DataBase::class.java)
        }

        val soldArticlesList = soldArticlesSnapshot.children.mapNotNull {
            it.getValue(Ancien_SoldArticlesTabelle::class.java)
        }

        val couleurs_List = couleurs_Snapshot.children.mapNotNull {
            it.getValue(Ancien_ColorArticle::class.java)
        }

        val clients_List = clients_Snapshot.children.mapNotNull {
            it.getValue(Ancien_ClientsDataBase::class.java)
        }

        return Ancien_Datas_Resources(
            produitsList,
            soldArticlesList,
            couleurs_List,
            clients_List
        )
    } catch (e: Exception) {
        Log.e("GetDatas", "Error fetching data from Firebase", e)
        throw e
    }
}
