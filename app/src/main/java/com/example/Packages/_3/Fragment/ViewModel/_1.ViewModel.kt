package com.example.Packages._3.Fragment.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.init.Init_Cree_Ui_State
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal class P3_ViewModel : ViewModel() {
    private val database = Firebase.database
    val refFirebase = database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    var _ui_Mutable_State = Ui_Mutable_State()
    val ui_Mutable_State: Ui_Mutable_State get() = _ui_Mutable_State

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)

    init {

        viewModelScope.launch {
            createNewReference()
            addRandomTestProductReferences()
            setupGroupReferencesListener()
            isInitializing = true
            Init_Cree_Ui_State { progress ->
                initializationProgress = progress
            }
            isInitializing = false

        }

    }


    private fun addRandomTestProductReferences() {
        val randomProducts = List(5) { index ->
            Ui_Mutable_State.Produits_Commend_DataBase(
                id = Random.nextInt(500, 700),
                nom = "Test Product ${index + 1}",
            )
        }

        _ui_Mutable_State.groupeur_References_FireBase_DataBase.find { it.id == 1L }
            ?.let { groupRef ->
                groupRef.produits_A_Update = randomProducts.map { product ->
                    Ui_Mutable_State.Groupeur_References_FireBase_DataBase.Produits_A_Update(
                        id = product.id.toLong(),
                        position = product.id,
                        ref = "product_${product.id}",
                        nom = product.nom,
                        tiggr_Time = System.currentTimeMillis()
                    )
                }

                // Update the reference in Firebase
                refFirebase
                    .child("1_Groupeur_References_FireBase_DataBase")
                    .child(groupRef.id.toString())
                    .setValue(groupRef)
            }
    }

    private suspend fun createNewReference() {
        val now = LocalDateTime.now()
        val formattedTimestamp = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss"))

        val referencesRef = refFirebase.child("1_Groupeur_References_FireBase_DataBase")

        val referencesSnapshot = referencesRef.get().await()

        val maxId = referencesSnapshot.children
            .mapNotNull { it.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)?.id }
            .maxOrNull() ?: 0L

        val maxPosition = referencesSnapshot.children
            .mapNotNull { it.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)?.position }
            .maxOrNull() ?: 0

        val newReference = Ui_Mutable_State.Groupeur_References_FireBase_DataBase(
            id = maxId + 1,
            position = maxPosition + 1,
            nom = "Produits_Commend_DataBase",
            description = "Produits_Commend_DataBase",
            ref = "Produits_Commend_DataBase",
            last_Update_Time_Formatted = formattedTimestamp
        )

        referencesRef.child(newReference.id.toString()).setValue(newReference).await()
    }
}

// Add a Firebase value event listener for group references
private fun P3_ViewModel.setupGroupReferencesListener() {
    val groupReferencesRef = refFirebase.child("1_Groupeur_References_FireBase_DataBase")

    groupReferencesRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val referenceData = snapshot.children.mapNotNull {
                it.getValue(Ui_Mutable_State.Groupeur_References_FireBase_DataBase::class.java)
            }
            _ui_Mutable_State.groupeur_References_FireBase_DataBase = referenceData
        }

        override fun onCancelled(error: DatabaseError) {
            // Log or handle the error appropriately
            println("Group References Listener Cancelled: ${error.message}")
        }
    })
}
