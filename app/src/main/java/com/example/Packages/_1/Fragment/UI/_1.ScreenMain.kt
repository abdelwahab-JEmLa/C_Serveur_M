package com.example.Packages._1.Fragment.UI

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.Grossissts_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.ViewModel.F3_ViewModel

private const val TAG = "ScreenMain"
private const val DEBUG_LIMIT = 7

@Composable
internal fun ScreenMain(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    p3_ViewModel: F3_ViewModel = viewModel(),
) {
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

    // Create an immutable snapshot of the database list
    val currentItems by
    remember(initViewModel._appsHead.produits_Main_DataBase) {
        // Log the first 7 products
        initViewModel._appsHead.produits_Main_DataBase.take(DEBUG_LIMIT).forEach { product ->
            Log.d(TAG, """
                Product ${product.id}:
                Name: ${product.nom}
                Reference: ${product.it_ref_don_FireBase}
                Colors/Flavors: ${product.coloursEtGouts.size}
                Visible: ${product.isVisible}
                Needs Update: ${product.besoin_To_Be_Updated}
                """.trimIndent()
            )
        }

        mutableStateOf(initViewModel._appsHead.produits_Main_DataBase)
    }

    // With this:
    val visibleItems by remember(currentItems) {
        derivedStateOf { currentItems.filter { it.isVisible }.toMutableStateList() }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = initViewModel.appsHead.produits_Main_DataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        visibleItems = visibleItems,
                        contentPadding = paddingValues,
                        viewModelScope=initViewModel.viewModelScope
                    )
                }
            }

            Grossissts_FloatingActionButtons_Grouped(
                headViewModel = initViewModel,
                modifier = modifier,
                ui_State = p3_ViewModel.uiState,
                appsHeadModel = initViewModel.appsHead,
            )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = modifier,
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
