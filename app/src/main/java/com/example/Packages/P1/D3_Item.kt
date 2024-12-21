package i_SupplierArticlesRecivedManager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Packages.P.ClientButton
import com.example.App_Produits_Main._1.Images_Handler.Glide_Display_Image_By_Id
import com.example.Packages.P1.FragmentsActions
import com.example.Models.ClientsDataBase
import com.example.Models.DiviseurDeDisplayProductForEachClient
import com.example.Models.Produits_DataBase

@Composable
internal fun Item(
    modifier: Modifier = Modifier,
    product: Produits_DataBase,
    reloadKey: Long = 0,
    clientsDataBase: List<ClientsDataBase>,
    diviseurDeDisplayProductForEachClient: List<DiviseurDeDisplayProductForEachClient>,
    actions: FragmentsActions,
) {
    val colorText = Color.Blue

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // Background image
            Glide_Display_Image_By_Id(
                produit_Id = product.idArticle,
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp),
                reloadKey = reloadKey
            )

            // Semi-transparent overlay
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            // Article details and client buttons
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Product name
                Text(
                    text = product.nomArticleFinale,
                    style = MaterialTheme.typography.titleSmall,
                    color = colorText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier=Modifier.height(50.dp)
                )
                val standardStatClient = ClientsDataBase(
                    idClientsSu = 100,
                    nomClientsSu = "StandartStatProductClient",
                    itsReadyForEdite = true ,
                    couleurSu =  "#FF0000"
                )
                val keyVidClientProductDisplayStat = "${100}->${product.idArticle}"
                val standartStatProduct = diviseurDeDisplayProductForEachClient.find {
                    it.keyVid == keyVidClientProductDisplayStat
                }

                val clientList = listOf(standardStatClient) + clientsDataBase.filter { it.itsReadyForEdite }
                // Client buttons grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7), // Changed to 3 columns
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(

                        clientList
                    ) { client ->
                        ClientButton(
                            modifier=Modifier.height(70.dp),
                            actions = actions,
                            product = product,
                            client = client,
                            diviseurDeDisplayProductForEachClient = diviseurDeDisplayProductForEachClient,
                            standardStatProduct= standartStatProduct
                        )
                    }
                }
            }
        }
    }
}
