package com.example.Packages._1.Fragment.UI._3.ItemMainDifferentsAffichageModes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._3.Modules.Images_Handler.Glide_Display_Image_By_Id

@Composable
internal fun ItemMain(
    item: AppInitializeModel.Audio_DatasModel.AudioMarks_Model,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Glide_Display_Image_By_Id(
            produit_Id = 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )
        // Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = Color.Black.copy(alpha = 0.4f))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.tag,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.positionTime.toString(),
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Gray)
                )
            }
        }
    }
}
