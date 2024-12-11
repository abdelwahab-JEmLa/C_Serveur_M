package com.example.Packages._3.Fragment.ViewModel._2.Init

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UiState(
    initialLastUpdateTime: String? = getCurrentFormattedTime(),
    initialReferencesFireBaseGroup: List<ReferencesFireBaseGroup> = emptyList()
) {
    // State properties
    var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
    var referencesFireBaseGroup: SnapshotStateList<ReferencesFireBaseGroup> =
        initialReferencesFireBaseGroup.toMutableStateList()

    // Firebase reference
    private val uiStateFireBaseDatabaseRef = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")


    // Nested class for group references
    class ReferencesFireBaseGroup(
        var id: Long = 0L,
        var position: Int = 0,
        var nom: String = "",
        initialUpdateAllTrigger: Boolean = false,
        initialLastUpdateTime: String? = getCurrentFormattedTime(),
        initialProductsToUpdate: List<Product> = emptyList()
    ) {
        var updateAllTrigger: Boolean by mutableStateOf(initialUpdateAllTrigger)
        var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
        var productsToUpdate: SnapshotStateList<Product> =
            initialProductsToUpdate.toMutableStateList()

        class Product(
            var id: Long = 0L,
            initialTriggerTime: Long = System.currentTimeMillis()
        ) {
            var triggerTime: Long by mutableStateOf(initialTriggerTime)

            fun updateTriggerTime() {
                triggerTime = System.currentTimeMillis()
            }
        }

        // Product management functions
        fun addProduct(product: Product) {
            productsToUpdate.add(product)
        }

        fun removeProduct(productId: Long) {
            productsToUpdate.removeAll { it.id == productId }
        }

        fun updateProduct(updatedProduct: Product) {
            val index = productsToUpdate.indexOfFirst { it.id == updatedProduct.id }
            if (index != -1) {
                productsToUpdate[index] = updatedProduct
            }
        }
        suspend fun updateReferencesFireBaseGroupSelfInFirebaseDataBase() {
            try {
                val groupRef = Firebase.database
                    .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
                    .child("groups")
                    .child(id.toString())

                groupRef.setValue(this).await()
                lastUpdateTimeFormatted = getCurrentFormattedTime()
            } catch (e: Exception) {
                throw Exception("Failed to update group in Firebase: ${e.message}")
            }
        }
    }

    // Group management functions
    fun addReferencesSnap(referenceFireBase: ReferencesFireBaseGroup) {
        referencesFireBaseGroup.add(referenceFireBase)
    }

    fun removeReferenceSnap(referenceFireBaseId: Long) {
        referencesFireBaseGroup.removeAll { it.id == referenceFireBaseId }
    }

    fun updateReferenceSnap(updatedReferenceGroup: ReferencesFireBaseGroup) {
        val index = referencesFireBaseGroup.indexOfFirst { it.id == updatedReferenceGroup.id }
        if (index != -1) {
            referencesFireBaseGroup[index] = updatedReferenceGroup
        }
    }

    fun getReferenceFireBaseById(referenceId: Long): ReferencesFireBaseGroup? {
        return referencesFireBaseGroup.find { it.id == referenceId }
    }

    companion object {
        private fun getCurrentFormattedTime(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
        }
    }
    // Firebase operations
    suspend fun updateUiStateSelfInFirebaseDataBase() {
        try {
            uiStateFireBaseDatabaseRef.setValue(this).await()
            lastUpdateTimeFormatted = getCurrentFormattedTime()
        } catch (e: Exception) {
            throw Exception("Failed to update state in Firebase: ${e.message}")
        }
    }

    suspend fun loadFromFirebaseDataBase() {
        try {
            val snapshot = uiStateFireBaseDatabaseRef.get().await()
            snapshot.getValue<UiState>()?.let { state ->
                lastUpdateTimeFormatted = state.lastUpdateTimeFormatted
                referencesFireBaseGroup.clear()
                referencesFireBaseGroup.addAll(state.referencesFireBaseGroup)
            }
        } catch (e: Exception) {
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }

}
