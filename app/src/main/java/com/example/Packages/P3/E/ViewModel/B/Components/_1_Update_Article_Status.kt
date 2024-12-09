package com.example.Packages.P3.E.ViewModel.B.Components

import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.Model.Commende_Produits_Au_Grossissts_DataBase
import com.example.Packages.P3.E.ViewModel.ViewModelFragment
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun ViewModelFragment.Update_Article_Status(
    article: Commende_Produits_Au_Grossissts_DataBase
) {
        viewModelScope.launch {
                _Ui_Statue_DataBase.update { currentState ->
                    val updatedArticles = currentState.commende_Produits_Au_Grossissts_DataBase.map {
                        if (it.vid == article.vid) article else it
                    }
                    currentState.copy(commende_Produits_Au_Grossissts_DataBase = updatedArticles)
                }

                _Ref_Grossist_Products_Achete.child(article.vid.toString()).apply {
                    child("itsInFindedAskSupplierSA").setValue(article.itsInFindedAskSupplierSA)
                    child("disponibylityStatInSupplierStore").setValue(article.disponibylityStatInSupplierStore)
                }
        }
}
fun ViewModelFragment.Move_Articles_To_Supplier(
    articlesToMove: List<Commende_Produits_Au_Grossissts_DataBase>,
    toSupp: Long
) {
        viewModelScope.launch {
                articlesToMove.forEach { article ->
                    _Ui_Statue_DataBase.update { currentState ->
                        val updatedArticles = currentState.commende_Produits_Au_Grossissts_DataBase.map {
                            if (it.vid == article.vid) {
                                it.copy(idSupplierTSA = toSupp.toInt(), itsInFindedAskSupplierSA = false)
                            } else it
                        }
                        currentState.copy(commende_Produits_Au_Grossissts_DataBase = updatedArticles)
                    }
                    _Ref_Grossist_Products_Achete.child(article.vid.toString()).apply {
                        child("idSupplierTSA").setValue(toSupp.toInt())
                        child("itsInFindedAskSupplierSA").setValue(false)
                    }
                    _Ref_Produit_DataBase.child(article.a_c_idarticle_c.toString()).apply {
                        child("lastIdSupplierChoseToBuy").setValue(toSupp)
                        child("dateLastIdSupplierChoseToBuy").setValue(currentDate)
                    }
                    _Produit_Actuelle_OnEditation.update { currentArticle ->
                        if (currentArticle?.idArticle?.toLong() == article.a_c_idarticle_c) {
                            currentArticle.copy(
                                lastIdSupplierChoseToBuy = toSupp,
                                dateLastIdSupplierChoseToBuy = currentDate
                            )
                        } else {
                            currentArticle
                        }
                    }
                }
        }
}
