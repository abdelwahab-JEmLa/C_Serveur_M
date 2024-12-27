package com.example.Packages.Z.Archives.P1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
internal fun rememberFragmentsActions(
    vM: ClientProductsDisplayerStatsViewModel
): FragmentsActions {
    val state by vM.state.collectAsState()

    return remember(vM, state) {
        FragmentsActions(
            onClick = {},
            onClickToUpdateitsReadyForEdite = { vM.updateClientReadyForEdit(it) },
            updateClientName = { it1, it2 -> vM.updatenameAggregation(it1, it2) },
            upsertClientsProductDisplayeStat = { it1, it2, it3, it4 ->
                vM.upsertDiviseurDeDisplayProductForEachClient(it1, it2, it3, it4)
            },
            deleteClientsProductDisplayeStat = { productId, clientId ->
                vM.deleteDiviseurDeDisplayProductForEachClient(productId, clientId)
            } ,
        )
    }
}

/**
 * Actions UI pour
 */
internal data class FragmentsActions(
    val onClick: () -> Unit = {},
    val onClickToUpdateitsReadyForEdite: (Long) -> Unit,
    val updateClientName: (Long, String) -> Unit = { _, _ -> },
    val upsertClientsProductDisplayeStat: (Long, Long, Boolean, Boolean) -> Unit = { _, _, _, _ -> },
    val deleteClientsProductDisplayeStat: (Long, Long) -> Unit
)
