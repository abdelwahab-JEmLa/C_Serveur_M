package com.example.Packages._3.Fragment.V.FABs.Modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.Models.Update_Parent_Ui_State_Var
import kotlin.math.roundToInt

@Composable
fun Grossissts_FloatingActionButtons_Grouped(
    modifier: Modifier = Modifier,
    grouped_Produits_Par_Id_Grossist: Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>>,
    ui_Mutable_State: Ui_Mutable_State,
) {
    var showLabels by remember { mutableStateOf(true) }
    var showFloatingButtons by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .zIndex(1f)
    ) {
        AnimatedVisibility(
            visible = showFloatingButtons,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                modifier = Modifier.wrapContentHeight(),
                color = Color.Transparent
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    grouped_Produits_Par_Id_Grossist.forEach { (supplierId, supplierProducts) ->
                        supplierProducts.firstOrNull()?.grossist_Choisi_Pour_Acheter_CeProduit?.let { supplier ->
                            FabButton(
                                supplierProductssize = supplierProducts.size,
                                label = supplier.nom,
                                color = Color(android.graphics.Color.parseColor(supplier.couleur)),
                                showLabel = showLabels,
                                isFiltered = ui_Mutable_State.selectedSupplierId == supplierId,
                                onClick = {
                                    handleSupplierClick(
                                        ui_Mutable_State = ui_Mutable_State,
                                        supplierId = supplierId,
                                        supplier = supplier
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showLabels = !showLabels },
            modifier = Modifier.size(48.dp),
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showLabels) Icons.Default.Close else Icons.AutoMirrored.Filled.Label,
                contentDescription = if (showLabels) "Hide Labels" else "Show Labels"
            )
        }
        FloatingActionButton(
            onClick = { showFloatingButtons = !showFloatingButtons },
            modifier = Modifier.size(48.dp),
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showFloatingButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showFloatingButtons) "Collapse" else "Expand"
            )
        }
    }
}

private fun handleSupplierClick(
    ui_Mutable_State: Ui_Mutable_State,
    supplierId: Long,
    supplier: Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit
) {
    when {
        ui_Mutable_State.mode_Trie_Produit_Non_Trouve -> {
            ui_Mutable_State.produits_Commend_DataBase
                .filter { it.non_Trouve }
                .forEach { produit ->
                    updateProduct(produit, supplier, ui_Mutable_State)
                }
        }
        ui_Mutable_State.mode_Update_Produits_Non_Defini_Grossist -> {
            ui_Mutable_State.produits_Commend_DataBase
                .firstOrNull { it.grossist_Choisi_Pour_Acheter_CeProduit?.id == 0L }
                ?.let { produit ->
                    updateProduct(produit, supplier, ui_Mutable_State)
                }
        }
        else -> {
            ui_Mutable_State.selectedSupplierId =
                if (ui_Mutable_State.selectedSupplierId == supplierId) 0L else supplierId
            ui_Mutable_State.Update_Parent_Ui_State_Var(ui_Mutable_State)
        }
    }
}

private fun updateProduct(
    produit: Ui_Mutable_State.Produits_Commend_DataBase,
    supplier: Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit,
    ui_Mutable_State: Ui_Mutable_State
) {
    val updatedProduct = produit.copy(
        non_Trouve = false,
        grossist_Choisi_Pour_Acheter_CeProduit = supplier
    )
    updatedProduct.updateSelf(ui_Mutable_State)
}

@Composable
private fun FabButton(
    label: String,
    color: Color,
    showLabel: Boolean,
    isFiltered: Boolean = false,
    onClick: () -> Unit,
    supplierProductssize: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedVisibility(
            visible = showLabel,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = if (isFiltered) Color.Red.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isFiltered) Color.White else color
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            containerColor = color
        ) {
            Text(
                text = supplierProductssize.toString(),
                modifier = Modifier,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White // Changed to white for better visibility
            )
        }
    }
}
