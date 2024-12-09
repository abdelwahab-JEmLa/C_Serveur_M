package com.example.Packages.P3.E.ViewModel.B.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Packages.P3.Ui_Statue_DataBase
import com.example.Packages.P3.E.ViewModel.ViewModelFragment

@Composable
fun VoiceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    uiState: Ui_Statue_DataBase,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Voice Input") },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                text = "Format: articleId + supplierName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    )
}

fun handleTwoPartInput(
    articleIdStr: String,
    supplierName: String,
    viewModelFragment: ViewModelFragment,
    uiState: Ui_Statue_DataBase,
) {
    val articleId = articleIdStr.toLongOrNull()
    if (articleId != null) {
        val article = uiState.commende_Produits_Au_Grossissts_DataBase.find {
            it.vid == articleId
        }
        val supplier = uiState.grossissts_DataBAse.find {
            it.nameInFrenche.equals(supplierName, ignoreCase = true)
        }

        if (article != null && supplier != null) {
            viewModelFragment.Move_Articles_To_Supplier(
                articlesToMove = listOf(article),
                toSupp = supplier.idSupplierSu
            )
        }
    }
}
