package com.example.App_Produits_Main._1.Images_Handler

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
import com.example.c_serveur.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "Suive_Le_tigere_quend_Update"
private const val BASE_PATH = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
private const val IMAGE_QUALITY = 3

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun Glide_Display_Image_By_Id(
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
    var forceReload by remember { mutableStateOf(0) } // Nouvel état pour forcer le rechargement

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    // Wait for image update before triggering reload
    LaunchedEffect(produit_Image_Need_Update, reloadKey) {
        if (produit_Image_Need_Update) {
            Log.d(TAG, "🔄 Starting update process for Product ID: $produit_Id")
            isLoading = true
            imageLoaded = false

            val baseImagePath = "$BASE_PATH/${produit_Id}_${index + 1}"
            val imageFile = File("$baseImagePath.jpg")

            imageFile.parentFile?.mkdirs()

            val initialFileSize = if (imageFile.exists()) {
                Log.d(TAG, "📁 Existing file found for Product ID: $produit_Id")
                imageFile.length()
            } else {
                Log.d(TAG, "📁 No existing file for Product ID: $produit_Id")
                0
            }

            Log.d(TAG, "📊 Initial file size for Product ID: $produit_Id is $initialFileSize bytes")

            while (retryCount < maxRetries) {
                delay(1000)

                if (!imageFile.exists()) {
                    Log.w(TAG, "⚠️ File still not created after delay for Product ID: $produit_Id")
                    retryCount++
                    continue
                }

                val currentFileSize = imageFile.length()
                Log.d(TAG, "📍 Retry $retryCount - Current file size: $currentFileSize bytes for Product ID: $produit_Id")

                if (currentFileSize > 0 && currentFileSize != initialFileSize) {
                    Log.d(TAG, "✅ Valid file detected for Product ID: $produit_Id - Size: $currentFileSize bytes")
                    delay(500) // Attendre que le fichier soit complètement écrit
                    forceReload++ // Forcer un rechargement
                    Log.d(TAG, "🔄 Triggering force reload for Product ID: $produit_Id")
                    break
                }

                retryCount++
            }
        }
    }

    // Effet séparé pour gérer le rechargement forcé
    LaunchedEffect(forceReload) {
        if (forceReload > 0) {
            Log.d(TAG, "🔄 Executing force reload for Product ID: $produit_Id")
            isLoading = true
            imageLoaded = false
            currentQuality = 5f
            delay(300)
            currentQuality = IMAGE_QUALITY.toFloat()
            imageLoaded = true
            isLoading = false
        }
    }

    // Effet pour le rechargement normal
    LaunchedEffect(reloadKey) {
        if (!produit_Image_Need_Update) {
            Log.d(TAG, "🔄 Normal reload for Product ID: $produit_Id")
            isLoading = true
            imageLoaded = false
            currentQuality = 5f
            delay(300)
            currentQuality = IMAGE_QUALITY.toFloat()
            imageLoaded = true
        }
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        // Utiliser vararg keys pour passer plusieurs clés
        keys = arrayOf(produit_Id, index, reloadKey, forceReload)
    ) {
        value = withContext(Dispatchers.IO) {
            Log.d(TAG, "🔍 Searching for image files for Product ID: $produit_Id")
            val baseImagePath = "$BASE_PATH/${produit_Id}_${index + 1}"
            val file = listOf("jpg", "jpeg", "png", "webp")
                .asSequence()
                .map { ext -> File("$baseImagePath.$ext") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }

            file?.let {
                Log.d(TAG, "✅ Using image file: ${it.absolutePath} (size: ${it.length()} bytes)")
            } ?: Log.w(TAG, "⚠️ No valid image file found for Product ID: $produit_Id")

            file
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
        imageFile?.takeIf { it.length() > 0 }?.let { file ->
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
                    .signature(ObjectKey("${produit_Id}_${index}_${currentQuality}_${forceReload}")) // Ajout de forceReload à la signature
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e(TAG, "❌ Glide load failed for Product ID $produit_Id: ${e?.message}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d(TAG, "✅ Glide load success for Product ID: $produit_Id")
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
