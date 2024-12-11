package com.example.Packages._3.Fragment.Models.Test

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

class UiStateSnapshotStateList(
    initialLastUpdateTime: String? = getCurrentFormattedTime(),
    initialGroupFireBaseReference: List<GroupFireBaseReference> = emptyList()
) {
    // State properties
    var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
    var groupFireBaseReferences: SnapshotStateList<GroupFireBaseReference> =
        initialGroupFireBaseReference.toMutableStateList()

    // Firebase reference
    private val databaseRef = Firebase.database
        .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    // Firebase operations
    suspend fun updateSelfInFirebase() {
        try {
            databaseRef.setValue(this).await()
            lastUpdateTimeFormatted = getCurrentFormattedTime()
        } catch (e: Exception) {
            throw Exception("Failed to update state in Firebase: ${e.message}")
        }
    }

    suspend fun loadFromFirebase() {
        try {
            val snapshot = databaseRef.get().await()
            snapshot.getValue<UiStateSnapshotStateList>()?.let { state ->
                lastUpdateTimeFormatted = state.lastUpdateTimeFormatted
                groupFireBaseReferences.clear()
                groupFireBaseReferences.addAll(state.groupFireBaseReferences)
            }
        } catch (e: Exception) {
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }

    // Nested class for group references
    class GroupFireBaseReference(
        var id: Long = 0L,
        var position: Int = 0,
        var nom: String = "",
        initialLastUpdateTime: String? = getCurrentFormattedTime(),
        initialProductsToUpdate: List<Product> = emptyList()
    ) {
        var lastUpdateTimeFormatted: String? by mutableStateOf(initialLastUpdateTime)
        var productsToUpdate: SnapshotStateList<Product> =
            initialProductsToUpdate.toMutableStateList()

        suspend fun updateSelfInFirebase() {
            try {
                val groupRef = Firebase.database
                    .getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")
                    .child("groups")
                    .child(id.toString())

                groupRef.setValue(this).await()
                lastUpdateTimeFormatted = getCurrentFormattedTime()
            } catch (e: Exception) {
                throw Exception("Failed to update group in Firebase: ${e.message}")
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
    }

    // Product class
    class Product(
        var id: Long = 0L,
        initialTriggerTime: Long = System.currentTimeMillis()
    ) {
        var triggerTime: Long by mutableStateOf(initialTriggerTime)

        fun updateTriggerTime() {
            triggerTime = System.currentTimeMillis()
        }
    }

    // Group management functions
    fun addGroup(group: GroupFireBaseReference) {
        groupFireBaseReferences.add(group)
    }

    fun removeGroup(groupId: Long) {
        groupFireBaseReferences.removeAll { it.id == groupId }
    }

    fun updateGroup(updatedGroup: GroupFireBaseReference) {
        val index = groupFireBaseReferences.indexOfFirst { it.id == updatedGroup.id }
        if (index != -1) {
            groupFireBaseReferences[index] = updatedGroup
        }
    }

    fun getGroupById(groupId: Long): GroupFireBaseReference? {
        return groupFireBaseReferences.find { it.id == groupId }
    }

    companion object {
        private fun getCurrentFormattedTime(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
        }
    }
}
