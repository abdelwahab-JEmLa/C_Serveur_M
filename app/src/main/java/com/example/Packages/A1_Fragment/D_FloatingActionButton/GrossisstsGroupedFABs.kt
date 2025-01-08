package com.example.Packages.A1_Fragment.D_FloatingActionButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.roundToInt

@Composable
fun GrossisstsGroupedFABsFragment_1(
    produitsMainDataBase: List<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    onClick: (Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>?) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    // Firebase reference
    val database = FirebaseDatabase.getInstance()
    val mapsFireBaseRef = database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("A_CodingWithListsPatterns")

    var grossistList by remember(produitsMainDataBase) {
        mutableStateOf<List<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>>>(
            emptyList()
        )
    }

    // LaunchedEffect to handle Firebase operations
    LaunchedEffect(produitsMainDataBase) {
        if (true) {
            startImplementation(produitsMainDataBase, mapsFireBaseRef)
        }

        // Listen for changes
        mapsFireBaseRef
            .child("filteredAndGroupedData")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updatedList = snapshot.children.mapNotNull { grossistSnapshot ->
                        try {
                            val grossist = grossistSnapshot.child("first")
                                .getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)
                            val produits =
                                grossistSnapshot.child("second").children.mapNotNull { produitSnapshot ->
                                    produitSnapshot.getValue(AppsHeadModel.ProduitModel::class.java)
                                }
                            if (grossist != null) {
                                grossist to produits
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    grossistList = updatedList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    println("Firebase Error: ${error.message}")
                }
            })
    }

    var visibleGrossistAssociatedProduits by remember {
        mutableStateOf<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>
        ?>(null)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main FAB
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Hide" else "Show"
                )
            }

            // Animated content
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grossistList.forEachIndexed { index, entry ->
                        val (grossist, produits) = entry
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        grossistList = grossistList.toMutableList().apply {
                                            val temp = this[index]
                                            this[index] = this[index - 1]
                                            this[index - 1] = temp
                                        }
                                    },
                                    modifier = Modifier.size(36.dp),
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandLess,
                                        contentDescription = "Move Up"
                                    )
                                }
                            }

                            Text(
                                text = grossist.nom,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        if (visibleGrossistAssociatedProduits?.first == grossist) Color.Blue
                                        else Color.Transparent
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    visibleGrossistAssociatedProduits = entry
                                    onClick(visibleGrossistAssociatedProduits)
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(grossist.couleur))
                            ) {
                                Text(
                                    text = produits.size.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun startImplementation(
    produitsMainDataBase: List<AppsHeadModel.ProduitModel>,
    mapsFireBaseRef: DatabaseReference
) {
    val filteredAndGroupedData = produitsMainDataBase
        .filter { it.bonCommendDeCetteCota?.grossistInformations != null }
        .groupBy { it.bonCommendDeCetteCota!!.grossistInformations!! }
        .toList()

    // Update Firebase
    mapsFireBaseRef
        .child("filteredAndGroupedData")
        .setValue(filteredAndGroupedData)
}
