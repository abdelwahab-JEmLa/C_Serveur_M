package com.example.Packages.P

import com.example.Packages.Z.Archives.P1.FragmentsActions
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Packages.Z.Archives.Models.ClientsDataBase
import com.example.Packages.Z.Archives.Models.DiviseurDeDisplayProductForEachClient
import com.example.Packages.Z.Archives.Models.Produits_DataBase

@Composable
internal fun ClientButton(
    modifier: Modifier,
    client: ClientsDataBase,
    actions: FragmentsActions,
    product: Produits_DataBase,
    diviseurDeDisplayProductForEachClient: List<DiviseurDeDisplayProductForEachClient>,
    standardStatProduct: DiviseurDeDisplayProductForEachClient?
) {

    val keyVidClientProductDisplayStat = "${client.idClientsSu}->${product.idArticle}"

    // Find the corresponding stat for this client and product
    val correspondent = diviseurDeDisplayProductForEachClient.find {
        it.keyVid == keyVidClientProductDisplayStat
    }


    // Enhanced null handling for stat determination
    val isDenied = correspondent?.deniedFromDislplayToClient
        ?: standardStatProduct?.deniedFromDislplayToClient
        ?: false

    val haveBigImg = correspondent?.itsBigImage
        ?: standardStatProduct?.itsBigImage
        ?: false

    // Determine the text to display
    val displayText = when {
        client.nameAggregation.isNotBlank() -> client.nameAggregation
        else -> client.nomClientsSu.take(3)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(android.graphics.Color.parseColor(client.couleurSu)))
            .clickable {
                when {
                    correspondent ==null  -> {
                        val newDeniedStatus = true
                        val newHaveBigImg = false
                        actions.upsertClientsProductDisplayeStat(
                            product.idArticle,
                            client.idClientsSu,
                            newDeniedStatus,
                            newHaveBigImg
                        )
                    }
                    correspondent.deniedFromDislplayToClient -> {
                        val newDeniedStatus = false
                        val newHaveBigImg = true
                        actions.upsertClientsProductDisplayeStat(
                            product.idArticle,
                            client.idClientsSu,
                            newDeniedStatus,
                            newHaveBigImg
                        )
                    }
                    correspondent.itsBigImage -> {
                        // Delete the specific client-product stat
                        actions.deleteClientsProductDisplayeStat(
                            product.idArticle,
                            client.idClientsSu
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                ( correspondent == null && standardStatProduct==null) -> Icon(
                    imageVector = Icons.Default.PhonelinkRing,
                    contentDescription = "Default",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(2.dp)
                )
                isDenied -> Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = "Denied",
                    tint = Color.White,
                    modifier = Modifier.padding(2.dp)
                )
                haveBigImg -> Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Big Image",
                    tint = Color.White,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        Text(
            text = displayText,
            color = Color.Red,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(2.dp)
        )
    }
}
