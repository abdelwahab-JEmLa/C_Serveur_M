package Z.WorkingOn.Fragment_2.D_MainItem

import Z.WorkingOn.Fragment_2.E.Dialogs.QuantitySelectionDialog
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.GlideDisplayImageBykeyId
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandedMainItem_F2(
    viewModelInitApp: ViewModelInitApp,
    mainItem: _ModelAppsFather.ProduitModel,
    modifier: Modifier = Modifier,
    onCLickOnMain: () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf<_ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel?>(null) }
    var selectedBonVent by remember { mutableStateOf<_ModelAppsFather.ProduitModel.ClientBonVentModel?>(null) }

    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onCLickOnMain() }
            .padding(8.dp)
    ) {
        // Header with image and basic info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideDisplayImageBykeyId(
                imageGlidReloadTigger = 0,
                mainItem = mainItem,
                modifier = Modifier
                    .width(350.dp)
                    .height(350.dp)
                ,
                size = 350.dp
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = mainItem.nom,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: ${mainItem.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // List of buyers and their purchases
        mainItem.bonsVentDeCetteCota.forEach { bonVent ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
                    .heightIn(max = 150.dp)

            ) {
                // Buyer info
                Text(
                    text = bonVent.clientInformations?.nom ?: "Unknown Client",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Colors grid for this buyer
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(bonVent.colours_Achete.filter { it.quantity_Achete > 0 }) { color ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp)
                                .clickable {
                                    selectedBonVent=bonVent
                                    selectedColor = color
                                    showDialog = true
                                }
                        ) {
                            Text(
                                text = color.imogi.ifEmpty { color.nom.take(2) },
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${color.quantity_Achete}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedColor != null) {
        QuantitySelectionDialog(
            onQuantitySelected = { quantity ->
                viewModelInitApp
                    .extension_App1_F2.changeColours_AcheteQuantity_Achete(
                    selectedBonVent,
                    mainItem,
                    selectedColor!!,
                    quantity
                )
            },
            onDismiss = { showDialog = false }
        )
    }
}
