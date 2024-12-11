package com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial

import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.Models.addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.Models.clear_Ui_Mutable_State_C_produits_Commend_DataBase
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal fun P3_ViewModel.addRandomTestProductReferences() {
    val randomProducts = List(200) { index ->
        Ui_Mutable_State.Groupeur_References_FireBase_DataBase.Produits_A_Update(
            id = Random.nextInt(500, 700).toLong(),
            position = index + 1,
            ref = "product_${index + 1}",
            nom = "Test Product ${index + 1}",
            tiggr_Time = System.currentTimeMillis()
        )
    }

    // Find the existing group reference
    val groupRef = _ui_Mutable_State.groupeur_References_FireBase_DataBase
        .firstOrNull { it.id == 1L || it.nom == "Produits_Commend_DataBase" }

    if (groupRef == null) {
        // Create a default group reference with the products
        val defaultGroupRef = Ui_Mutable_State.Groupeur_References_FireBase_DataBase(
            id = 1L,
            position = 1,
            ref = "Produits_Commend_DataBase",
            nom = "Produits_Commend_DataBase",
            description = "Default group for Ref products",
            last_Update_Time_Formatted = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            update_All = false
        )
        defaultGroupRef.updateFirebaseSelfF(defaultGroupRef)

    } else {

    }
}
internal fun P3_ViewModel.Test_Initiale_Calcules_Autre_Valeurs(
    itemCount: Int = 30
) {
    viewModelScope.launch(Dispatchers.Main) {
        val initialProduits_Commend_DataBase = List(itemCount) { i ->
            Ui_Mutable_State.Produits_Commend_DataBase(
                id = i + 1 + 700,
                nom = "Produit_Item #${i + 1}",
                non_Trouve = false,
                grossist_Choisi_Pour_Acheter_CeProduit = generate_Random_Supplier2(),
                colours_Et_Gouts_Commende = List(4) { generateRandomColoursEtGouts() }
            )
        }

        _ui_Mutable_State.clear_Ui_Mutable_State_C_produits_Commend_DataBase()
        _ui_Mutable_State.addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(initialProduits_Commend_DataBase)
        _ui_Mutable_State.namePhone = ""
        ref_ViewModel_Produit_DataBase.setValue(_ui_Mutable_State)
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

internal fun generate_Random_Supplier2(): Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit {
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
