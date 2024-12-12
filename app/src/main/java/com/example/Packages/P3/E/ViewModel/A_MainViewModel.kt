package com.example.Packages.P3.E.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Models.Grossissts_DataBAse
import com.example.Models.Grossissts_DataBAse.Companion.init_Collect_Grossissts_DataBAse
import com.example.Models.Res.ArticlesAcheteModele
import com.example.Models.Res.ClientsList
import com.example.Models.Res.ColorsArticles
import com.example.Models.Res.DataBaseArticles
import com.example.Models.Res.MapArticleInSupplierStore
import com.example.Models.Res.PlacesOfArticelsInCamionette
import com.example.Models.Res.PlacesOfArticelsInEacheSupplierSrore
import com.example.Models.Res.SoldArticlesTabelle
import com.example.Packages.P3.Historique_D_Achate_Grossisst_DataBase.Companion.initializeData
import com.example.Packages.P3.Ui_Statue_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Commende_Produits_Au_Grossissts_DataBase
import com.example.clientjetpack.Modules.AppDatabase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewModelFragment internal constructor(
    context: Context,
    val dataBase: AppDatabase
) : ViewModel() {
    val _Ui_Statue_DataBase = MutableStateFlow(Ui_Statue_DataBase())
    val Ui_State_DataBase = _Ui_Statue_DataBase.asStateFlow()

    val _Suppliers_DataBase = _Ui_Statue_DataBase.value.grossissts_DataBAse
    val _Produits_DATABASE = _Ui_Statue_DataBase.value.produits_DataBase
    val _Historique_D_Achate_Grossisst_DataBase = _Ui_Statue_DataBase.value.historique_D_Achate_Grossisst_DataBase
    val commende_Au_Grossissts_DataBase = _Ui_Statue_DataBase.value.commende_Produits_Au_Grossissts_DataBase

    val _Produit_Actuelle_OnEditation = MutableStateFlow<DataBaseArticles?>(null)
    val currentEditedArticle: StateFlow<DataBaseArticles?> = _Produit_Actuelle_OnEditation.asStateFlow()



    var totalSteps = 100 // Total number of steps for the timer
    var currentStep = 0 // Current step in the process

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    val _Ref_Produit_DataBase = firebaseDatabase.getReference("e_DBJetPackExport")

    private val _Ref_Colors_Articles = firebaseDatabase.getReference("H_ColorsArticles")
    private val refArticlesAcheteModele = firebaseDatabase.getReference("ArticlesAcheteModeleAdapted")
    private val refSoldArticlesTabelle = firebaseDatabase.getReference("O_SoldArticlesTabelle")
    val _Ref_Grossist_Products_Achete = firebaseDatabase.getReference("K_SupplierArticlesRecived")
    val refTabelleSuppliersSA = firebaseDatabase.getReference("F_Suppliers")
    private val refMapArticleInSupplierStore = firebaseDatabase.getReference("L_MapArticleInSupplierStore")
    private val refPlacesOfArticelsInEacheSupplierSrore = firebaseDatabase.getReference("M_PlacesOfArticelsInEacheSupplierSrore")
    private val refPlacesOfArticelsInCamionette = firebaseDatabase.getReference("N_PlacesOfArticelsInCamionette")
    private val refClientsList = firebaseDatabase.getReference("G_Clients")

    val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    companion object {
        private const val TAG = "HeadOfViewModels"
    }
    init {
        initializeData()
        init_Collect_Grossissts_DataBAse()
    }
    private fun Update_Upload_Progress_Bar_Counter_And_It_Text(nameFunInProgressBar: String = "", addPLusTOCurrentStep: Int, stepProgress: Float = 100f, delayUi: Long = 0) {}

    private  fun finalize() {
        Update_Upload_Progress_Bar_Counter_And_It_Text("Finalizing Update", ++currentStep, 0f)
        repeat(20) {
            val progress = (it + 1).toFloat() / 20 * 100f
            Update_Upload_Progress_Bar_Counter_And_It_Text("Finalizing Update", currentStep, progress, 50)
        }
        Update_Upload_Progress_Bar_Counter_And_It_Text("Update Complete", totalSteps, 100f)
    }


    private fun handleError(message: String, exception: Exception) {
    }


    /**inti*/

    fun intialaizeArticlesCommendToSupplierFromClientNeed() {
        val TAG = "SupplierCommand"

        viewModelScope.launch {
            try {
                _Ref_Grossist_Products_Achete.get().addOnSuccessListener { supplierSnapshot ->
                    val currentMaxVid = supplierSnapshot.children
                        .mapNotNull { it.key?.toLongOrNull() }
                        .maxOrNull() ?: 0

                    _Ref_Grossist_Products_Achete.removeValue()

                    refSoldArticlesTabelle.get().addOnSuccessListener { snapshot ->

                        val soldArticles = snapshot.children.mapNotNull {
                            it.getValue(SoldArticlesTabelle::class.java)
                        }

                        val groupedMap = soldArticles.groupBy { it.idArticle }
                        val groupedArticles = groupedMap.entries.withIndex().map { (index, entry) ->
                            val articleId = entry.key
                            val articles = entry.value
                            val firstArticle = articles.first()

                            val clientIdsList = articles.map { it.clientSoldToItId }.distinct()
                            val clientIdsString = clientIdsList.joinToString(",")
                            val clientNamesString = clientIdsList
                                .mapNotNull { clientId ->
                                    _Ui_Statue_DataBase.value.clientsList.find { it.idClientsSu == clientId }?.nomClientsSu
                                }
                                .joinToString(",")

                            Commende_Produits_Au_Grossissts_DataBase(
                                vid = currentMaxVid + index + 1,
                                a_c_idarticle_c = articleId,
                                nameArticle = firstArticle.nameArticle,
                                idsClientsNeedItGBC = clientNamesString,
                                nameClientsNeedItGBC = clientIdsString,
                                color1SoldQuantity = articles.sumOf { it.color1SoldQuantity },
                                color2SoldQuantity = articles.sumOf { it.color2SoldQuantity },
                                color3SoldQuantity = articles.sumOf { it.color3SoldQuantity },
                                color4SoldQuantity = articles.sumOf { it.color4SoldQuantity }
                            )
                        }

                        // Process with intiAuthersFieldFromAuthersModels before saving
                        val completeGroupedArticles = intiAuthersFieldFromAuthersModels(groupedArticles)

                        completeGroupedArticles.forEach { articleData ->
                            _Ref_Grossist_Products_Achete
                                .child(articleData.vid.toString())
                                .setValue(articleData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Successfully saved grouped article: ${articleData.nameArticle} with VID: ${articleData.vid}")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to save grouped article: ${articleData.nameArticle} with VID: ${articleData.vid}", e)
                                }
                        }

                        Log.d(TAG, "Completed supplier command creation: ${completeGroupedArticles.size} grouped articles saved")
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Failed to fetch sold articles data", e)
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Failed to fetch current max VID", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating supplier commands", e)
                _Ui_Statue_DataBase.update { it.copy(error = "Error creating supplier commands: ${e.message}") }
            }
        }
    }
    private fun intiAuthersFieldFromAuthersModels(groupedArticles: List<Commende_Produits_Au_Grossissts_DataBase>): List<Commende_Produits_Au_Grossissts_DataBase> {
        return groupedArticles.map { article ->
            // Find corresponding article in database
            val correspondingArticle = _Ui_Statue_DataBase.value.DataBaseArticles.find {
                it.idArticle.toLong() == article.a_c_idarticle_c
            }

            // Find color names from ColorsArticles
            val color1Name = correspondingArticle?.let { baseArticle ->
                _Ui_Statue_DataBase.value.colorsArticles.find { it.idColore == baseArticle.idcolor1 }?.nameColore ?: ""
            } ?: ""

            val color2Name = correspondingArticle?.let { baseArticle ->
                _Ui_Statue_DataBase.value.colorsArticles.find { it.idColore == baseArticle.idcolor2 }?.nameColore ?: ""
            } ?: ""

            val color3Name = correspondingArticle?.let { baseArticle ->
                _Ui_Statue_DataBase.value.colorsArticles.find { it.idColore == baseArticle.idcolor3 }?.nameColore ?: ""
            } ?: ""

            val color4Name = correspondingArticle?.let { baseArticle ->
                _Ui_Statue_DataBase.value.colorsArticles.find { it.idColore == baseArticle.idcolor4 }?.nameColore ?: ""
            } ?: ""

            // Calculate total quantity
            val totalQuantity = article.color1SoldQuantity +
                    article.color2SoldQuantity +
                    article.color3SoldQuantity +
                    article.color4SoldQuantity


            article.copy(
                idSupplierTSA = generate(correspondingArticle),
                datedachate = currentDate,
                totalquantity = totalQuantity,
                disponibylityStatInSupplierStore = "",
                itsInFindedAskSupplierSA = false,
                a_d_nomarticlefinale_c_1 = color1Name,
                a_d_nomarticlefinale_c_2 = color2Name,
                a_d_nomarticlefinale_c_3 = color3Name,
                a_d_nomarticlefinale_c_4 = color4Name
            )
        }
    }
    private fun generate(correspondingArticle: DataBaseArticles?): Int {
        if (correspondingArticle == null) {
            return 10 // Default value if correspondingArticle is null
        }

        val lastSupplierIdBuyedFrom = correspondingArticle.lastSupplierIdBuyedFrom
        val lastIdSupplierChoseToBuy = correspondingArticle.lastIdSupplierChoseToBuy

        // Parse dates, defaulting to epoch time if parsing fails
        val dateLastSupplierIdBuyedFrom = correspondingArticle.dateLastSupplierIdBuyedFrom.let {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
            } catch (e: Exception) {
                0L
            }
        } ?: 0L

        val dateLastIdSupplierChoseToBuy = correspondingArticle.dateLastIdSupplierChoseToBuy.let {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
            } catch (e: Exception) {
                0L
            }
        } ?: 0L

        return when {
            dateLastSupplierIdBuyedFrom > dateLastIdSupplierChoseToBuy -> lastSupplierIdBuyedFrom.toInt()
            dateLastIdSupplierChoseToBuy > dateLastSupplierIdBuyedFrom -> lastIdSupplierChoseToBuy.toInt()
            lastSupplierIdBuyedFrom.toInt() != 0 -> lastSupplierIdBuyedFrom.toInt()
            lastIdSupplierChoseToBuy.toInt() != 0 -> lastIdSupplierChoseToBuy.toInt()
            else -> 10 // Default value if both IDs are 0
        }
    }

    init {
        viewModelScope.launch {
            initDataFromFirebase()
        }
    }



    private suspend fun initDataFromFirebase() {
        try {


            Update_Upload_Progress_Bar_Counter_And_It_Text("Starting data fetch", ++currentStep, 0f)

            val articles = fetchArticles()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched articles", ++currentStep, 100f)

           // val categories = categoriesDao.getAllCategoriesList()
    //        Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched articles", ++currentStep, 100f)

            val colorsArticles = fetchColorsArticles()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched colors", ++currentStep, 100f)

            val supplierArticlesRecived = fetchSupplierArticles()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched supplier articles", ++currentStep, 100f)

            val suppliersSA = fetchSuppliers()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched suppliers", ++currentStep, 100f)

            val mapArticleInSupplierStore = fetchMapArticleInSupplierStore()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched article map", ++currentStep, 100f)

            val placesOfArticelsInEacheSupplierSrore = fetchPlacesOfArticelsInEacheSupplierSrore()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched supplier store places", ++currentStep, 100f)

            val placesOfArticelsInCamionette = fetchPlacesOfArticelsInCamionette()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched camionette places", ++currentStep, 100f)

            val articlesAcheteModele = fetchArticlesAcheteModele()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched purchased articles", ++currentStep, 100f)

            val soldArticlesTabelle = fetchSoldArticlesTabelle()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched purchased articles", ++currentStep, 100f)


            val clientsList = fetchClientsList()
            Update_Upload_Progress_Bar_Counter_And_It_Text("Fetched clients", ++currentStep, 100f)




            updateUiState(
                articles, supplierArticlesRecived, suppliersSA,
                mapArticleInSupplierStore, placesOfArticelsInEacheSupplierSrore,
                placesOfArticelsInCamionette, articlesAcheteModele, colorsArticles, clientsList,soldArticlesTabelle
            )
            Update_Upload_Progress_Bar_Counter_And_It_Text("Data fetch complete", totalSteps, 100f)
        } catch (e: Exception) {
            handleError("Failed to load data from Firebase", e)
        } finally {
            _Ui_Statue_DataBase.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun fetchArticles() = try {
        _Ref_Produit_DataBase.get().await().children.mapNotNull { snapshot ->
            snapshot.getValue(DataBaseArticles::class.java)?.apply {
                idArticle = snapshot.key?.toIntOrNull() ?: 0
            }
        }.also {  }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchColorsArticles() = try {
        _Ref_Colors_Articles.get().await().children
            .mapNotNull { it.getValue(ColorsArticles::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchSuppliers() = try {
        refTabelleSuppliersSA.get().await().children
            .mapNotNull { it.getValue(Grossissts_DataBAse::class.java) }
            .sortedBy{ it.classmentSupplier }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchMapArticleInSupplierStore() = try {
        refMapArticleInSupplierStore.get().await().children
            .mapNotNull { it.getValue(MapArticleInSupplierStore::class.java) }
            .sortedBy { it.itClassement }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchSupplierArticles() = try {
        _Ref_Grossist_Products_Achete.get().await().children
            .mapNotNull { it.getValue(Commende_Produits_Au_Grossissts_DataBase::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchPlacesOfArticelsInEacheSupplierSrore() = try {
        refPlacesOfArticelsInEacheSupplierSrore.get().await().children
            .mapNotNull { it.getValue(PlacesOfArticelsInEacheSupplierSrore::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchPlacesOfArticelsInCamionette() = try {
        refPlacesOfArticelsInCamionette.get().await().children
            .mapNotNull { it.getValue(PlacesOfArticelsInCamionette::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun fetchArticlesAcheteModele() = try {
        refArticlesAcheteModele.get().await().children
            .mapNotNull { it.getValue(ArticlesAcheteModele::class.java) }
    } catch (e: Exception) {
        emptyList()
    }
    private suspend fun fetchSoldArticlesTabelle() = try {
        refSoldArticlesTabelle.get().await().children
            .mapNotNull { it.getValue(SoldArticlesTabelle::class.java) }
    } catch (e: Exception) {
        emptyList()
    }
    private suspend fun fetchClientsList() = try {
        refClientsList.get().await().children
            .mapNotNull { it.getValue(ClientsList::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    private fun updateUiState(
        articles: List<DataBaseArticles>,
        supplierArticlesRecived: List<Commende_Produits_Au_Grossissts_DataBase>,
        suppliersSA: List<Grossissts_DataBAse>,
        mapArticleInSupplierStore: List<MapArticleInSupplierStore>,
        placesOfArticelsInEacheSupplierSrore: List<PlacesOfArticelsInEacheSupplierSrore>,
        placesOfArticelsInCamionette: List<PlacesOfArticelsInCamionette>,
        articlesAcheteModele: List<ArticlesAcheteModele>,
        colorsArticles: List<ColorsArticles>,
        clientsList: List<ClientsList>,
        soldArticlesTabelle: List<SoldArticlesTabelle>,

        ) {
        _Ui_Statue_DataBase.update { it.copy(
            DataBaseArticles = articles,
            commende_Produits_Au_Grossissts_DataBase = supplierArticlesRecived,
            grossissts_DataBAse = suppliersSA,
            mapArticleInSupplierStore = mapArticleInSupplierStore,
            placesOfArticelsInEacheSupplierSrore = placesOfArticelsInEacheSupplierSrore,
            placesOfArticelsInCamionette=placesOfArticelsInCamionette,
            articlesAcheteModele =articlesAcheteModele,
            soldArticlesTabelle =soldArticlesTabelle,
            colorsArticles =colorsArticles,
            clientsList =clientsList  ,
            isLoading = false
        ) }
    }
}


