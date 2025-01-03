package com.example.Apps_Head.B_RelationalDataBase._3.Init

import com.example.Apps_Head.B_RelationalDataBase._1.Model.RelationalDatabase
import com.example.Apps_Head.B_RelationalDataBase._2.ViewModel.RelationalViewModel
import kotlin.random.Random

suspend fun RelationalViewModel.Cree() {
    // List of sample product names
    val productNames = listOf(
        "Laptop", "Smartphone", "Tablet", "Headphones", "Mouse",
        "Keyboard", "Monitor", "Printer", "Scanner", "Speaker"
    )

    // List of sample client names
    val clientNames = listOf(
        "John", "Alice", "Bob", "Carol", "David",
        "Eva", "Frank", "Grace", "Henry", "Iris"
    )

    // List of sample colors
    val colors = listOf(
        "Red", "Blue", "Green", "Yellow", "Purple",
        "Orange", "Pink", "Brown", "Gray", "Black"
    )

    // Generate random products
    val products = (1..15).map { id ->
        RelationalDatabase.ProduitModel(
            id = id.toLong(),
            nom = productNames[Random.nextInt(productNames.size)]
        )
    }

    // Generate random clients
    val clients = (1..8).map { id ->
        RelationalDatabase.ClientInformationsModel(
            id = id.toLong(),
            nom = clientNames[Random.nextInt(clientNames.size)],
            couleur = colors[Random.nextInt(colors.size)]
        )
    }

    // Create random associations between clients and products
    val clientProductMap = clients.associateWith { client ->
        // Randomly select 1 to 5 products for each client
        products.shuffled().take(Random.nextInt(1, 6))
    }

    // Update the state
    _relationalDatabase.clientAcheteurEtCesProduits = clientProductMap
    _relationalDatabase.produitsMainList.clear()
    _relationalDatabase.produitsMainList.addAll(products)

    // Save to Firebase
    RelationalDatabase.refFireBase.setValue(
        mapOf(
            "selectedClientAndProducts" to clientProductMap,
            "produitsMainList" to products
        )
    )
}
