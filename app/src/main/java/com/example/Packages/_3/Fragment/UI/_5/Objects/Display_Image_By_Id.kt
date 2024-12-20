package com.example.Packages._3.Fragment.UI._5.Objects

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.c_serveur.R
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "CameraPickImageHandler"
private const val BASE_PATH = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
private const val IMAGE_QUALITY = 30 // Reduced quality to 30%

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun Display_Image_By_Id(
    modifier: Modifier = Modifier,
    produit_Id: Long,
    produit_Image_Need_Update: Boolean = false,
    index: Int = 0,
    reloadKey: Any = Unit,
    contentDescription: String? = null,
    height_Defini: Dp? = null,
    width_Defini: Dp? = null,
    cornerRadius: Dp = 8.dp
) {
    var retryCount by remember { mutableStateOf(0) }
    val maxRetries = 3
    var currentQuality by remember { mutableStateOf(5f) }
    var isLoading by remember { mutableStateOf(true) }
    var imageLoaded by remember { mutableStateOf(false) }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    LaunchedEffect(produit_Image_Need_Update, reloadKey) {
        if (produit_Image_Need_Update && retryCount < maxRetries) {
            delay(1000)
            retryCount++
        }
    }

    LaunchedEffect(reloadKey) {
        isLoading = true
        imageLoaded = false
        currentQuality = 5f
        delay(300)
        currentQuality = IMAGE_QUALITY.toFloat()
        imageLoaded = true
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        key1 = produit_Id,
        key2 = index,
        key3 = reloadKey
    ) {
        value = withContext(Dispatchers.IO) {
            val baseImagePath = "$BASE_PATH/${produit_Id}_${index + 1}"
            listOf("jpg", "jpeg", "png", "webp")
                .asSequence()
                .map { ext -> File("$baseImagePath.$ext") }
                .firstOrNull { it.exists() && it.canRead() }
        }
    }

    Box(
        modifier = modifier.then(
            if (height_Defini != null && width_Defini != null) {
                Modifier.size(width = width_Defini, height = height_Defini)
            } else {
                Modifier.fillMaxSize()
            }
        )
    ) {
        imageFile?.let { file ->
            GlideImage(
                model = file,
                contentDescription = contentDescription ?: "Product Image $produit_Id",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .graphicsLayer {
                        if (blurRadius > 0f) {
                            renderEffect = BlurEffect(
                                radiusX = blurRadius,
                                radiusY = blurRadius,
                                edgeTreatment = TileMode.Decal
                            )
                        }
                    }
            ) { requestBuilder ->
                requestBuilder
                    .thumbnail(
                        requestBuilder.clone()
                            .transform(jp.wasabeef.glide.transformations.BlurTransformation(10))
                    )
                    .downsample(DownsampleStrategy.AT_MOST)
                    .encodeQuality(IMAGE_QUALITY)
                    .error(R.drawable.ic_launcher_background)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(com.bumptech.glide.Priority.HIGH)
                    .signature(ObjectKey("${produit_Id}_${index}_${currentQuality}"))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e(TAG, "Error loading image for product $produit_Id: ${e?.message}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            if (isFirstResource && currentQuality < IMAGE_QUALITY.toFloat()) {
                                currentQuality = IMAGE_QUALITY.toFloat()
                            }
                            isLoading = false
                            return false
                        }
                    })
            }
        } ?: GlideImage(
            model = R.drawable.ic_launcher_background,
            contentDescription = "Fallback Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}
