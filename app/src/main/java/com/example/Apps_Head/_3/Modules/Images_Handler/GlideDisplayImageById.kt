package com.example.Apps_Head._3.Modules.Images_Handler

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.imagesProduitsLocalExternalStorageBasePath
import com.example.Apps_Head._2.ViewModel.InitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val IMAGE_QUALITY = 3
private const val TAG = "GlideImageDebug"
private const val MIN_RELOAD_INTERVAL = 500L // Minimum time between reloads in milliseconds

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideDisplayImageById(
    initViewModel: InitViewModel,
    modifier: Modifier = Modifier,
    produit_Id: Long,
    sonImageBesoinActualisation: Boolean = false,
    index: Int = 0,
    reloadKey: Any = Unit,
    contentDescription: String? = null,
    height_Defini: Dp? = null,
    width_Defini: Dp? = null,
    cornerRadius: Dp = 8.dp,
    onRelodeDonne: () -> Unit
) {
    Log.d(TAG, "Starting GlideDisplayImageById - productId: $produit_Id, index: $index")

    var isLoading by remember { mutableStateOf(true) }
    var forceReload by remember { mutableStateOf(0) }
    var currentQuality by remember { mutableStateOf(IMAGE_QUALITY.toFloat()) }
    var reloadSuccess by remember { mutableStateOf(false) }
    var previousTrigger by remember { mutableStateOf(0) }
    var lastReloadTimestamp by remember { mutableStateOf(0L) }

    // Animate blur effect
    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    // Configure box modifier
    val boxModifier = modifier.then(
        if (height_Defini != null && width_Defini != null) {
            Modifier.size(width = width_Defini, height = height_Defini)
        } else {
            Modifier.fillMaxSize()
        }
    )

    // Handle reload triggers and state updates
    LaunchedEffect(produit_Id, sonImageBesoinActualisation, reloadKey) {
        initViewModel.appsHead.produitsMainDataBase.find { it.id == produit_Id }?.let { product ->
            val currentTime = System.currentTimeMillis()
            val currentTrigger = product.statuesBase.imageGlidReloadTigger

            if (currentTime - lastReloadTimestamp > MIN_RELOAD_INTERVAL &&
                (currentTrigger != previousTrigger || sonImageBesoinActualisation)
            ) {
                Log.d(TAG, "Initiating reload sequence - trigger: $currentTrigger")
                lastReloadTimestamp = currentTime
                previousTrigger = currentTrigger
                forceReload++
                isLoading = true

                // Update product state
                if (product.statuesBase.sonImageBesoinActualisation) {
                    delay(1000) // Wait for file system operations
                    product.statuesBase.sonImageBesoinActualisation = false
                    product.besoin_To_Be_Updated = true
                    initViewModel.updateProduct(product)
                }
            }
        }
    }

    // Monitor image file state
    val imageFile by produceState<File?>(
        initialValue = null,
        produit_Id,
        index,
        reloadKey,
        forceReload
    ) {
        withContext(Dispatchers.IO) {
            val baseImagePath = "$imagesProduitsLocalExternalStorageBasePath/${produit_Id}_${index + 1}"
            Log.d(TAG, "Checking image file at: $baseImagePath")

            value = listOf("jpg", "jpeg", "png", "webp")
                .asSequence()
                .map { ext -> File("$baseImagePath.$ext") }
                .firstOrNull { file ->
                    val exists = file.exists()
                    val canRead = file.canRead()
                    val length = file.length()
                    Log.d(TAG, "File ${file.name} - exists: $exists, readable: $canRead, size: $length")
                    exists && canRead && length > 0
                }
        }
    }

    // Main image display
    Box(modifier = boxModifier) {
        val fileLength = imageFile?.length() ?: 0
        Log.d(TAG, "File size for display: $fileLength bytes")

        if (fileLength > 0) {
            GlideImage(
                model = imageFile,
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
                    .error(com.example.c_serveur.R.drawable.ic_launcher_background)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(com.bumptech.glide.Priority.HIGH)
                    .signature(ObjectKey("${produit_Id}_${index}_${currentQuality}_${forceReload}"))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e(TAG, "Image load failed", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d(TAG, "Image loaded successfully - source: $dataSource, isFirst: $isFirstResource")
                            isLoading = false
                            if (reloadSuccess) {
                                onRelodeDonne()
                                reloadSuccess = false
                            }
                            return false
                        }
                    })
            }
        } else {
            // Fallback image
            GlideImage(
                model = com.example.c_serveur.R.drawable.ic_launcher_background,
                contentDescription = "Fallback Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
            )
        }
    }
}
