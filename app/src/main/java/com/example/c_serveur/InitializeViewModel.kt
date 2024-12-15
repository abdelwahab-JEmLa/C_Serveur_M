package com.example.clientjetpack.ViewModel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Models.AppSettingsSaverModel
import com.example.Models.ClientsDataBase
import com.example.Models.DiviseurDeDisplayProductForEachClient
import com.example.Models.Grossissts_DataBAse
import com.example.Models.ProductsCategoriesDataBase
import com.example.Models.Produits_DataBase
import com.example.Packages.P3.Historique_D_Achate_Grossisst_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Commende_Produits_Au_Grossissts_DataBase
import com.example.clientjetpack.Modules.AppDatabase
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.format.DateTimeFormatter

/**
 * Actions UI pour ClientBonsByDay
 */
// In ClientBonsByDayActions:
data class InitViewModelActions(
    val onClick: () -> Unit = {},
    val onStatisticsDateSelected: (String) -> Unit = {}
)

@Composable
fun rememberClientBonsByDayActions(viewModel: InitializeViewModel): InitViewModelActions {
    return remember(viewModel) {
        InitViewModelActions(
            onClick = {},
            onStatisticsDateSelected = { viewModel.updateStatisticsDate(it) }
        )
    }
}
open class InitializeViewModel(
    context: Context,
    private val database: AppDatabase,
) : ViewModel() {
    // État UI
    private val _stateFlow = MutableStateFlow(UiStat())
    val state: StateFlow<UiStat> = _stateFlow.asStateFlow()

    // Firebase Setup
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val refAppSettingsSaverModel = firebaseDatabase.getReference("2_AppSettingsSaverNew")
    private val refClientsDataBase = firebaseDatabase.getReference("G_Clients")
    private val displayStatProductRef = firebaseDatabase.getReference("3_DisplayStatProduct")
    private val refProductsDataBase = firebaseDatabase.getReference("e_DBJetPackExport")
    private val productsCategoriesDataBaseRef = firebaseDatabase.getReference("H_CategorieTabele")
    private val ref_Grossissts_DataBAse = firebaseDatabase.getReference("F_Suppliers")
    private val ref_Historique_D_Achate_Grossisst_DataBase = firebaseDatabase.getReference("Historique_D_Achate_Grossisst_DataBase")
    private val ref_Commende_Produits_Au_Grossissts_DataBase = firebaseDatabase.getReference("K_SupplierArticlesRecived")

    private val appSettingsSaverModelDao = database.appSettingsSaverModelDao()
    private val clientsDataBaseDao = database.clientsDataBaseDao()
    private val productsDataBaseDao = database.productsDataBaseDao()
    private val displayStatProductDao = database.diviseurDeDisplayProductForEachClientDao()

    private val productsCategoriesDataBaseDao = database.productsCategoriesDataBaseDao()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


    // In ClientBonsByDayViewModel, add this new function:
    fun updateStatisticsDate(date: String) {
        viewModelScope.launch {
            try {
                // Get current settings
                val currentSettings = appSettingsSaverModelDao.getAll().firstOrNull()

                // Create new or update existing settings
                val updatedSettings = currentSettings?.copy(
                    displayStatisticsDate = date
                ) ?: AppSettingsSaverModel(
                    id = 1,
                    displayStatisticsDate = date
                )

                // Update local database
                appSettingsSaverModelDao.upsert(updatedSettings)

                // Update Firebase
                refAppSettingsSaverModel.child(updatedSettings.id.toString()).setValue(updatedSettings)

                // Update UI state
                _stateFlow.update { currentState ->
                    currentState.copy(
                        appSettingsSaverModel = listOf(updatedSettings)
                    )
                }
            } catch (e: Exception) {
                _stateFlow.update { it.copy(error = "Error updating statistics date: ${e.message}") }
            }
        }
    }

    /**
     * Configure l'écouteur Firebase
     */
    init {
        initializeData()
        setupFirebaseListeners()
    }
    /**
     * Initialise_ViewModel les données
     */
    private fun initializeData() {
        viewModelScope.launch {
            try {
                // Collecteur AppSettingsSaverModel
                launch {
                    appSettingsSaverModelDao.getAllFlow().collect { appSettingsSaverModel ->
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                appSettingsSaverModel = appSettingsSaverModel,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
                // Collecteur
                launch {
                    clientsDataBaseDao.getAllFlow().collect {
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                clientsDataBase = it,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
                // Collecteur
                launch {
                    productsDataBaseDao.getAllFlow().collect {
                        _stateFlow.update { currentState ->
                            currentState.copy(
                                produitsDataBase = it,
                                isLoading = false,
                                isInitialized = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur initialisation: ${e.message}",
                        isInitialized = false
                    )
                }
            }
        }
    }

    private fun setupFirebaseListeners() {
        import_Commende_Produits_Au_Grossissts_DataBase()
        importGrossissts_DataBAse()
        importProductsDataBase()
        importProductsCategoriesDataBase()
        importDisplayStatProduct()
        importClientsDataBase()
        Import_Historique_D_Achate_Grossisst_DataBase()
        setupAppSettingsSaverModel()
    }
    private fun import_Commende_Produits_Au_Grossissts_DataBase() {
        viewModelScope.launch {
            try {
                val localProductsCount = database.commende_Produits_Au_Grossissts_DataBase_Dao().count()
                if (localProductsCount == 0) {
                    val snapshot = ref_Commende_Produits_Au_Grossissts_DataBase.get().await()
                    if (snapshot.exists()) {
                        val itemList = mutableListOf<Commende_Produits_Au_Grossissts_DataBase>()
                        for (childSnapshot in snapshot.children) {
                            val item = childSnapshot.getValue( Commende_Produits_Au_Grossissts_DataBase::class.java)
                            item?.let {
                                itemList.add(it)
                            }
                        }
                        if (itemList.isNotEmpty()) {
                            database.commende_Produits_Au_Grossissts_DataBase_Dao().insertAll(itemList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des produits: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun importGrossissts_DataBAse() {
        viewModelScope.launch {
            try {
                // Vérifier si la base de données Room est vide
                val localProductsCount = database.grossissts_DataBAse_Dao().count()
                if (localProductsCount == 0) {

                    // Récupérer tous les produits depuis Firebase
                    val snapshot = ref_Grossissts_DataBAse.get().await()

                    if (snapshot.exists()) {
                        val itemList = mutableListOf<Grossissts_DataBAse>()

                        for (childSnapshot in snapshot.children) {
                            val item = childSnapshot.getValue(Grossissts_DataBAse::class.java)
                            item?.let {
                                itemList.add(it)
                            }
                        }

                        // Insérer tous les produits en une seule fois
                        if (itemList.isNotEmpty()) {
                            database.grossissts_DataBAse_Dao().insertAll(itemList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des produits: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun Import_Historique_D_Achate_Grossisst_DataBase() {
        viewModelScope.launch {
            try {
                // Vérifier si la base de données Room est vide
                val localProductsCount = database.historique_D_Achate_Grossisst_DataBase_Dao().count()
                if (localProductsCount == 0) {

                    // Récupérer tous les produits depuis Firebase
                    val snapshot = ref_Historique_D_Achate_Grossisst_DataBase.get().await()

                    if (snapshot.exists()) {
                        val itemList = mutableListOf<Historique_D_Achate_Grossisst_DataBase>()

                        for (childSnapshot in snapshot.children) {
                            val item = childSnapshot.getValue(Historique_D_Achate_Grossisst_DataBase::class.java)
                            item?.let {
                                itemList.add(it)
                            }
                        }

                        // Insérer tous les produits en une seule fois
                        if (itemList.isNotEmpty()) {
                            database.historique_D_Achate_Grossisst_DataBase_Dao().insertAll(itemList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des produits: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun importProductsCategoriesDataBase() {
        viewModelScope.launch {
            try {
                // Vérifier si la base de données Room est vide
                val localProductsCount = productsCategoriesDataBaseDao.count()
                if (localProductsCount == 0) {

                    // Récupérer tous les produits depuis Firebase
                    val snapshot = productsCategoriesDataBaseRef.get().await()

                    if (snapshot.exists()) {
                        val itemList = mutableListOf<ProductsCategoriesDataBase>()

                        for (childSnapshot in snapshot.children) {
                            val item = childSnapshot.getValue(ProductsCategoriesDataBase::class.java)
                            item?.let {
                                itemList.add(it)
                            }
                        }

                        // Insérer tous les produits en une seule fois
                        if (itemList.isNotEmpty()) {
                            productsCategoriesDataBaseDao.insertAll(itemList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des produits: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun importDisplayStatProduct() {
        viewModelScope.launch {
            try {
                // Vérifier si la base de données Room est vide
                val localItemCount = displayStatProductDao.count()
                if (localItemCount == 0) {

                    // Récupérer tous les produits depuis Firebase
                    val snapshot = displayStatProductRef.get().await()

                    if (snapshot.exists()) {
                        val productsList = mutableListOf<DiviseurDeDisplayProductForEachClient>()

                        for (childSnapshot in snapshot.children) {
                            val product = childSnapshot.getValue(DiviseurDeDisplayProductForEachClient::class.java)
                            product?.let {
                                productsList.add(it)
                            }
                        }

                        // Insérer tous les produits en une seule fois
                        if (productsList.isNotEmpty()) {
                            displayStatProductDao.insertAll(productsList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des produits: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun importProductsDataBase() {
        viewModelScope.launch {
            try {
                // Vérifier si la base de données Room est vide
                val localProductsCount = productsDataBaseDao.count()
                if (localProductsCount == 0) {

                    // Récupérer tous les produits depuis Firebase
                    val snapshot = refProductsDataBase.get().await()

                    if (snapshot.exists()) {
                        val productsList = mutableListOf<Produits_DataBase>()

                        for (childSnapshot in snapshot.children) {
                            val product = childSnapshot.getValue(Produits_DataBase::class.java)
                            product?.let {
                                productsList.add(it)
                            }
                        }

                        // Insérer tous les produits en une seule fois
                        if (productsList.isNotEmpty()) {
                            productsDataBaseDao.insertAll(productsList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des produits: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun importClientsDataBase() {
        viewModelScope.launch {
            try {
                // Vérifier si la base de données Room est vide
                val localClientsCount = clientsDataBaseDao.count()
                if (localClientsCount == 0) {
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
                            clientsDataBaseDao.insertAll(clientsList)
                        }
                    }
                }
            } catch (e: Exception) {
                _stateFlow.update {
                    it.copy(
                        error = "Erreur d'import des clients: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private var appSettingsSaverModelListener: ValueEventListener? = null
    private fun setupAppSettingsSaverModel() {
        appSettingsSaverModelListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    try {
                        // Get local data
                        val localDatas = appSettingsSaverModelDao.getAll()
                        val localDatasMap = localDatas.associateBy { it.id }

                        val firebaseData = mutableListOf<AppSettingsSaverModel>()
                        snapshot.children.forEach { childSnapshot ->
                            childSnapshot.getValue(AppSettingsSaverModel::class.java)?.let {
                                firebaseData.add(it)
                            }
                        }

                        // Compare and synchronize
                        val firebaseBuyBonsMap = firebaseData.associateBy { it.id }

                        // Handle updates and additions
                        firebaseData.forEach {
                            val localData = localDatasMap[it.id]
                            if (localData == null || localData != it) {
                                appSettingsSaverModelDao.upsert(it)
                            }
                        }

                        // Handle deletions
                        localDatas.forEach {
                            if (!firebaseBuyBonsMap.containsKey(it.id)) {
                                appSettingsSaverModelDao.delete(it)
                            }
                        }

                    } catch (e: Exception) {
                        _stateFlow.update { it.copy(error = "Erreur sync Firebase BuyBon: ${e.message}") }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.update { it.copy(error = "Sync Firebase BuyBon annulée: ${error.message}") }
            }
        }

        appSettingsSaverModelListener?.let {
            refAppSettingsSaverModel.addValueEventListener(it)
        }
    }

}
