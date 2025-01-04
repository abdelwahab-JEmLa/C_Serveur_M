package com.example.Packages._2.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._2.Fragment.UI._5.FloatingActionButton.ClientsGroupedFABs
import com.example.Packages._2.Fragment.UI._5.FloatingActionButton.GlobalEditesGroupedFloatingActionButtons
import com.example.Packages._2.Fragment.ViewModel.Frag_ViewModel

internal const val DEBUG_LIMIT = 7

@Composable
internal fun ScreenMainFragment2(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    frag_ViewModel: Frag_ViewModel = viewModel(),
) {
    if (!initViewModel.initializationComplete) {
        LoadingScreen()
        return
    }

    val selectedClientData = remember {
        mutableStateOf(emptyMap<AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations,List<AppsHeadModel.ProduitModel>>())
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                if (initViewModel.appsHead.produitsMainDataBase.isNotEmpty()) {
                    ListMain(
                        visibleClientEtCesProduit = selectedClientData.value,
                        contentPadding = paddingValues,
                    )
                }
            }

            ClientsGroupedFABs(
                onClientSelected = { client, products ->
                    selectedClientData.value = mapOf(client to products.sortedBy { it.bonCommendDeCetteCota?.position_Grossist_Don_Parent_Grossists_List }
                        .sortedBy { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
                    )
                },
                produitsMainDataBase = initViewModel.appsHead.produitsMainDataBase,
                modifier = modifier
            )

            GlobalEditesGroupedFloatingActionButtons(
                produitsMainDataBase = initViewModel.appsHead.produitsMainDataBase,
                app_Initialize_Model = initViewModel.appsHead,
                modifier = modifier,
                fragment_Ui_State = frag_ViewModel.uiState
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            progress = { 0f },
            modifier = Modifier.align(Alignment.Center),
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
        )
    }
}

@Preview
@Composable
private fun PreviewFragmentMainScreen() {
    ScreenMainFragment2(modifier = Modifier.fillMaxSize())
}
