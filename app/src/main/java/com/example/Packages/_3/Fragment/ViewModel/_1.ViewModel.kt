package com.example.Packages._3.Fragment.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal class P3_ViewModel : ViewModel() {
    private val database = Firebase.database
    val ref_ViewModel_Produit_DataBase =
        database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    var _ui_Mutable_State = Ui_Mutable_State()
    val ui_Mutable_State: Ui_Mutable_State get() = _ui_Mutable_State

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)

    init {
        viewModelScope.launch {
            setupGroupReferencesListener()
            addRandomTestProductReferences()
            /*
        isInitializing = true
        Init_Cree_Ui_State { progress ->
            initializationProgress = progress
        }
        isInitializing = false    */
        }
    }


    private fun addRandomTestProductReferences() {
        val randomProducts = List(5) { index ->
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
                description = "Default group for products",
                last_Update_Time_Formatted = LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                produits_A_Update = randomProducts  // Add the products here
            )

            // Add the complete group reference with products
            ref_ViewModel_Produit_DataBase
                .child("1_Groupeur_References_FireBase_DataBase")
                .child(defaultGroupRef.id.toString())
                .setValue(defaultGroupRef)
        } else {
            // Update just the products_A_Update field
            ref_ViewModel_Produit_DataBase
                .child("1_Groupeur_References_FireBase_DataBase")
                .child(groupRef.id.toString())
                .child("produits_A_Update")
                .setValue(randomProducts)
        }
    }

    private fun setupGroupReferencesListener() {
        val groupReferencesRef =
            ref_ViewModel_Produit_DataBase.child("1_Groupeur_References_FireBase_DataBase")

        groupReferencesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val referenceData = snapshot.children.mapNotNull {
                    it.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)
                }

                _ui_Mutable_State.groupeur_References_FireBase_DataBase = referenceData
            }

            override fun onCancelled(error: DatabaseError) {
                // Error handling can be added here if needed
            }
        })
    }
}
