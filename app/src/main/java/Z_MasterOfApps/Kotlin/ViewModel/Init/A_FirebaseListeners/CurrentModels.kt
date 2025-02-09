package Z_MasterOfApps.Kotlin.ViewModel.Init.A_FirebaseListeners

import Z_MasterOfApps.Kotlin.Model.A_ProduitModel
import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CurrentModels {
    private val listeners = mutableListOf<ListenerInfo>()

    private data class ListenerInfo(
        val listener: ValueEventListener,
        val path: String
    )

    fun setupCurrentModels(viewModel: ViewModelInitApp) {
        cleanup() // Clean up existing listeners before setting up new ones
        setupProductsListener(viewModel)
        setupClientsListener(viewModel)
        setupGrossistsListener(viewModel)
    }

    fun cleanup() {
        listeners.forEach { info ->
            when (info.path) {
                "products" -> _ModelAppsFather.produitsFireBaseRef.removeEventListener(info.listener)
                "clients" -> B_ClientsDataBase.refClientsDataBase.removeEventListener(info.listener)
                "grossists" -> _ModelAppsFather.ref_HeadOfModels.removeEventListener(info.listener)
            }
        }
        listeners.clear()
    }

    private fun setupProductsListener(viewModel: ViewModelInitApp) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val products = mutableListOf<A_ProduitModel>()

                        snapshot.children.forEach { snap ->       //-->
                        //TODO(1): pk meme si   D  Connectivity check complete - Online: true
                            //17:29:01.249              D  Performing new connectivity check
                            //17:29:01.499              D  Connectivity check complete - Online: true 
                            
                            //ca ne s active pas on change dons fireBase poduit 
                            val map = snap.value as? Map<*, *> ?: return@forEach
                            val prod = A_ProduitModel(
                                id = snap.key?.toLongOrNull() ?: return@forEach,
                                itsTempProduit = map["itsTempProduit"] as? Boolean ?: false,
                                init_nom = map["nom"] as? String ?: "",
                                init_besoin_To_Be_Updated = map["besoin_To_Be_Updated"] as? Boolean ?: false,
                                initialNon_Trouve = map["non_Trouve"] as? Boolean ?: false,
                                init_visible = map["isVisible"] as? Boolean ?: false
                            ).apply {
                                // Load StatuesBase
                                snap.child("statuesBase").getValue(A_ProduitModel.StatuesBase::class.java)?.let {
                                    statuesBase = it
                                    statuesBase.imageGlidReloadTigger = 0
                                }

                                // Load ColoursEtGouts
                                val coloursEtGoutsList = mutableListOf<A_ProduitModel.ColourEtGout_Model>()
                                snap.child("coloursEtGoutsList").children.forEach { colorSnap ->
                                    colorSnap.getValue(A_ProduitModel.ColourEtGout_Model::class.java)?.let {
                                        coloursEtGoutsList.add(it)
                                    }
                                }
                                this.coloursEtGoutsList = coloursEtGoutsList

                                // Load BonCommend with proper states
                                snap.child("bonCommendDeCetteCota").getValue(A_ProduitModel.GrossistBonCommandes::class.java)?.let { bonCommend ->
                                    snap.child("bonCommendDeCetteCota/mutableBasesStates")
                                        .getValue(A_ProduitModel.GrossistBonCommandes.MutableBasesStates::class.java)?.let {
                                            bonCommend.mutableBasesStates = it
                                        }
                                    bonCommendDeCetteCota = bonCommend
                                }

                                // Load BonsVent
                                val bonsVent = mutableListOf<A_ProduitModel.ClientBonVentModel>()
                                snap.child("bonsVentDeCetteCotaList").children.forEach { bonVentSnap ->
                                    bonVentSnap.getValue(A_ProduitModel.ClientBonVentModel::class.java)?.let {
                                        bonsVent.add(it)
                                    }
                                }
                                bonsVentDeCetteCotaList = bonsVent

                                // Load Historique
                                val historique = mutableListOf<A_ProduitModel.ClientBonVentModel>()
                                snap.child("historiqueBonsVentsList").children.forEach { historySnap ->
                                    historySnap.getValue(A_ProduitModel.ClientBonVentModel::class.java)?.let {
                                        historique.add(it)
                                    }
                                }
                                historiqueBonsVentsList = historique
                            }
                            products.add(prod)
                        }

                        withContext(Dispatchers.Main) {
                            viewModel.modelAppsFather.produitsMainDataBase.apply {
                                clear()
                                addAll(products)
                            }
                        }
                    } catch (e: Exception) {
                        // Log error but don't crash
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }

        listeners.add(ListenerInfo(listener, "products"))
        _ModelAppsFather.produitsFireBaseRef.addValueEventListener(listener)
    }

    private fun setupClientsListener(viewModel: ViewModelInitApp) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val clients = mutableListOf<B_ClientsDataBase>()

                        snapshot.children.forEach { snap ->
                            val map = snap.value as? Map<*, *> ?: return@forEach
                            B_ClientsDataBase(
                                id = snap.key?.toLongOrNull() ?: return@forEach,
                                nom = map["nom"] as? String ?: ""
                            ).apply {
                                snap.child("statueDeBase")
                                    .getValue(B_ClientsDataBase.StatueDeBase::class.java)?.let {
                                        statueDeBase = it
                                    }
                                snap.child("gpsLocation")
                                    .getValue(B_ClientsDataBase.GpsLocation::class.java)?.let {
                                        gpsLocation = it
                                    }
                                clients.add(this)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            viewModel.modelAppsFather.clientDataBase.apply {
                                clear()
                                addAll(clients)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }

        listeners.add(ListenerInfo(listener, "clients"))
        B_ClientsDataBase.refClientsDataBase.addValueEventListener(listener)
    }

    private fun setupGrossistsListener(viewModel: ViewModelInitApp) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val grossists = mutableListOf<C_GrossistsDataBase>()

                        val grossistsNode = snapshot.child("C_GrossistsDataBase")
                        if (!grossistsNode.exists()) {
                            grossists.add(C_GrossistsDataBase(
                                id = 1,
                                nom = "Default Grossist",
                                statueDeBase = C_GrossistsDataBase.StatueDeBase(
                                    cUnClientTemporaire = true
                                )
                            ))
                        } else {
                            grossistsNode.children.forEach { grossistSnapshot ->
                                try {
                                    val grossistMap = grossistSnapshot.value as? Map<*, *> ?: return@forEach
                                    C_GrossistsDataBase(
                                        id = grossistSnapshot.key?.toLongOrNull() ?: return@forEach,
                                        nom = grossistMap["nom"] as? String ?: "Non Defini"
                                    ).apply {
                                        grossistSnapshot.child("statueDeBase")
                                            .getValue(C_GrossistsDataBase.StatueDeBase::class.java)?.let {
                                                statueDeBase = it
                                            }
                                        grossists.add(this)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            viewModel.modelAppsFather.grossistsDataBase.apply {
                                clear()
                                addAll(grossists)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }

        listeners.add(ListenerInfo(listener, "grossists"))
        _ModelAppsFather.ref_HeadOfModels.addValueEventListener(listener)
    }
}
