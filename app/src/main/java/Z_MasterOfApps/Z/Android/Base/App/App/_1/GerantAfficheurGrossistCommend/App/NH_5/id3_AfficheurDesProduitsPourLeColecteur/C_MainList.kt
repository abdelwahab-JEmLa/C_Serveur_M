package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur

import Z_MasterOfApps.Kotlin.Model.A_ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id2_TravaillieurListProduitAchercheChezLeGrossist.D_MainItem.ExpandedMainItem_F2
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.ExpandedMainItem_F3
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.MainItem_F3
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.LogUtils.logProductFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_F3(
    viewModelInitApp: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val frag_3A1_ExtVM = viewModelInitApp.frag_3A1_ExtVM

    // Get all products for this client, including both positioned and unpositioned ones
    val allClientProducts = viewModelInitApp._modelAppsFather
        .groupedProductsParClients.find {
            it.key.id == frag_3A1_ExtVM.iDAuFilter
        }
        ?.value.orEmpty()

    // Log all products for debugging
    allClientProducts.forEach { product ->
        frag_3A1_ExtVM.iDAuFilter?.let {
            logProductFilter(
                product = product,
                clientId = it,
                grossists = viewModelInitApp._modelAppsFather.grossistsDataBase
            )
        }
    }

    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    // Split products into regular and carton products
    val (etagersProduits, cartonsSectionProsduits) = allClientProducts.partition {
        !it.statuesBase.seTrouveAuDernieDuCamionCarCCarton
    }

    // Group regular products by grossist, including all products regardless of position
    val groupedRegularProducts = etagersProduits
        .groupBy { product ->
            product.bonCommendDeCetteCota?.idGrossistChoisi
        }
        .filterKeys { it != null }
        .mapKeys { (grossistId, _) ->
            viewModelInitApp._modelAppsFather.grossistsDataBase.find { it.id == grossistId }
        }
        .filterKeys { it != null }
        .toSortedMap(compareBy { grossist ->
            grossist?.statueDeBase?.itPositionInParentList ?: Int.MAX_VALUE
        })

    // Sort carton products
    val sortedCartonProducts = cartonsSectionProsduits.sortedWith(
        compareBy<A_ProduitModel> { product ->
            val grossistPosition = product.bonCommendDeCetteCota?.idGrossistChoisi?.let { grossistId ->
                viewModelInitApp._modelAppsFather.grossistsDataBase
                    .find { it.id == grossistId }
                    ?.statueDeBase
                    ?.itPositionInParentList
            } ?: Int.MAX_VALUE
            grossistPosition
        }.thenBy {
            it.bonCommendDeCetteCota?.mutableBasesStates?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: Int.MAX_VALUE
        }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Regular products sections
        groupedRegularProducts.forEach { (grossist, products) ->
            if (grossist != null) {
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(
                                    android.graphics.Color.parseColor(
                                        grossist.statueDeBase.couleur
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = grossist.nom,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (grossist.statueDeBase.couleur == "#FFFFFF") {
                                Color.Black
                            } else {
                                Color.White
                            }
                        )
                    }
                }

                items(
                    items = products.sortedBy { product ->
                        product.bonCommendDeCetteCota?.mutableBasesStates?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                            ?: Int.MAX_VALUE
                    },
                ) { product ->
                    ProductItem(
                        viewModelInitApp = viewModelInitApp,
                        product = product,
                        expandedItemId = expandedItemId,
                        onExpandedItemIdChange = { expandedItemId = it }
                    )
                }
            }
        }

        // Carton products section
        if (sortedCartonProducts.isNotEmpty()) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Produits Type: Carton",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            items(items = sortedCartonProducts) { product ->
                ProductItem(
                    viewModelInitApp = viewModelInitApp,
                    product = product,
                    expandedItemId = expandedItemId,
                    onExpandedItemIdChange = { expandedItemId = it }
                )
            }
        }
    }
}

@Composable
private fun ProductItem(
    viewModelInitApp: ViewModelInitApp,
    product: A_ProduitModel,
    expandedItemId: Long?,
    onExpandedItemIdChange: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        MainItem_F3(
            viewModelProduits = viewModelInitApp,
            mainItem = product,
            modifier = Modifier.fillMaxWidth(),
            onCLickOnMain = {
                onExpandedItemIdChange(
                    if (expandedItemId == product.id) null else product.id
                )
            }
        )

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
            if (product.statuesBase.seTrouveAuDernieDuCamionCarCCarton) {
                ExpandedMainItem_F3(
                    viewModelInitApp = viewModelInitApp,
                    mainItem = product,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onCLickOnMain = { onExpandedItemIdChange(null) }
                )
            } else {
                ExpandedMainItem_F2(
                    viewModelInitApp = viewModelInitApp,
                    mainItem = product,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onCLickOnMain = { onExpandedItemIdChange(null) }
                )
            }
        }
    }
}
