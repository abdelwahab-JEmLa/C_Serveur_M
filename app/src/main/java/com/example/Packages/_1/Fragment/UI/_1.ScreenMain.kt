package com.example.Packages._1.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // Référence à la base de données
    val dbRef = remember {
        Firebase.database.getReference("0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase")
    }

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

    var currentItems by remember(visibleItems) { mutableStateOf(visibleItems) }

    val updateProductPosition: (AppsHeadModel.ProduitModel, Int) -> Unit = remember {
        { produit, nouvellePosition ->
            if (produit.bonCommendDeCetteCota == null) {
                produit.bonCommendDeCetteCota = AppsHeadModel.ProduitModel.GrossistBonCommandes()
            }
            produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = nouvellePosition

            // Update local state immediately
            currentItems = currentItems.map {
                if (it.id == produit.id) produit else it
            }

            // Then update Firebase
            dbRef.child(produit.id.toString()).setValue(produit)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = initViewModel.appsHead.produits_Main_DataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        currentItems = currentItems,
                        contentPadding = paddingValues,
                        onClick =updateProductPosition
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
