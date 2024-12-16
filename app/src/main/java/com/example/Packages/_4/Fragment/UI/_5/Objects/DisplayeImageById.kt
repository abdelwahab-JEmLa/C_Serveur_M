package com.example.Packages._4.Fragment.UI._5.Objects

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.size.Dimension
import com.example.c_serveur.R
import java.io.File

@Composable
internal fun DisplayeImageByIdd(
    modifier: Modifier = Modifier,
    produit_Id: Long,
    index: Int = 0,
    reloadKey: Any = Unit,
    contentDescription: String? = null,
    height_Defini: Dp? = null,
    width_Defini: Dp? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val baseImagePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/${produit_Id}_${index + 1}"
    val supportedExtensions = listOf("jpg", "jpeg", "png", "webp")

    val imageExist = remember(reloadKey, produit_Id, index) {
        supportedExtensions.firstNotNullOfOrNull { _ ->
            supportedExtensions.map { ext ->
                File("$baseImagePath.$ext")
            }.firstOrNull { file ->
                file.exists()
            }?.absolutePath
        }
    }

    val imageSource = imageExist ?: R.drawable.ic_launcher_background

    val imageSize = when {
        height_Defini != null && width_Defini != null -> {
            with(density) {
                Size(
                    width = Dimension.Pixels(width_Defini.toPx().toInt()),
                    height = Dimension.Pixels(height_Defini.toPx().toInt())
                )
            }
        }
        width_Defini != null -> {
            with(density) {
                Size(
                    width = Dimension.Pixels(width_Defini.toPx().toInt()),
                    height = Dimension.Undefined
                )
            }
        }
        height_Defini != null -> {
            with(density) {
                Size(
                    width = Dimension.Undefined,
                    height = Dimension.Pixels(height_Defini.toPx().toInt())
                )
            }
        }
        else -> Size.ORIGINAL
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageSource)
            .size(imageSize)
            .crossfade(true)
            .listener(
                onStart = { _ -> },
                onError = { _, _ -> }
            )
            .build()
    )

    Image(
        painter = painter,
        contentDescription = contentDescription ?: "Article Image $produit_Id",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center
    )
}

