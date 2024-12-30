package com.example.Packages._1.Fragment.UI

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.Grossissts_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.ViewModel.F3_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
internal fun ScreenMain(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    p3_ViewModel: F3_ViewModel = viewModel(),
) {
    val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    val baseRef = Firebase.database.getReference(CHEMIN_BASE)
    if (!initViewModel.initializationComplete) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    initViewModel.initializationProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    val visibleItems = initViewModel._appsHead.produits_Main_DataBase.filter { it.isVisible }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = initViewModel.appsHead.produits_Main_DataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        visibleItems = visibleItems,
                        ui_State = p3_ViewModel.uiState,
                        contentPadding = paddingValues,
                        onClickDelete = { itemClicke ->
                            visibleItems.find { it.id == itemClicke.id }?.let { item ->
                                item.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                            }
                            baseRef.setValue(itemClicke)
                        },
                        onCLickOnMain = {
                            // Find the maximum position among existing products
                            val maxPosition = initViewModel._appsHead.produits_Main_DataBase
                                .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
                                .filter { it >= 0 }
                                .maxOrNull() ?: -1

                            Log.d("onClickMainCard", "Current max position: $maxPosition")

                            // Calculate new position
                            val newPosition = maxPosition + 1

                            // Create or update bonCommendDeCetteCota if necessary
                            if (it.bonCommendDeCetteCota == null) {
                                it.bonCommendDeCetteCota = AppsHeadModel.ProduitModel.GrossistBonCommandes()
                            }

                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition

                            baseRef.setValue(it)
                        }
                    )
                }
            }

            Grossissts_FloatingActionButtons_Grouped(
                headViewModel = initViewModel,
                modifier = modifier,  // Correction 4: Utilisation du modifier passé en paramètre
                ui_State = p3_ViewModel.uiState,
                app_Initialize_Model = initViewModel.appsHead
            )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = modifier,  // Correction 4: Utilisation du modifier passé en paramètre
                fragment_Ui_State = p3_ViewModel.uiState,
                app_Initialize_Model = initViewModel.appsHead
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    ScreenMain(modifier = Modifier.fillMaxSize())
}
