package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id2_TravaillieurListProduitAchercheChezLeGrossist.D_MainItem.ExpandedMainItem_F2
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.ExpandedMainItem_F3
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.MainItem_F3
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.LogUtils.logProductFilter
import androidx.compose.animation.AnimatedVisibility
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
    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    // Get products for current client
    val products = viewModelInitApp._modelAppsFather.groupedProductsParClients
        .find { it.key.id == viewModelInitApp.frag_3A1_ExtVM.iDClientAuFilter }
        ?.value
        .orEmpty()
        .filter { it.bonCommendDeCetteCota?.mutableBasesStates?.cPositionCheyCeGrossit == true }

    // Log for debugging
    products.forEach {
        viewModelInitApp.frag_3A1_ExtVM.iDClientAuFilter?.let { it1 ->
            logProductFilter(it, it1,
            viewModelInitApp._modelAppsFather.grossistsDataBase)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group and sort products by grossist
        val groupedProducts = products
            .groupBy {
                viewModelInitApp._modelAppsFather.grossistsDataBase
                    .find { grossist -> grossist.id == it.bonCommendDeCetteCota?.idGrossistChoisi }
            }
            .filterKeys { it != null }
            .toSortedMap(compareBy { it?.statueDeBase?.itPositionInParentList })

        // Display regular products
        groupedProducts.forEach { (grossist, grossistProducts) ->
            if (grossist != null) {
                val regularProducts = grossistProducts.filter {
                    !it.statuesBase.seTrouveAuDernieDuCamionCarCCarton
                }

                if (regularProducts.isNotEmpty()) {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(android.graphics.Color.parseColor(grossist.statueDeBase.couleur)))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = grossist.nom,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (grossist.statueDeBase.couleur == "#FFFFFF")
                                    Color.Black else Color.White
                            )
                        }
                    }

                    items(regularProducts) { product ->
                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                            MainItem_F3(
                                viewModelProduits = viewModelInitApp,
                                mainItem = product,
                                modifier = Modifier.fillMaxWidth(),
                                onCLickOnMain = {
                                    expandedItemId = if (expandedItemId == product.id) null
                                    else product.id
                                }
                            )

                            AnimatedVisibility(
                                visible = expandedItemId == product.id,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                ExpandedMainItem_F2(
                                    viewModelInitApp = viewModelInitApp,
                                    mainItem = product,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    onCLickOnMain = { expandedItemId = null }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Display carton products
        val cartonProducts = products.filter { it.statuesBase.seTrouveAuDernieDuCamionCarCCarton }
        if (cartonProducts.isNotEmpty()) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Produits Type: Carton",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            items(cartonProducts) { product ->
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    MainItem_F3(
                        viewModelProduits = viewModelInitApp,
                        mainItem = product,
                        modifier = Modifier.fillMaxWidth(),
                        onCLickOnMain = {
                            expandedItemId = if (expandedItemId == product.id) null
                            else product.id
                        }
                    )

                    AnimatedVisibility(
                        visible = expandedItemId == product.id,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        ExpandedMainItem_F3(
                            viewModelInitApp = viewModelInitApp,
                            mainItem = product,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            onCLickOnMain = { expandedItemId = null }
                        )
                    }
                }
            }
        }
    }
}
