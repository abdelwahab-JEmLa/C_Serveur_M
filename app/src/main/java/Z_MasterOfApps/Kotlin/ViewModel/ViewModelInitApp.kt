package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeNewStart
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.LoadFromFirebaseProduits
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp(val appContext: Context) : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())

    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)


    init {
        viewModelScope.launch {
            try {
                isLoading = true
                val nombre = 0
                if (nombre == 0) {
                    LoadFromFirebaseProduits.loadFromFirebase(this@ViewModelInitApp)
                } else {
                    CreeNewStart(_modelAppsFather)
                }

                setupDataListeners()
                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }


    private fun setupDataListeners() {
        _modelAppsFather.produitsMainDataBase.forEach { produit ->
            Log.d("SetupListener", "Setting up listener for product ${produit.id}")
            produitsFireBaseRef.child(produit.id.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModelScope.launch {
                            try {
                                val updatedProduct = LoadFromFirebaseProduits.parseProduct(snapshot)
                                if (updatedProduct != null) {
                                    val index = _modelAppsFather.produitsMainDataBase.indexOfFirst {
                                        it.id == updatedProduct.id
                                    }
                                    if (index != -1) {
                                        _modelAppsFather.produitsMainDataBase[index] = updatedProduct
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("SetupListener", "Error updating product", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("SetupListener", "Database error: ${error.message}")
                    }
                })
        }
    }

}
