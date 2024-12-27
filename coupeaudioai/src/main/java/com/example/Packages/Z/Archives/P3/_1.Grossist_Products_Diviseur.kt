package com.example.Packages.Z.Archives.P3

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.Packages._1.Fragment.UI.Main_Screen_Fragment
import com.example.Packages.Z.Archives.P3.F.FABs.Modules.Global_Controls_FloatingActionButtons
import com.example.Packages.Z.Archives.P3.F.FABs.Modules.Suppliers_Floating_Buttons
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Move_Articles_To_Supplier
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Reorder_Suppliers
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Update_Supplier_Vocal_Arab_Name
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Update_Supplier_Vocal_French_Name
import com.example.Packages.Z.Archives.P3.E.ViewModel.ViewModelFragment

@Composable
fun Grossist_Products_Diviseur_Fragment(
    viewModelFragment: ViewModelFragment
) {
    val ui_statu_dataBase by viewModelFragment.Ui_State_DataBase.collectAsState()
    var showFloatingButtons by remember { mutableStateOf(false) }
    var gridColumns by remember { mutableIntStateOf(2) }
    var voiceInputText by remember { mutableStateOf("") }
    var toggleCtrlToFilterToMove by remember { mutableStateOf(false) }
    var idSupplierOfFloatingButtonClicked by remember { mutableStateOf<Long?>(null) }
    var itsReorderMode by remember { mutableStateOf(false) }
    var firstClickedSupplierForReorder by remember { mutableStateOf<Long?>(null) }
    var itsMoveFirstNonDefined by remember { mutableStateOf(false) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            voiceInputText = spokenText
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Main_Screen_Fragment(
                modifier =Modifier.padding(padding),
            )

            Global_Controls_FloatingActionButtons(
                modifier = Modifier.align(Alignment.Center),
                showFloatingButtons = showFloatingButtons,
                onToggleFloatingButtons = { showFloatingButtons = !showFloatingButtons },
                onChangeGridColumns = { gridColumns = it },
                onToggleToFilterToMove = { toggleCtrlToFilterToMove = !toggleCtrlToFilterToMove },
                filterSuppHandledNow = toggleCtrlToFilterToMove,
                onLaunchVoiceRecognition = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-DZ")
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant pour mettre à jour cet article...")
                    }
                    speechRecognizerLauncher.launch(intent)
                },
                viewModelFragment = viewModelFragment,
                uiState = ui_statu_dataBase,
                onToggleMoveFirstNonDefined = { itsMoveFirstNonDefined = !itsMoveFirstNonDefined })

            Suppliers_Floating_Buttons(
                ui_state_dataBase= ui_statu_dataBase,
                viewModelFragment=viewModelFragment,
                allArticles = ui_statu_dataBase.commende_Produits_Au_Grossissts_DataBase,
                suppliers = ui_statu_dataBase.grossissts_DataBAse,
                supplierFlotBisHandled = idSupplierOfFloatingButtonClicked,
                onClickFlotButt = { clickedSupplierId ->
                    if (itsReorderMode) {
                        if (firstClickedSupplierForReorder == null) {
                            firstClickedSupplierForReorder = clickedSupplierId
                        } else if (firstClickedSupplierForReorder != clickedSupplierId) {
                            viewModelFragment.Reorder_Suppliers(firstClickedSupplierForReorder!!, clickedSupplierId)
                            firstClickedSupplierForReorder = null
                        } else {
                            firstClickedSupplierForReorder = null
                        }
                    } else {
                        if (toggleCtrlToFilterToMove) {
                            val filterBytabelleSupplierArticlesRecived =
                                ui_statu_dataBase.commende_Produits_Au_Grossissts_DataBase.filter {
                                    it.itsInFindedAskSupplierSA
                                }
                            viewModelFragment.Move_Articles_To_Supplier(
                                articlesToMove = filterBytabelleSupplierArticlesRecived,
                                toSupp = clickedSupplierId
                            )
                            toggleCtrlToFilterToMove = false
                        } else {
                            idSupplierOfFloatingButtonClicked = when (idSupplierOfFloatingButtonClicked) {
                                clickedSupplierId -> null
                                else -> clickedSupplierId
                            }
                        }
                    }
                },
                itsReorderMode = itsReorderMode,
                firstClickedSupplierForReorder = firstClickedSupplierForReorder,
                onUpdateVocalArabName = { supplierId, newName ->
                    viewModelFragment.Update_Supplier_Vocal_Arab_Name(supplierId, newName)
                },
                onUpdateVocalFrencheName = { supplierId, newName ->
                    viewModelFragment.Update_Supplier_Vocal_French_Name(supplierId, newName)
                },
                onToggleReorderMode = {
                    itsReorderMode = !itsReorderMode
                    if (!itsReorderMode) {
                        firstClickedSupplierForReorder = null
                    }
                }
            )

//        displayedArticle?.let { article ->
//            WindowArticleDetail(
//                article = article,
//                onDismissWithUpdatePlaceArticle = {
//                    redisplayedDetailerChanger = null
//                },
//                onDismissWithUpdateOfnonDispo = { updatedArticle ->
//                    redisplayedDetailerChanger = null
//                    viewModelFragment.Update_Article_Status(updatedArticle)
//                },
//                onDismiss = {
//                    redisplayedDetailerChanger = null
//                },
//                viewModelFragment = viewModelFragment,
//                modifier = Modifier.padding(horizontal = 3.dp)
//            )
//        }
        }
    }
}
