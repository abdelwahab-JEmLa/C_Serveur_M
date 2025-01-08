package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.ArticleInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.ColourEtGoutInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.GrossistInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.Maps
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.TypePosition
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.init.startImplementationViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import java.util.AbstractMap

class ViewModel_Head : ViewModel() {
    var _maps by mutableStateOf(Maps())
    val maps: Maps get() = _maps

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                isLoading = true
                loadingProgress = 0f

                startImplementationViewModel(100){
                    loadingProgress= it.toFloat()
                }
                // Chargement des données
                loadingProgress = 0.5f

                // Récupération Firebase
                Firebase.database
                    .getReference("0_UiState_3_Host_Package_3_Prototype11Dec/Maps/mapGroToMapPositionToProduits")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        _maps.mapGroToMapPositionToProduits = snapshot.children.mapNotNull { grossistSnapshot ->
                            parseGrossistData(grossistSnapshot.value as? Map<*, *> ?: return@mapNotNull null)
                        }.toMutableList()

                        loadingProgress = 1f
                        isLoading = false
                    }

            } catch (e: Exception) {
                println("Erreur de chargement: ${e.message}")
                isLoading = false
            }
        }
    }

    private fun parseGrossistData(data: Map<*, *>): Map.Entry<GrossistInfosModel, Map<TypePosition, MutableList<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>>> {
        val grossistInfo = (data["grossistInfo"] as? Map<*, *>)?.let { info ->
            GrossistInfosModel(
                (info["id"] as? Number)?.toLong() ?: 0L,
                info["nom"] as? String ?: ""
            )
        } ?: GrossistInfosModel()

        val positionMap = mutableMapOf<TypePosition, MutableList<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>>()

        (data["products"] as? Map<*, *>)?.forEach { (positionKey, products) ->
            val position = TypePosition.valueOf(positionKey.toString())
            positionMap[position] = parseProducts(products)
        }

        return AbstractMap.SimpleEntry(grossistInfo, positionMap)
    }

    private fun parseProducts(products: Any?): MutableList<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>> {
        return (products as? List<*>)?.mapNotNull { productData ->
            (productData as? Map<*, *>)?.let { pData ->
                val articleInfo = parseArticleInfo(pData["articleInfo"] as? Map<*, *>)
                val colors = parseColors(pData["colors"] as? List<*>)
                AbstractMap.SimpleEntry(articleInfo, colors)
            }
        }?.toMutableList() ?: mutableListOf()
    }

    private fun parseArticleInfo(data: Map<*, *>?): ArticleInfosModel {
        return ArticleInfosModel(
            id = (data?.get("id") as? Number)?.toLong() ?: 0L,
            nom = data?.get("nom") as? String ?: ""
        )
    }

    private fun parseColors(colors: List<*>?): MutableList<Map.Entry<ColourEtGoutInfosModel, Double>> {
        return colors?.mapNotNull { colorData ->
            (colorData as? Map<*, *>)?.let { cData ->
                val colorInfo = (cData["colorInfo"] as? Map<*, *>)?.let { cInfo ->
                    ColourEtGoutInfosModel(
                        (cInfo["id"] as? Number)?.toLong() ?: 0L,
                        cInfo["nom"] as? String ?: "",
                        cInfo["imogi"] as? String ?: ""
                    )
                } ?: return@mapNotNull null
                AbstractMap.SimpleEntry(colorInfo, (cData["quantity"] as? Number)?.toDouble() ?: 0.0)
            }
        }?.toMutableList() ?: mutableListOf()
    }
}
