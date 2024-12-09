package com.example.Packages.P1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import kotlinx.serialization.Serializable

/**
 * Object used for a type safe destination to a Home screen
 */
@Serializable
data class ClientProductsDisplayerStatsDestination(val route: String = "ClientProductsDisplayerStatsFragment") : java.io.Serializable

@Composable
fun ClientProductsDisplayerStatsFragment(
    viewModel: ClientProductsDisplayerStatsViewModel
) {
    // State observing and declarations
    val state by viewModel.state.collectAsStateWithLifecycle(UiStat())
    val actions = rememberFragmentsActions(viewModel)

    Screen(state,actions)
}
