package com.example.coupeaudioai.Modules.Z.Archives

import android.util.Log
import com.example.Packages.Z.Archives.Models.ClientsDataBase
import com.example.Packages.Z.Archives.Models.DiviseurDeDisplayProductForEachClient
import com.example.Packages.Z.Archives.Models.Produits_DataBase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FireBaseHandler (private val database: AppDatabase,){

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val refAppSettingsSaverModel = firebaseDatabase.getReference("2_AppSettingsSaverNew")
    private val refClientsDataBase = firebaseDatabase.getReference("G_Clients")
    private val refProductsDataBase = firebaseDatabase.getReference("e_DBJetPackExport")
    private val diviseurDeDisplayProductForEachClientRef = firebaseDatabase.getReference("3_DiviseurDeDisplayProductForEachClient")

    private val diviseurDeDisplayProductForEachClientDao = database.diviseurDeDisplayProductForEachClientDao()


    fun updateFirebaseProductsDataBase(
        key: String,
        stat: Produits_DataBase
    ) {
        refProductsDataBase.child(key)
            .setValue(stat)
            .addOnSuccessListener {
                Log.d("Firebase", "Client product stat updated successfully: $key")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error updating client product stat", e)

            }
    }

     fun updateFirebaseDiviseurDeDisplayProductForEachClient(
        key: String="",
        stat: DiviseurDeDisplayProductForEachClient
    ) {
        diviseurDeDisplayProductForEachClientRef.child(key)
            .setValue(stat)
            .addOnSuccessListener {
                Log.d("Firebase", "Client product stat updated successfully: $key")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error updating client product stat", e)
            }
    }

    suspend fun importClientsDataBase() {
        try {
            // Récupérer tous les clients depuis Firebase
            val snapshot = refClientsDataBase.get().await()

            if (snapshot.exists()) {
                val clientsList = mutableListOf<ClientsDataBase>()

                for (childSnapshot in snapshot.children) {
                    val client = childSnapshot.getValue(ClientsDataBase::class.java)
                    client?.let {
                        clientsList.add(it)
                    }
                }

                // Insérer tous les clients en une seule fois
                if (clientsList.isNotEmpty()) {
                    database.clientsDataBaseDao().insertAll(clientsList)
                    Log.d("Firebase", "Successfully imported ${clientsList.size} clients")
                }
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error importing clients", e)
            throw e // Rethrow the exception to be handled by the ViewModel
        }
    }

}
