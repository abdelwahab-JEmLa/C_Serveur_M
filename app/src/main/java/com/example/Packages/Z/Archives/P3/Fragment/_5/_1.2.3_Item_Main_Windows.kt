package com.example.Packages.Z.Archives.P3.Fragment._5

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Packages.Z.Archives.P3.E.ViewModel.ViewModelFragment
import com.example.Packages._1.Fragment.Z.Archives.Model.Archives.Commende_Produits_Au_Grossissts_DataBase
import java.io.File

@Composable
fun WindowArticleDetail(
    article: Commende_Produits_Au_Grossissts_DataBase,
    onDismissWithUpdatePlaceArticle: () -> Unit,
    onDismissWithUpdateOfnonDispo: (Commende_Produits_Au_Grossissts_DataBase) -> Unit,
    onDismiss: () -> Unit,
    viewModelFragment: ViewModelFragment,
    modifier: Modifier = Modifier
) {
    val reloadKey = remember(article) { System.currentTimeMillis() }

    val infiniteTransition = rememberInfiniteTransition(label = "yellowPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yellowPulseAlpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .clickable { onDismissWithUpdatePlaceArticle() },
            shape = MaterialTheme.shapes.large,
            color = if (article.itsInFindedAskSupplierSA) Color.Blue.copy(alpha = 0.3f)
            else Color.Red.copy(alpha = 0.3f)
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = modifier.fillMaxWidth()) {
                    val ifStat =
                        article.color2SoldQuantity + article.color3SoldQuantity + article.color4SoldQuantity == 0
                    Box(
                        modifier = modifier
                            .clickable { onDismissWithUpdatePlaceArticle() }
                            .height(if (ifStat) 250.dp else 500.dp)
                    ) {
                        if (ifStat) {
                            SingleColorImageSA(article, viewModelFragment, reloadKey)
                        } else {
                            MultiColorGridSA(article, viewModelFragment, reloadKey)
                        }
                    }

                    // Article name
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onDismissWithUpdateOfnonDispo(article) },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                article.itsInFindedAskSupplierSA -> Color.Yellow.copy(alpha = alpha)
                                else -> Color.Red.copy(alpha = 0.3f)
                            }
                        )
                    ) {
                        AutoResizedText(
                            text = article.nameArticle.capitalize(Locale.current),
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }

                    // Client names
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        items(article.nameClientsNeedItGBC.split(")")) { clientName ->
                            val cleanedName = clientName.trim().replace("(", "").replace(")", "")
                            if (cleanedName.isNotBlank()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    AutoResizedText(
                                        text = cleanedName.capitalize(Locale.current),
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleColorImageSA(
    article: Commende_Produits_Au_Grossissts_DataBase,
    viewModelFragment: ViewModelFragment,
    reloadKey: Long
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imagePathWithoutExt =
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/${article.a_c_idarticle_c}_1"
            val imagePathWebp = "$imagePathWithoutExt.webp"
            val imagePathJpg = "$imagePathWithoutExt.jpg"
            val webpExists = File(imagePathWebp).exists()
            val jpgExists = File(imagePathJpg).exists()

            if (webpExists || jpgExists) {
//                Displaye_Image(
//                    article = article,
//                    index = 0,
//                    reloadKey
//                )
            } else {
                // Display rotated article name for empty articles
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = article.nameArticle,
                        color = Color.Red,
                        modifier = Modifier
                            .rotate(45f)
                            .padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (!article.a_d_nomarticlefinale_c_1.contains("Sta", ignoreCase = true)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = article.a_d_nomarticlefinale_c_1,
                        color = Color.Red,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "${article.totalquantity}",
                    color = Color.Red,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.6f))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MultiColorGridSA(article: Commende_Produits_Au_Grossissts_DataBase, viewModelFragment: ViewModelFragment,
                     reloadKey: Any = Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        val colorData = listOf(
            article.color1SoldQuantity to article.a_d_nomarticlefinale_c_1,
            article.color2SoldQuantity to article.a_d_nomarticlefinale_c_2,
            article.color3SoldQuantity to article.a_d_nomarticlefinale_c_3,
            article.color4SoldQuantity to article.a_d_nomarticlefinale_c_4
        )

        items(colorData.size) { index ->
            val (quantity, colorName) = colorData[index]
            if (quantity > 0) {
                ColorItemCard(article, index, quantity, colorName, viewModelFragment, reloadKey)
            }
        }
    }
}

@Composable
private fun ColorItemCard(
    article: Commende_Produits_Au_Grossissts_DataBase,
    index: Int,
    quantity: Int,
    colorName: String?,
    viewModelFragment: ViewModelFragment,
    reloadKey: Any = Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF40E0D0) // Bleu turquoise
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            val imagePathWithoutExt =
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/${article.a_c_idarticle_c}_${index + 1}"
            val imagePathWebp = "$imagePathWithoutExt.webp"
            val imagePathJpg = "$imagePathWithoutExt.jpg"
            val webpExists = File(imagePathWebp).exists()
            val jpgExists = File(imagePathJpg).exists()

            if (webpExists || jpgExists) {
//                Displaye_Image(
//                    article = article,
//                    index = index,
//                    reloadKey
//                )
            } else {
                Text(
                    text = colorName ?: "",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(45f)
                        .background(Color.White.copy(alpha = 0.6f))
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = quantity.toString(),
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.6f))
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )

        }
    }
}
