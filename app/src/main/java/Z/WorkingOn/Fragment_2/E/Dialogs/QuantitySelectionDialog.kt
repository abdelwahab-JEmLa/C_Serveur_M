package Z.WorkingOn.Fragment_2.E.Dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// QuantitySelectionDialog.kt
@Composable
fun QuantitySelectionDialog(
    onQuantitySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Quantity") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(50) { quantity ->
                    Button(
                        onClick = {
                            onQuantitySelected(quantity + 1)
                            onDismiss()
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth() // Added to ensure button takes full width
                    ) {
                        Text(
                            text = (quantity + 1).toString(),
                            style = MaterialTheme.typography.bodyMedium // Added style
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
