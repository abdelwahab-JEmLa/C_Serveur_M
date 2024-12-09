package com.example.Main.StartFragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.serveurecherielhanaaebeljemla.Models.UiStat
import kotlinx.serialization.Serializable

/**
 * Object used for a type safe destination to a Home screen
 */
@Serializable
data class StartFragmentDestination(val route: String = "startFragment") : java.io.Serializable

@Composable
fun StartFragment(
    viewModel: StartFragmentViewModel
) {
    // State observing and declarations
    val state by viewModel.state.collectAsStateWithLifecycle(UiStat())
    val actions = rememberFragmentsActions(viewModel)

    Screen(state,actions)
}
