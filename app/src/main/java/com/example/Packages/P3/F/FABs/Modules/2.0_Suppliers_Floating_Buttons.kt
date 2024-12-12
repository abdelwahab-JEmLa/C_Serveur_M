package com.example.Packages.P3.F.FABs.Modules

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.Models.Grossissts_DataBAse
import com.example.Packages.P3.E.ViewModel.ViewModelFragment
import com.example.Packages.P3.Ui_Statue_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Commende_Produits_Au_Grossissts_DataBase
import kotlin.math.roundToInt

@Composable
internal fun Suppliers_Floating_Buttons(
    allArticles: List<Commende_Produits_Au_Grossissts_DataBase>,
    suppliers: List<Grossissts_DataBAse>,
    onClickFlotButt: (Long) -> Unit,
    supplierFlotBisHandled: Long?,
    itsReorderMode: Boolean,
    firstClickedSupplierForReorder: Long?,
    onToggleReorderMode: () -> Unit,
    onUpdateVocalArabName: (Long, String) -> Unit,
    onUpdateVocalFrencheName: (Long, String) -> Unit,
    viewModelFragment: ViewModelFragment,
    ui_state_dataBase: Ui_Statue_DataBase,
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isExpanded by remember { mutableStateOf(false) }
    var filterButtonsWhereArtNotEmpty by remember { mutableStateOf(false) }
    var showDescriptionFlotBS by remember { mutableStateOf(true) }
    var showNoms by remember { mutableStateOf(false) }
    var onToggleReorderModeCliked by remember { mutableStateOf(false) }

    val filteredSuppliers = remember(suppliers, allArticles, filterButtonsWhereArtNotEmpty) {
        if (filterButtonsWhereArtNotEmpty) {
            suppliers.filter { supplier ->
                allArticles.any { article ->
                    article.idSupplierTSA.toLong() == supplier.idSupplierSu && !article.itsInFindedAskSupplierSA
                }
            }
        } else {
            suppliers
        }
    }
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            supplierFlotBisHandled?.let { supplierId ->
                onUpdateVocalArabName(supplierId, spokenText)
            }
        }
    }
    val speechRecognizerLauncherFrenche = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            supplierFlotBisHandled?.let { supplierId ->
                onUpdateVocalFrencheName(supplierId, spokenText)
            }
        }
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    dragOffset += Offset(dragAmount.x, dragAmount.y)
                }
            }
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .widthIn(max = 100.dp)
                    ) {
                        item {
                            FloatingActionButton(
                                onClick = {
                                    val intent =
                                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(
                                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                            )
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
                                            putExtra(
                                                RecognizerIntent.EXTRA_PROMPT,
                                                "Parlez maintenant pour mettre à jour le nom vocal arabe du fournisseur..."
                                            )
                                        }
                                    speechRecognizerLauncherFrenche.launch(intent)
                                }
                            ) {
                                Icon(
                                    Icons.Default.MicOff,
                                    contentDescription = "Update vocal Frenche name"
                                )
                            }
                        }
                        item {
                            FloatingActionButton(
                                onClick = {
                                    val intent =
                                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(
                                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                            )
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-DZ")
                                            putExtra(
                                                RecognizerIntent.EXTRA_PROMPT,
                                                "Parlez maintenant pour mettre à jour le nom vocal arabe du fournisseur..."
                                            )
                                        }
                                    speechRecognizerLauncher.launch(intent)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = "Update vocal Arab name"
                                )
                            }
                        }
                        item {
                            FloatingActionButton(
                                onClick = {
                                    filterButtonsWhereArtNotEmpty = !filterButtonsWhereArtNotEmpty
                                }
                            ) {
                                Icon(
                                    if (filterButtonsWhereArtNotEmpty) Icons.Default.Close else Icons.Default.FilterAlt,
                                    contentDescription = if (filterButtonsWhereArtNotEmpty) "Clear filter" else "Filter suppliers"
                                )
                            }
                        }
                        item {
                            FloatingActionButton(
                                onClick = { showDescriptionFlotBS = !showDescriptionFlotBS }
                            ) {
                                Icon(
                                    if (showDescriptionFlotBS) Icons.Default.Close else Icons.Default.Dehaze,
                                    contentDescription = if (showDescriptionFlotBS) "Hide descriptions" else "Show descriptions"
                                )
                            }
                        }
                        item {
                            FloatingActionButton(
                                onClick = { showNoms = !showNoms }
                            ) {
                                Icon(
                                    if (showNoms) Icons.Default.Close else Icons.Default.Dehaze,
                                    contentDescription = if (showNoms) "Hide names" else "Show names"
                                )
                            }
                        }
                        item {
                            FloatingActionButton(
                                onClick = {
                                    onToggleReorderMode()
                                    onToggleReorderModeCliked = !onToggleReorderModeCliked
                                }
                            ) {
                                Icon(
                                    if (onToggleReorderModeCliked) Icons.Default.Close else Icons.Default.Autorenew,
                                    contentDescription = null
                                )
                            }
                        }

                    }
                }
                items(filteredSuppliers) { grossisst ->
                    SupplierButton(
                        ui_state_dataBase = ui_state_dataBase,
                        viewModelFragment = viewModelFragment,
                        grossisst = grossisst,
                        allArticles = allArticles,
                        showDescription = showDescriptionFlotBS,
                        isFirstClickedForReorder = firstClickedSupplierForReorder == grossisst.idSupplierSu,
                        isReorderMode = itsReorderMode,
                        onClick = {
                            onClickFlotButt(grossisst.idSupplierSu)
                        },
                        showNoms = showNoms
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                contentDescription = if (isExpanded) "Collapse supplier list" else "Expand supplier list"
            )
        }
    }
}
