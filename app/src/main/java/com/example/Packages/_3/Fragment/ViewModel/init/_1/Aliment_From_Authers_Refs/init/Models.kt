package com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.init

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Produits_DataBase(
    @PrimaryKey(autoGenerate = true)
    var idArticle: Long = 0,
    var nomArticleFinale: String = "",
    var classementCate: Double = 0.0,
    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var nmbrCat: Int = 0,
    var couleur1: String? = null,
    var idcolor1: Long = 0,
    var couleur2: String? = null,
    var idcolor2: Long = 0,
    var couleur3: String? = null,
    var idcolor3: Long = 0,
    var couleur4: String? = null,
    var idcolor4: Long = 0,
    var articleHaveUniteImages: Boolean=false,
    var nomCategorie2: String? = null,
    var nmbrUnite: Int = 0,
    var nmbrCaron: Int = 0,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var monPrixAchat: Double = 0.0,
    var clienPrixVentUnite: Double = 0.0,
    var minQuan: Int = 0,
    var monBenfice: Double = 0.0,
    var monPrixVent: Double = 0.0,
    var diponibilityState: String = "",
    var neaon2: String = "",
    var idCategorie: Double = 0.0,
    var idArticlePlaceInCamionette: Long = 0,
    var funChangeImagsDimention: Boolean = false,
    var idCategorieNewMetode: Long = 0,
    var articleItIdClassementInItCategorieInHVM: Long = 0,
    var nomCategorie: String = "",
    var idPlaceStandartInStoreSupplier: Long = 0,
    var neaon1: Double = 0.0,
    var lastUpdateState: String = "",
    var lastSupplierIdBuyedFrom: Long = 0,
    var dateLastSupplierIdBuyedFrom: String = "",
    var lastIdSupplierChoseToBuy: Long = 0,
    var dateLastIdSupplierChoseToBuy: String = "",
    var cartonState: String = "",
    var dateCreationCategorie: String = "",
    var prixDeVentTotaleChezClient: Double = 0.0,
    var benficeTotaleEntreMoiEtClien: Double = 0.0,
    var benificeTotaleEn2: Double = 0.0,
    var monPrixAchatUniter: Double = 0.0,
    var monPrixVentUniter: Double = 0.0,
    var benificeClient: Double = 0.0,
    var monBeneficeUniter: Double = 0.0,
    var itsNewArrivale: Boolean = false,
    var imageDimention: String = "",
    var pret_pour_deplace_au_grossisst: Boolean = false,
    var id_De_Dernier_Grossisst_Choisi: Long = 0,
    var id_De_Avant_Dernier_Grossisst_Choisi_Pour_Si_Evite: Long = 0,
) {
    constructor() : this(0)
}

@Entity
data class Commende_Produits_Au_Grossissts_DataBase(
    @PrimaryKey(autoGenerate = true)
    val vid: Long = 0,
    var a_c_idarticle_c: Long = 0,
    val nameArticle: String = "",
    var idSupplierTSA: Int = 0,
    var nomSupplierTSA: String? = null,
    var idInStoreOfSupp: Long = 0,
    var nmbrCat: Int = 0,
    val trouve_c: Boolean = false,
    val a_u_prix_1_q1_c: Double = 0.0,
    var a_q_prixachat_c: Double = 0.0,
    val a_l_nmbunite_c: Int = 0,
    val a_r_prixdevent_c: Double = 0.0,
    val idsClientsNeedItGBC: String = "",
    val nameClientsNeedItGBC: String = "",
    val datedachate: String = "",
    val a_d_nomarticlefinale_c_1: String = "",
    val color1SoldQuantity: Int = 0,
    val a_d_nomarticlefinale_c_2: String = "",
    val color2SoldQuantity: Int = 0,
    val a_d_nomarticlefinale_c_3: String = "",
    val color3SoldQuantity: Int = 0,
    val a_d_nomarticlefinale_c_4: String = "",
    val color4SoldQuantity: Int = 0,
    val totalquantity: Int = 0,
    var itsInFindedAskSupplierSA: Boolean = false,
    var disponibylityStatInSupplierStore: String = "",
    var pret_pour_deplace_au_grossisst: Boolean = false,
) {
    constructor() : this(0L)
}

// File: ColorArticle.kt - Assurez-vous que cette classe correspond exactement à la structure de votre base de données
data class ColorArticle(
    val idColore: Long = 0,
    val nameColore: String = "",
    val iconColore: String = "",
    var classementColore: Int = 0
) {
    // Firebase necessite un constructeur vide
    constructor() : this(0, "", "", 0)
}
