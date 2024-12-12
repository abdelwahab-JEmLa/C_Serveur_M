package com.example.Packages._3.Fragment.ViewModel._2.Init

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.UiState
import kotlin.random.Random


internal fun generate_Random_Supplier2(): SnapshotStateList<UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit> {
    val colors = listOf(
        "#FF5733", "#33FF57", "#3357FF", "#FF33F1",
        "#33FFF1", "#F1FF33", "#8E44AD", "#3498DB"
    )

    return List(Random.nextInt(1, 3)) {
        val randomNum = Random.nextInt(0, 6) // Including 0 as a possibility
        val position = when {
            randomNum == 0 -> 0 // Explicitly set position to 0 for undefined suppliers
            Random.nextBoolean() -> -Random.nextInt(1, 5) // Randomly make some positions negative
            else -> Random.nextInt(1, 5) // Positive positions
        }

        UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit(
            id = randomNum.toLong(),
            nom = if (randomNum == 0) "Undefined Supplier" else "Grossiste $randomNum",
            position_Grossist_Don_Parent_Grossists_List = position,
            couleur = colors.random(),
            currentCreditBalance = Random.nextDouble(0.0, 10000.0),
            position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = position
        )
    }.toMutableStateList()
}
