package com.example.Apps_Head._4.Init.Z.Components

import android.util.Log
import com.example.Apps_Head._4.Init.Ancien_ClientsDataBase_Main
import com.example.Apps_Head._4.Init.Ancien_ColorArticle_Main
import com.example.Apps_Head._4.Init.Ancien_SoldArticlesTabelle_Main
import com.example.Apps_Head._4.Init.Produits_Ancien_DataBase_Main
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class Ancien_Resources_DataBase_Main(
    val produitsDatabase: List<Produits_Ancien_DataBase_Main>,
    val soldArticles: List<Ancien_SoldArticlesTabelle_Main>,
    val couleurs_List: List<Ancien_ColorArticle_Main>,
    val clients_List: List<Ancien_ClientsDataBase_Main>
)

internal suspend fun get_Ancien_DataBases_Main(): Ancien_Resources_DataBase_Main {
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
            it.getValue(Produits_Ancien_DataBase_Main::class.java)
        }

        val soldArticlesList = soldArticlesSnapshot.children.mapNotNull {
            it.getValue(Ancien_SoldArticlesTabelle_Main::class.java)
        }

        val couleurs_List = couleurs_Snapshot.children.mapNotNull {
            it.getValue(Ancien_ColorArticle_Main::class.java)
        }

        val clients_List = clients_Snapshot.children.mapNotNull {
            it.getValue(Ancien_ClientsDataBase_Main::class.java)
        }

        return Ancien_Resources_DataBase_Main(
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
