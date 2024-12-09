package com.example.serveurecherielhanaaebeljemla.Models

import com.example.Models.AppSettingsSaverModel
import com.example.Models.ClientsDataBase
import com.example.Models.DiviseurDeDisplayProductForEachClient
import com.example.Models.ProductsCategoriesDataBase
import com.example.Models.Produits_DataBase

/**
 * UI State that represents Fragment3_Main_Screen
 **/
data class UiStat(
    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val produitsDataBase: List<Produits_DataBase> = emptyList(),
    val clientsDataBase: List<ClientsDataBase> = emptyList(),
    val diviseurDeDisplayProductForEachClient: List<DiviseurDeDisplayProductForEachClient> = emptyList(),
    val productsCategoriesDataBase: List<ProductsCategoriesDataBase> = emptyList(),

    val isLoading: Boolean = true,
    val error: String? = null,
    val isInitialized: Boolean = false
)



