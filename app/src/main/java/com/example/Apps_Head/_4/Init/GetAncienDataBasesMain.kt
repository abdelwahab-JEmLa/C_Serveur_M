package com.example.Apps_Head._4.Init

import android.util.Log
import com.example.Apps_Head._1.Model.AncienResourcesDataBaseMain
import com.example.Apps_Head._1.Model.Ancien_ClientsDataBase_Main
import com.example.Apps_Head._1.Model.Ancien_ColorArticle_Main
import com.example.Apps_Head._1.Model.Ancien_SoldArticlesTabelle_Main
import com.example.Apps_Head._1.Model.ProduitsAncienDataBaseMain
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

internal suspend fun GetAncienDataBasesMain(): AncienResourcesDataBaseMain {
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
            it.getValue(ProduitsAncienDataBaseMain::class.java)
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

        return AncienResourcesDataBaseMain(
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
