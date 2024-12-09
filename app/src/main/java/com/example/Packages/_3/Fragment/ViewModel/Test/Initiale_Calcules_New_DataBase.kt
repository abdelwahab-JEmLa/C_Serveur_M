package com.example.Packages._3.Fragment.ViewModel.Test

import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.Models.addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.Models.clear_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

internal fun P3_ViewModel.Test_Initiale_Calcules_Autre_Valeurs(
    itemCount: Int = 30
) {
    viewModelScope.launch(Dispatchers.Main) {
        val initialProduits_Commend_DataBase = List(itemCount) { i ->
            Ui_Mutable_State.Produits_Commend_DataBase(
                id = i + 1 + 700,
                nom = "Produit_Item #${i + 1}",
                non_Trouve = false,
                grossist_Choisi_Pour_Acheter_CeProduit = generateRandomSupplier(),
                colours_Et_Gouts_Commende = List(4) { generateRandomColoursEtGouts() }
            )
        }

        _ui_Mutable_State.clear_Ui_Mutable_State_C_produits_Commend_DataBase()
        _ui_Mutable_State.addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(initialProduits_Commend_DataBase)
        _ui_Mutable_State.namePhone = ""
        refFirebase.setValue(_ui_Mutable_State)
    }
}

internal fun generateRandomColoursEtGouts(): Ui_Mutable_State.Produits_Commend_DataBase.Colours_Et_Gouts_Commende {
    val flavorsAndColors = listOf(
        "Citron" to "",
        "Standard" to "🎨",
        "Orange" to "🍊",
        "Pomme" to "🍎",
        "Vanille" to "",
        "Chocolat" to "🍫"
    )

    val randomPair = flavorsAndColors.random()

    return Ui_Mutable_State.Produits_Commend_DataBase.Colours_Et_Gouts_Commende(
        position_Du_Couleur_Au_Produit = Random.nextLong(1, 4),
        id_Don_Tout_Couleurs = Random.nextLong(1, 100), // Added a random ID
        nom = randomPair.first,
        quantity_Achete = if (Random.nextInt(0, 10) < 5) 0 else Random.nextInt(0, 10),
        imogi = randomPair.second
    )
}

internal fun generateRandomSupplier(): Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit {
    val colors = listOf(
        "#FF5733", "#33FF57", "#3357FF", "#FF33F1",
        "#33FFF1", "#F1FF33", "#8E44AD", "#3498DB"
    )

    val randomNum = Random.nextInt(0, 6) // Including 0 as a possibility
    val position = when {
        randomNum == 0 -> 0 // Explicitly set position to 0 for undefined suppliers
        Random.nextBoolean() -> -Random.nextInt(1, 5) // Randomly make some positions negative
        else -> Random.nextInt(1, 5) // Positive positions
    }

    return Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit(
        id = randomNum.toLong(),
        nom = if (randomNum == 0) "Undefined Supplier" else "Grossiste $randomNum",
        position_Grossist_Don_Parent_Grossists_List = position,
        couleur = colors.random(),
        currentCreditBalance = Random.nextDouble(0.0, 10000.0),
        position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = position
    )
}
