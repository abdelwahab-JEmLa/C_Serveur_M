package com.example.Main.StartFragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
internal fun rememberFragmentsActions(
    vM: StartFragmentViewModel
): FragmentsActions {
    val state by vM.state.collectAsState()

    return remember(vM, state) {
        FragmentsActions(
            updateProductCategoryReferences = {  vM.updateProductCategoryReferences() },
            importClientsDataBase = {
                vM.importClientsDataBase()
            }
        )
    }
}

/**
 * Actions UI pour
 */
internal data class FragmentsActions(
    val updateProductCategoryReferences: () -> Unit,
    val importClientsDataBase: () -> Unit
)
