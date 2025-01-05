package com.example.Packages.A1_Fragment.D_FloatingActionButton

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

enum class CE_TELEPHONE_EST {
    _SERVEUR,
    _AFFICHEUR
}
@Composable
fun GlobalEditesGFABsFragment_1(
    appsHeadModel: AppsHeadModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(CE_TELEPHONE_EST._SERVEUR) }

    val handleImage = { uri: Uri ->
        scope.launch {
            try {
                val sortedByPosition = appsHeadModel.produitsMainDataBase
                    .sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit }

                // Find existing product that needs update
                val existingProduct = sortedByPosition.firstOrNull {
                    it.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB == true
                            && it.bonCommendDeCetteCota!!.
                    positionProduitDonGrossistChoisiPourAcheterCeProduit>0
                            && it.itsTempProduit
                }

                val lastIdUnder2000 = sortedByPosition
                    .filter { !it.itsTempProduit }
                    .maxOfOrNull { it.id } ?: 0L

                // Le nouvel ID sera le dernier ID + 1
                val newId = lastIdUnder2000 + 1

                // Create and add new product
                val newProduct = AppsHeadModel.ProduitModel(
                    id = newId,
                    init_nom = existingProduct?.nom ?: "",
                    init_besoin_To_Be_Updated = true,
                    init_it_Image_besoin_To_Be_Updated = true,
                    initialNon_Trouve = existingProduct?.non_Trouve ?: false,
                    init_colours_Et_Gouts = existingProduct?.coloursEtGouts?.toList() ?: listOf(),
                    init_historiqueBonsCommend = existingProduct?.historiqueBonsCommend?.toList() ?: listOf()
                )

                // Remove old product if it exists
                existingProduct?.let {
                    appsHeadModel.produitsMainDataBase.removeAll { prod -> prod.id == it.id }
                }
                appsHeadModel.produitsMainDataBase.add(newProduct)

                // Upload image
                val fileName = "${newId}_1.jpg"
                val storageRef = Firebase.storage.reference
                    .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    storageRef.putBytes(input.readBytes()).await()
                }

                appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            handleImage(uri)
        }
    }

    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (showOptions) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            tempFile
                        )
                        cameraLauncher.launch(uri)
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.AddAPhoto, "Take Photo")
                }

                FloatingActionButton(
                    onClick = { mode = if (mode == CE_TELEPHONE_EST._SERVEUR) CE_TELEPHONE_EST._AFFICHEUR else CE_TELEPHONE_EST._SERVEUR },
                    containerColor = Color(0xFFFF5722)
                ) {
                    Icon(Icons.Default.Upload, if (mode == CE_TELEPHONE_EST._SERVEUR) "To _AFFICHEUR" else "To _SERVEUR")
                }
            }
        }

        FloatingActionButton(
            onClick = { showOptions = !showOptions },
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showOptions) "Hide Options" else "Show Options"
            )
        }
    }
}
