package Z.WorkingOn.Fragment_2


import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import Z.WorkingOn.Fragment_2.D_MainItem.ExpandedMainItem_F2
import Z.WorkingOn.Fragment_2.D_MainItem.MainItem_F2

@Composable
fun MainList_F2(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelInitApp: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = visibleProducts.sortedBy { product ->
                product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                    ?: Int.MAX_VALUE
            },
            key = { product ->
                "${product.id}_${product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit}"
            }
        ) { product ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                // Always show the collapsed item
                MainItem_F2(
                    mainItem = product,
                    modifier = Modifier.fillMaxWidth(),
                    onCLickOnMain = {
                        expandedItemId = if (expandedItemId == product.id) null else product.id
                    }
                )

                // Animated expanded content
                AnimatedVisibility(
                    visible = expandedItemId == product.id,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = 0.9f,
                            stiffness = 300f
                        )
                    ),
                    exit = shrinkVertically(
                        animationSpec = spring(
                            dampingRatio = 0.9f,
                            stiffness = 300f
                        )
                    )
                ) {
                    ExpandedMainItem_F2(
                        mainItem = product,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        onCLickOnMain = { expandedItemId = null }
                    )
                }
            }
        }
    }
}
