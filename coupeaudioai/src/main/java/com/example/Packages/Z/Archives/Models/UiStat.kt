package com.example.serveurecherielhanaaebeljemla.Models

import com.example.Packages.Z.Archives.Models.AppSettingsSaverModel
import com.example.Packages.Z.Archives.Models.ClientsDataBase
import com.example.Packages.Z.Archives.Models.DiviseurDeDisplayProductForEachClient
import com.example.Packages.Z.Archives.Models.ProductsCategoriesDataBase
import com.example.Packages.Z.Archives.Models.Produits_DataBase

/**
 * UI State that represents Main_Screen_Fragment
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



