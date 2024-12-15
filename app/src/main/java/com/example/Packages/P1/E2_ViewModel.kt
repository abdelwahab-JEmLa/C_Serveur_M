package com.example.Packages.P1

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Models.ClientsDataBase
import com.example.Models.DiviseurDeDisplayProductForEachClient
import com.example.Models.Produits_DataBase
import com.example.clientjetpack.Modules.AppDatabase
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


open class ClientProductsDisplayerStatsViewModel(
    context: Context,
    private val database: AppDatabase,
) : ViewModel() {
    // État UI
    private val _stateFlow = MutableStateFlow(UiStat())
    val state: StateFlow<UiStat> = _stateFlow.asStateFlow()
    // Firebase Setup
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val refAppSettingsSaverModel = firebaseDatabase.getReference("2_AppSettingsSaverNew")
    private val refClientsDataBase = firebaseDatabase.getReference("G_Clients")
    private val refProductsDataBase = firebaseDatabase.getReference("e_DBJetPackExport")
    private val diviseurDeDisplayProductForEachClientRef = firebaseDatabase.getReference("3_DiviseurDeDisplayProductForEachClient")

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val appSettingsSaverModelDao = database.appSettingsSaverModelDao()
    private val clientsDataBaseDao = database.clientsDataBaseDao()
    private val productsDataBaseDao = database.productsDataBaseDao()
    private val diviseurDeDisplayProductForEachClientDao = database.diviseurDeDisplayProductForEachClientDao()
    private val productsCategoriesDataBaseDao = database.productsCategoriesDataBaseDao()

    fun updateProductCategoryReferences() {
        viewModelScope.launch {
            try {
                // Get all products and product categories
                val products = _stateFlow.value.produitsDataBase
                val categories = _stateFlow.value.productsCategoriesDataBase

                // Iterate through products and match categories
                products.forEach { product ->
                    val matchedCategory = categories.find {
                        it.nomCategorieInCategoriesTabele == product.nomCategorie
                    }

                    matchedCategory?.let { category ->
                        // If a matching category is found, update the product's category ID
                        if (product.idCategorieNewMetode != category.idCategorieInCategoriesTabele) {
                            val updatedProduct = product.copy(
                                idCategorieNewMetode = category.idCategorieInCategoriesTabele
                            )

                            // Update in local database
                            productsDataBaseDao.upsert(updatedProduct)

                            updateFirebaseProductsDataBase(updatedProduct.idArticle.toString(),updatedProduct)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur mise à jour références catégories: ${e.message}"
                    )
                }
            }
        }
    }
    fun deleteDiviseurDeDisplayProductForEachClient(
        productId: Long,
        clientId: Long
    ) {
        viewModelScope.launch {
            try {
                val keyVidClientProductDisplayStat = "$clientId->$productId"
                val statToDelete = _stateFlow.value.diviseurDeDisplayProductForEachClient.find {
                    it.productId == productId && it.idClientsSu == clientId
                }

                statToDelete?.let {
                    // Delete from local database
                    diviseurDeDisplayProductForEachClientDao.delete(it)

                    // Delete from Firebase
                    diviseurDeDisplayProductForEachClientRef.child(keyVidClientProductDisplayStat).removeValue()
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            _stateFlow.update {
                                it.copy(
                                    error = "Erreur suppression stat Firebase: ${e.message}"
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur suppression ClientsProductDisplayeStat: ${e.message}"
                    )
                }
            }
        }
    }
    fun upsertDiviseurDeDisplayProductForEachClient(
        productId: Long,
        clientId: Long,
        deniedFromDisplayToClient: Boolean,
        itsBigImage: Boolean
    ) {
        viewModelScope.launch {
            try {
                // Generate key for Firebase
                val keyVidClientProductDisplayStat = "$clientId->$productId"

                // Find existing stat or create a new one
                val existingStat = _stateFlow.value.diviseurDeDisplayProductForEachClient.find {
                    it.productId == productId && it.idClientsSu == clientId
                }

                if (existingStat != null) {
                    // Update existing stat with provided values or toggle existing values
                    val updatedStat = existingStat.copy(
                        deniedFromDislplayToClient = deniedFromDisplayToClient,
                        itsBigImage = itsBigImage
                    )

                    // Update in local database
                    diviseurDeDisplayProductForEachClientDao.update(updatedStat)

                    // Update in Firebase
                    updateFirebaseDiviseurDeDisplayProductForEachClient(keyVidClientProductDisplayStat, updatedStat)
                } else {
                    // Create new stat
                    val product = _stateFlow.value.produitsDataBase.find { it.idArticle == productId }
                    val client = _stateFlow.value.clientsDataBase.find { it.idClientsSu == clientId }

                    val newStat = DiviseurDeDisplayProductForEachClient(
                        keyVid = keyVidClientProductDisplayStat,
                        idClientsSu = clientId,
                        nomClientsSu = client?.nomClientsSu ?: "Unknown Client",
                        productId = productId,
                        productName = product?.nomArticleFinale ?: "Unknown Product",
                        itsBigImage = itsBigImage   ,
                        deniedFromDislplayToClient = deniedFromDisplayToClient
                    )

                    // Insert in local database
                    diviseurDeDisplayProductForEachClientDao.insert(newStat)

                    // Update in Firebase
                    updateFirebaseDiviseurDeDisplayProductForEachClient(keyVidClientProductDisplayStat, newStat)
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur upsert ClientsProductDisplayeStat: ${e.message}"
                    )
                }
            }
        }
    }


    fun updatenameAggregation(clientId: Long, newnameAggregation: String) {
        viewModelScope.launch {
            try {
                val client = _stateFlow.value.clientsDataBase.find { it.idClientsSu == clientId }
                client?.let {
                    val updatedClient = it.copy( nameAggregation = newnameAggregation)
                    clientsDataBaseDao.update(updatedClient)
                    // Add Firebase update
                    updateFirebaseClients(updatedClient)
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur mise à jour nom du client: ${e.message}"
                    )
                }
            }

        }
    }
    // Add this method to the ClientProductsDisplayerStatsViewModel
    fun updateClientReadyForEdit(clientId: Long) {
        viewModelScope.launch {
            try {
                val client = _stateFlow.value.clientsDataBase.find { it.idClientsSu==clientId }
                client?.let {
                    val updatedClient = it.copy(itsReadyForEdite = !it.itsReadyForEdite)

                    clientsDataBaseDao.update(updatedClient)
                    updateFirebaseClients(updatedClient)

                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur mise à jour client: ${e.message}"
                    )
                }
            }
        }
    }

    private fun updateFirebaseProductsDataBase(
        key: String,
        stat: Produits_DataBase
    ) {
        refProductsDataBase.child(key)
            .setValue(stat)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                _stateFlow.update {
                    it.copy(
                        error = "Erreur mise à jour stat Firebase: ${e.message}"
                    )
                }
            }
    }

    private fun updateFirebaseDiviseurDeDisplayProductForEachClient(
        key: String="",
        stat: DiviseurDeDisplayProductForEachClient
    ) {
        diviseurDeDisplayProductForEachClientRef.child(key)
            .setValue(stat)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                _stateFlow.update {
                    it.copy(
                        error = "Erreur mise à jour stat Firebase: ${e.message}"
                    )
                }
            }
    }

    private fun updateFirebaseClients(client: ClientsDataBase) {
        refClientsDataBase.child(client.idClientsSu.toString()).setValue(client)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                _stateFlow.update {
                    it.copy(
                        error = "Erreur mise à jour Firebase: ${e.message}"
                    )
                }
            }
    }

    /**
     * Configure l'écouteur Firebase
     */
    init {
        initializeData()
    }
    /**
     * Initialise_ViewModel les données
     */
    private fun initializeData() {
        viewModelScope.launch {
            try {
                // Collecteur AppSettingsSaverModel
                launch {
                    appSettingsSaverModelDao.getAllFlow().collect { appSettingsSaverModel ->
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                appSettingsSaverModel = appSettingsSaverModel,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
                // Collecteur
                launch {
                    clientsDataBaseDao.getAllFlow().collect {
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                clientsDataBase = it,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
                // Collecteur
                launch {
                    productsDataBaseDao.getAllFlow().collect {
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                produitsDataBase = it,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
                launch {
                    diviseurDeDisplayProductForEachClientDao.getAllFlow().collect {
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                diviseurDeDisplayProductForEachClient = it,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
                launch {
                    productsCategoriesDataBaseDao.getAllFlow().collect {
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                productsCategoriesDataBase = it,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur initialisation: ${e.message}",
                        isInitialized = false
                    )
                }
            }
        }
    }


}


