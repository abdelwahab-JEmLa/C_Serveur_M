package com.example.Packages._1.Fragment.Z.Archives.Test

/*
internal fun P3_ViewModel.Import_Initiale_Val_FireBase() {
    viewModelScope.launch(Dispatchers.IO) {
        val dataSnapshot = refFirebase.get().await()
        val tasksFromFirebase = dataSnapshot.children.mapNotNull { it.getValue(Ui_Mutable_State::class.java) }

        Serialiseur_Convertiseur_FireBase_Ancien_Keys_Au_New(tasksFromFirebase)
    }
}
internal fun P3_ViewModel.Serialiseur_Convertiseur_FireBase_Ancien_Keys_Au_New(tasksFromFirebase: List<Produits_Commend_DataBase>) {
    viewModelScope.launch {

        _produits_Commend_DataBase.clear()

        val updatedTasksFromFirebase = tasksFromFirebase.mapIndexed { _, item ->
            val updatedItem = item.updateSelectively(
                non_Trouve = item.non_Trouve,
                newLastChosenSupplier = item.lastChosenSupplier?.let { supplier ->
                    supplier.copy(currentCreditBalance = item.lastChosenSupplier!!.currentCreditBalance)
                }
            )

            Sync_With_Firebase(updatedItem)
            updatedItem
        }

        Initiale_Calcules_Autre_Valeurs(updatedTasksFromFirebase)

    }
}
internal fun P3_ViewModel.Initiale_Calcules_Autre_Valeurs(tasksFromFirebase: List<Produits_Commend_DataBase>) {
    viewModelScope.launch {

        _produits_Commend_DataBase.clear()

        val updatedTasksFromFirebase = tasksFromFirebase.mapIndexed { _, item ->
            val updatedItem = item.updateSelectively(
                non_Trouve = false,
                newLastChosenSupplier = item.lastChosenSupplier?.copy(currentCreditBalance = 1000.0)
            )

            Sync_With_Firebase(updatedItem)
            updatedItem
        }

        _produits_Commend_DataBase.addAll(updatedTasksFromFirebase)

        _produits_Commend_DataBase.logGroupingDetailsInitiale("Initiale_Calcules_Autre_Valeurs.after")
    }
}

internal fun List<Produits_Commend_DataBase>.logGroupingDetailsInitiale(
    tag: String = "No Tag",
    repeteList: Int = 10
) {
    println(tag)

    take(repeteList).forEachIndexed { index, item ->
        println("Item $index details:")
        println("  ID: ${item.id}")
        println("  Label: ${item.label}")
        println("  Checked: ${item.non_Trouve}")

        item.lastChosenSupplier?.let { supplier ->
            println("  Supplier Details:")
            println("    ID: ${supplier.id}")
            println("    Name: ${supplier.nom}")
            println("    Position: ${supplier.position}")
            println("    Color: ${supplier.couleur}")
            println("    Current Credit Balance: ${supplier.currentCreditBalance}")

            val copiedSupplier = supplier.copy(currentCreditBalance = 1000.0)
            println("  Copied Supplier Credit Balance: ${copiedSupplier.currentCreditBalance}")
            println("  Original Supplier Credit Balance: ${supplier.currentCreditBalance}")
        } ?: println("  No supplier information")

        println("---")
    }

    if (size >repeteList ) {
        println("... et ${size - repeteList} éléments supplémentaires")
    }
}

             */
