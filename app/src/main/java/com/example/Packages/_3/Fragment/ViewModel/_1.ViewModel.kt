package com.example.Packages._3.Fragment.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.init.Init_ImportCalcules_Ui_Stat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch

internal class P3_ViewModel : ViewModel() {
    private val database = Firebase.database
    val refFirebase = database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    var _ui_Mutable_State = Ui_Mutable_State()
    val ui_Mutable_State: Ui_Mutable_State get() = _ui_Mutable_State

    init {

        viewModelScope.launch {
                Init_ImportCalcules_Ui_Stat()
                //Test_Initiale_Calcules_Autre_Valeurs()
        }

    }

//    fun logGroupingDetails(tag: String = "No Tag", repeteList: Int = 0) {
//        logger.info("=== Grouping Details for $tag ===")
//        logger.info("Repeated List Count: $repeteList")
//        logger.info("Total Products: ${produits_Commend_DataBase.size}")
//
//        produits_Commend_DataBase.forEachIndexed { index, product ->
//            logger.info("""
//                Product $index:
//                  ID: ${product.id}
//                  Label: ${product.nom}
//                  Not Found: ${product.non_Trouve}
//                  ${product.grossist_Choisi_Pour_Acheter_CeProduit?.let { supplier ->
//                """
//                    Supplier:
//                      Name: ${supplier.nom}
//                      Credit Balance: ${supplier.currentCreditBalance}
//                    """.trimIndent()
//            } ?: "No Supplier"}
//            """.trimIndent())
//        }
//
//        logger.info("Status: $namePhone")
//        logger.info("=== End Details ===")
//    }
}
