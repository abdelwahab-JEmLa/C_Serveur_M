package com.example.Packages.Z.Archives.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductsCategoriesDataBase(
    @PrimaryKey(autoGenerate = true)
    val idCategorieInCategoriesTabele: Long = 0,
    val nomCategorieInCategoriesTabele: String = "",
    var idClassementCategorieInCategoriesTabele: Int = 0 ,
    var displayedHeader: Boolean = false,

    ) {
    constructor() : this(0)
}
