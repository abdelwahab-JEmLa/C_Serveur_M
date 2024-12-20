package com.example.c_serveur.Modules
//
//private fun findNextAvailableId(): Number {
//    val maxId = appInitializeModel.produit_Main_DataBase
//        .filter { it.id < 2000 }
//        .maxOfOrNull { it.id } ?: 0
//
//    return if (maxId + 1 < 2000) {
//        maxId + 1
//    } else {
//        val existingIds = appInitializeModel.produit_Main_DataBase
//            .filter { it.id < 2000 }
//            .map { it.id }
//            .toSet()
//
//        (1..2000).firstOrNull { it.toLong() !in existingIds }
//            ?: throw IllegalStateException("No available IDs under 2000")
//    }
//}
