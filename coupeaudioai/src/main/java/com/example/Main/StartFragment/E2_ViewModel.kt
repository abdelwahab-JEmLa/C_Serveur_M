package com.example.Main.StartFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coupeaudioai.Modules.Z.Archives.AppDatabase
import com.example.coupeaudioai.Modules.Z.Archives.FireBaseHandler
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


open class StartFragmentViewModel(
    context: Context,
    private val database: AppDatabase,
) : ViewModel() {
    // État UI
    private val _stateFlow = MutableStateFlow(UiStat())
    val state: StateFlow<UiStat> = _stateFlow.asStateFlow()
    private val fireBaseHandler = FireBaseHandler(database)

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
                            database.productsDataBaseDao().upsert(updatedProduct)

                            fireBaseHandler.updateFirebaseProductsDataBase(updatedProduct.idArticle.toString(),updatedProduct)
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

    fun importClientsDataBase() {
        viewModelScope.launch {
            try {
                fireBaseHandler.importClientsDataBase()
                database.historique_D_Achate_Grossisst_DataBase_Dao().deleteAll()
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Error importing clients: ${e.message}"
                    )
                }
            }
        }
    }

}
