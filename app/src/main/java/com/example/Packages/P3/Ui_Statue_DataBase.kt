package com.example.Packages.P3

import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.Model.Commende_Produits_Au_Grossissts_DataBase
import com.example.Models.Grossissts_DataBAse
import com.example.Models.Produits_DataBase
import com.example.Models.Res.ArticlesAcheteModele
import com.example.Models.Res.CategoriesTabelleECB
import com.example.Models.Res.ClientsList
import com.example.Models.Res.ColorsArticles
import com.example.Models.Res.DataBaseArticles
import com.example.Models.Res.MapArticleInSupplierStore
import com.example.Models.Res.PlacesOfArticelsInCamionette
import com.example.Models.Res.PlacesOfArticelsInEacheSupplierSrore
import com.example.Models.Res.SoldArticlesTabelle

data class Ui_Statue_DataBase internal constructor(
    val produits_DataBase: List<Produits_DataBase> = emptyList(),
    val historique_D_Achate_Grossisst_DataBase: List<Historique_D_Achate_Grossisst_DataBase> = emptyList(),
    val commende_Produits_Au_Grossissts_DataBase: List<Commende_Produits_Au_Grossissts_DataBase> = emptyList(),
    val categoriesECB: List<CategoriesTabelleECB> = emptyList(),
    val grossisst_Au_Filtre_Mnt: Grossissts_DataBAse?= null,
    val mode_click_is_trensfert_to_fab_gross: Boolean = false,

    val DataBaseArticles: List<DataBaseArticles> = emptyList(),
    val colorsArticles: List<ColorsArticles> = emptyList(),
    val articlesAcheteModele: List<ArticlesAcheteModele> = emptyList(),
    val soldArticlesTabelle: List<SoldArticlesTabelle> = emptyList(),
    val grossissts_DataBAse: List<Grossissts_DataBAse> = emptyList(),
    val mapArticleInSupplierStore: List<MapArticleInSupplierStore> = emptyList(),
    val placesOfArticelsInEacheSupplierSrore: List<PlacesOfArticelsInEacheSupplierSrore> = emptyList(),
    val placesOfArticelsInCamionette: List<PlacesOfArticelsInCamionette> = emptyList(),
    val clientsList: List<ClientsList> = emptyList(),
    val showOnlyWithFilter: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)
