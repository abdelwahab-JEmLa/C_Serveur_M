package com.example.Packages.Z.Archives.P1.Ui.Objects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class TableColumn<T>(
    val title: String,
    val weight: Float = 1f,
    val content: (T) -> String
)

@Composable
fun <T> TableGrid(
    items: List<T>,
    columns: List<TableColumn<T>>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            // Headers with enhanced styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                columns.forEach { column ->
                    GridHeader(
                        modifier = Modifier.weight(column.weight),
                        text = column.title
                    )
                }
            }

            // Data rows with enhanced styling
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    columns.forEach { column ->
                        GridCell(
                            modifier = Modifier.weight(column.weight),
                            text = column.content(item)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridHeader(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AutoResizedText(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun GridCell(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = modifier
            .height(44.dp),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            AutoResizedText(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Center
) {
    var fontSize by remember(text) { mutableStateOf(style.fontSize) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        textAlign = textAlign,
        onTextLayout = { textLayoutResult ->
            if (!readyToDraw) {
                if (textLayoutResult.hasVisualOverflow) {
                    fontSize *= 0.9f
                } else {
                    readyToDraw = true
                }
            }
        }
    )
}
