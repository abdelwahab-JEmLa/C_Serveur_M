// DisplayeImageById.kt
package com.example.Packages._3.Fragment.UI._5.Objects

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Dimension
import coil.size.Size
import com.example.c_serveur.R
import java.io.File
import kotlinx.coroutines.delay

private const val TAG = "CameraPickImageHandler"
private const val BASE_PATH = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

@Composable
internal fun DisplayeImageById(
    modifier: Modifier = Modifier,
    produit_Id: Long,
    produit_Image_Need_Update: Boolean = false,
    index: Int = 0,
    reloadKey: Any = Unit,
    contentDescription: String? = null,
    height_Defini: Dp? = null,
    width_Defini: Dp? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var retryCount by remember { mutableStateOf(0) }
    val maxRetries = 3

    // Add automatic retry mechanism for image loading
    LaunchedEffect(produit_Image_Need_Update, reloadKey) {
        if (produit_Image_Need_Update && retryCount < maxRetries) {
            delay(1000) // Wait before retry
            retryCount++
        }
    }

    val imageExist = remember(reloadKey, produit_Id, index, produit_Image_Need_Update, retryCount) {
        val supportedExtensions = listOf("jpg", "jpeg", "png", "webp")
        val baseImagePath = "$BASE_PATH/${produit_Id}_${index + 1}"

        supportedExtensions.firstNotNullOfOrNull { ext ->
            File("$baseImagePath.$ext").also { file ->
            }.takeIf { it.exists() }?.absolutePath
        }
    }

    val imageSize = calculateImageSize(height_Defini, width_Defini, density)
    val imageSource = imageExist ?: R.drawable.ic_launcher_background

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageSource)
            .size(imageSize)
            .crossfade(true)
            .listener(
                onStart = {
                },
                onError = { _, throwable ->
                    Log.e(TAG, "Error loading image for product $produit_Id")
                },
                onSuccess = { _, _ ->
                }
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

private fun calculateImageSize(height_Defini: Dp?, width_Defini: Dp?, density: androidx.compose.ui.unit.Density): Size {
    return with(density) {
        when {
            height_Defini != null && width_Defini != null -> {
                Size(
                    width = Dimension.Pixels(width_Defini.toPx().toInt()),
                    height = Dimension.Pixels(height_Defini.toPx().toInt())
                )
            }
            width_Defini != null -> {
                Size(
                    width = Dimension.Pixels(width_Defini.toPx().toInt()),
                    height = Dimension.Undefined
                )
            }
            height_Defini != null -> {
                Size(
                    width = Dimension.Undefined,
                    height = Dimension.Pixels(height_Defini.toPx().toInt())
                )
            }
            else -> Size.ORIGINAL
        }
    }
}

fun checkArticleImageExists(idArticle: Long, index: Int = 0): Boolean {
    val supportedExtensions = listOf("jpg", "jpeg", "png", "webp")
    val baseImagePath = "$BASE_PATH/${idArticle}_${index + 1}"

    return supportedExtensions.any { extension ->
        File("$baseImagePath.$extension").exists().also { exists ->
            Log.d(TAG, "Checking $baseImagePath.$extension: $exists")
        }
    }
}
