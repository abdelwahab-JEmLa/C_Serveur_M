package com.example.Packages.A_GrosssitsCommendHandler.Z_ActiveFragment

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages.A1_Fragment.SearchDialog
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.updateProduct
import com.example.Y_AppsFather.Kotlin.ViewModelProduits
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.ProduitModel

@Composable
fun B_ListMainFragment_1(
    viewModelProduits: ViewModelProduits,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val products = viewModelProduits.produitsMainDataBase.filter { it.isVisible }

    // Remember previous positions for animation
    val positionStates = remember {
        mutableMapOf<Long, MutableState<Float>>()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section des produits positionnés
        val positionedProducts = products
            .filter { it.bonCommendDeCetteCota?.cPositionCheyCeGrossit == true }
            .sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit }

        if (positionedProducts.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    "Produits avec position (${positionedProducts.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(
                items = positionedProducts,
                key = { it.id }
            ) { product ->
                val positionState = remember(product.id) {
                    positionStates.getOrPut(product.id) {
                        mutableStateOf(product.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit?.toFloat() ?: 0f)
                    }
                }

                // Animate position changes
                LaunchedEffect(product.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit) {
                    animate(
                        initialValue = positionState.value,
                        targetValue = product.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit?.toFloat() ?: 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) { value, _ ->
                        positionState.value = value
                    }
                }

                AnimatedContent(
                    targetState = product,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { if (targetState.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0 >
                                (initialState.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0)) 300 else -300 }
                        ) with slideOutHorizontally(
                            targetOffsetX = { if (targetState.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0 >
                                (initialState.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0)) -300 else 300 }
                        )
                    }
                ) { animatedProduct ->
                    C_ItemMainFragment_1(
                        mainItem = animatedProduct,
                        modifier = Modifier.animateItemPlacement(),
                        onCLickOnMain = {
                            animatedProduct.bonCommendDeCetteCota?.cPositionCheyCeGrossit = false
                            viewModelProduits.updateProduct(animatedProduct)
                        }
                    )
                }
            }
        }

        // Section des produits non positionnés
        val unpositionedProducts = products
            .filter { it.bonCommendDeCetteCota?.cPositionCheyCeGrossit != true }
            .sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit }

        if (unpositionedProducts.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { /* Implement drag functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Moving,
                            contentDescription = "Déplacer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        "Produits sans position (${unpositionedProducts.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            items(
                items = unpositionedProducts,
                key = { it.id }
            ) { product ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    C_ItemMainFragment_1(
                        mainItem = product,
                        modifier = Modifier.animateItemPlacement(),
                        onCLickOnMain = {
                            val newPosition = (positionedProducts.maxOfOrNull {
                                it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0
                            } ?: 0) + 1

                            product.bonCommendDeCetteCota?.apply {
                                cPositionCheyCeGrossit = true
                                positionProduitDonGrossistChoisiPourAcheterCeProduit = newPosition
                            }

                            if (product.itsTempProduit) {
                                product.statuesBase.prePourCameraCapture = true
                            }

                            viewModelProduits.updateProduct(product)
                        }
                    )
                }
            }
        }
    }

    SearchDialog(viewModelProduits)
}

// Extension function to help with animations
private suspend fun animate(
    initialValue: Float,
    targetValue: Float,
    animationSpec: AnimationSpec<Float>,
    onUpdate: (Float, Float) -> Unit
) {
    val anim = Animatable(initialValue)
    anim.animateTo(
        targetValue = targetValue,
        animationSpec = animationSpec,
    ) {
        onUpdate(value, velocity)
    }
}
