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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GlobalEditesGroupedFloatingActionButtons
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GrossisstsGroupedFABs
import com.example.Packages._1.Fragment.ViewModel.Frag_ViewModel

internal const val DEBUG_LIMIT = 7

@Composable
internal fun ScreenMain(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    frag_ViewModel: Frag_ViewModel = viewModel(),
) {
    val TAG = "ScreenMain"
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
    var produitsMainDataBase by
    remember(initViewModel._appsHeadModel.produitsMainDataBase) {
        // Log the first 7 products
        initViewModel._appsHeadModel.produitsMainDataBase.take(DEBUG_LIMIT).forEach { product ->
            Log.d(TAG, """
                Product ${product.id}:
                Name: ${product.nom}
                Colors/Flavors: ${product.coloursEtGouts.size}
                Visible: ${product.isVisible}
                Needs Update: ${product.besoin_To_Be_Updated}
                """.trimIndent()
            )
        }

        mutableStateOf(initViewModel._appsHeadModel.produitsMainDataBase)
    }

    // With this:
    val visibleItems by remember(produitsMainDataBase) {
        derivedStateOf { produitsMainDataBase.filter { it.isVisible }.toMutableStateList() }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = initViewModel.appsHead.produitsMainDataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        visibleItems = visibleItems,
                        contentPadding = paddingValues,
                        viewModelScope=initViewModel.viewModelScope
                    )
                }
            }

            GrossisstsGroupedFABs(
                onClickFAB = {produitsMainDataBase=it},
                produitsMainDataBase=produitsMainDataBase,
                modifier = modifier
            )

            GlobalEditesGroupedFloatingActionButtons(
                produitsMainDataBase=produitsMainDataBase,
                app_Initialize_Model = initViewModel.appsHead,
                modifier = modifier,
                fragment_Ui_State = frag_ViewModel.uiState
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    ScreenMain(modifier = Modifier.fillMaxSize())
}
