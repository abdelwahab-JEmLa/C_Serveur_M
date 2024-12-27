package com.example.Packages.Z.Archives.P3.F.FABs.Modules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.Packages.Z.Archives.Models.Grossissts_DataBAse
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Insert_Historical_PurchaseData
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Parent_Ui_Statue_DataBase_Update
import com.example.Packages.Z.Archives.P3.E.ViewModel.ViewModelFragment
import com.example.Packages.Z.Archives.P3.Ui_Statue_DataBase
import com.example.Packages._1.Fragment.Z.Archives.Model.Archives.Commende_Produits_Au_Grossissts_DataBase
import kotlinx.coroutines.launch

@Composable
internal fun SupplierButton(
    ui_state_dataBase: Ui_Statue_DataBase,
    viewModelFragment: ViewModelFragment,
    grossisst: Grossissts_DataBAse,
    allArticles: List<Commende_Produits_Au_Grossissts_DataBase>,
    showDescription: Boolean,
    isFirstClickedForReorder: Boolean,
    isReorderMode: Boolean,
    onClick: () -> Unit,
    showNoms: Boolean
) {
    val totalValue = remember(grossisst, allArticles) {
        allArticles
            .filter { it.idSupplierTSA.toLong() == grossisst.idSupplierSu }
            .sumOf { it.totalquantity * it.a_q_prixachat_c }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .widthIn(min = 50.dp, max = if (showNoms) 300.dp else 170.dp)
            .heightIn(max = if (showNoms) 100.dp else 40.dp)
    ) {
        if (showDescription) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "T: $${String.format("%.2f", totalValue)}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (showNoms) {
                        Text(
                            text = grossisst.nomSupplierSu,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = grossisst.nameInFrenche,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onClick()
                val supplierProductsWithoutHistory = ui_state_dataBase.commende_Produits_Au_Grossissts_DataBase.filter { commandeProduit ->
                    ui_state_dataBase.historique_D_Achate_Grossisst_DataBase.none { historique ->
                        historique.grossisst_id == grossisst.idSupplierSu &&
                                historique.produit_id == commandeProduit.a_c_idarticle_c
                    }
                }
                val productsToBeMoved = if (supplierProductsWithoutHistory.isEmpty()) {
                    ui_state_dataBase.commende_Produits_Au_Grossissts_DataBase.filter { it.pret_pour_deplace_au_grossisst }
                } else {
                    listOf(supplierProductsWithoutHistory.first())
                }
                if(!ui_state_dataBase.mode_click_is_trensfert_to_fab_gross) {
                    val grossisst_Au_Filtre_Mnt_A_Update =
                        if (ui_state_dataBase.grossisst_Au_Filtre_Mnt==null) {
                            grossisst
                        } else {
                            null
                        }
                        viewModelFragment.Parent_Ui_Statue_DataBase_Update(
                            "grossisst_Au_Filtre_Mnt",
                            grossisst_Au_Filtre_Mnt_A_Update
                        )
                }else{
                    viewModelFragment.Insert_Historical_PurchaseData(
                        grossisst,productsToBeMoved
                    )   
                    val produits_enleve_stat = productsToBeMoved.map { product ->
                        product.copy(
                            pret_pour_deplace_au_grossisst = false,
                            disponibylityStatInSupplierStore = ""
                        )
                    }
                    viewModelFragment.viewModelScope.launch {
                        viewModelFragment.dataBase.commende_Produits_Au_Grossissts_DataBase_Dao().upsertAll(produits_enleve_stat)
                    }
                }
            },
            modifier = Modifier.size(56.dp),
            containerColor = when {
                isFirstClickedForReorder -> MaterialTheme.colorScheme.tertiary
                isReorderMode -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                ui_state_dataBase.grossisst_Au_Filtre_Mnt == grossisst -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.secondary
            }
        ) {     //-> 
            Text(
                text = "${grossisst.classmentSupplier} ${MaterialTheme.typography.bodyLarge.fontSize}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
