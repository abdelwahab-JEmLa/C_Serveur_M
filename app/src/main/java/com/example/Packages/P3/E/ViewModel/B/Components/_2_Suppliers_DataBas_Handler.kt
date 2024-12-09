package com.example.Packages.P3.E.ViewModel.B.Components

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Models.Grossissts_DataBAse
import com.example.Packages.P3.E.ViewModel.ViewModelFragment
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun ViewModelFragment.Update_Supplier_Vocal_French_Name(supplierId: Long, newName: String) {      //->
    viewModelScope.launch {
        val updatedSuppliers = _Suppliers_DataBase.map { supplier ->
            if (supplier.idSupplierSu == supplierId) {
                supplier.copy(nameInFrenche = newName)
            } else {
                supplier
            }
        }
        _Ui_Statue_DataBase.update { currentState ->
            currentState.copy(grossissts_DataBAse = updatedSuppliers)
        }
        refTabelleSuppliersSA.child(supplierId.toString()).child("supplierNameInFrenche").setValue(newName)
            .addOnSuccessListener {
                // Handle success if needed
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
                Log.e("HeadOfViewModels", "Failed to update supplier vocal Arab name", e)
            }
    }
}
fun ViewModelFragment.Update_Supplier_Vocal_Arab_Name(supplierId: Long, newName: String) {
    viewModelScope.launch {
        val updatedSuppliers = _Suppliers_DataBase.map { supplier ->
            if (supplier.idSupplierSu == supplierId) {
                supplier.copy(nomVocaleArabeDuSupplier = newName)
            } else {
                supplier
            }
        }
        _Ui_Statue_DataBase.update { currentState ->
            currentState.copy(grossissts_DataBAse = updatedSuppliers)
        }
        refTabelleSuppliersSA.child(supplierId.toString()).child("nomVocaleArabeDuSupplier").setValue(newName)
    }
}
fun ViewModelFragment.Reorder_Suppliers(firstClickedSupplierId: Long, secondClickedSupplierId: Long) {
    val reorderedSuppliers =
        Reorder_Suppliers(
            _Suppliers_DataBase,
            firstClickedSupplierId,
            secondClickedSupplierId
        )
    // Mettre à jour le classement en fonction de la nouvelle position
    val updatedSuppliers = reorderedSuppliers.mapIndexed { index, supplier ->
        supplier.copy(classmentSupplier = (index + 1).toDouble())
    }
    // Mettre à jour l'état de l'UI avec le nouvel ordre des fournisseurs
    _Ui_Statue_DataBase.update { currentState ->
        currentState.copy(grossissts_DataBAse = updatedSuppliers)
    }
    // Mettre à jour Firebase et le stockage local
    viewModelScope.launch {
        Update_Firebase_And_Locale_Suppliers(updatedSuppliers)
    }
}
fun Reorder_Suppliers(
    suppliers: List<Grossissts_DataBAse>,
    fromSupplierId: Long,
    toSupplierId: Long
): List<Grossissts_DataBAse> {
    val mutableList = suppliers.toMutableList()

    // Étape 1: Placer le fournisseur avec ID 10 au début de la liste
    val supplier10 = mutableList.find { it.idSupplierSu == 10L }
    if (supplier10 != null) {
        mutableList.remove(supplier10)
        mutableList.add(0, supplier10)
    }

    // Étape 2: Trouver les indices des fournisseurs à réorganiser
    val fromIndex = mutableList.indexOfFirst { it.idSupplierSu == fromSupplierId }
    val toIndex = mutableList.indexOfFirst { it.idSupplierSu == toSupplierId }

    // Étape 3: Réorganiser les fournisseurs si les deux IDs existent
    if (fromIndex != -1 && toIndex != -1) {
        // Déplacer le fournisseur de la position source à la position cible
        val supplier = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, supplier)

        // Étape 4: Mettre à jour le classement des fournisseurs selon leur nouvelle position
        mutableList.forEachIndexed { index, supplier ->
            supplier.classmentSupplier = (index + 1).toDouble()
        }
    }

    return mutableList
}
fun ViewModelFragment.Update_Firebase_And_Locale_Suppliers(suppliers: List<Grossissts_DataBAse>) {
    suppliers.forEach { supplier ->
        refTabelleSuppliersSA.child(supplier.idSupplierSu.toString()).setValue(supplier)
    }
}
