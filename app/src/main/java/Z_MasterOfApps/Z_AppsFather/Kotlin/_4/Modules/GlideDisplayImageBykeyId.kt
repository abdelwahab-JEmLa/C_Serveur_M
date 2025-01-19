package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val MIN_RELOAD_INTERVAL = 500L
private const val IMAGE_QUALITY = 3
private const val DEFAULT_IMAGE = "logo.webp"

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideDisplayImageBykeyId(
    keyImageId: String = "null",
    imageGlidReloadTigger: Int =0,
    naPasDimage: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {}
) {
    var imageFile by remember { mutableStateOf<File?>(null) }
    var forceReload by remember { mutableIntStateOf(0) }
    var reloadSuccess by remember { mutableStateOf(false) }
    var previousTrigger by remember { mutableIntStateOf(0) }
    var lastReloadTimestamp by remember { mutableLongStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var shouldUseDefaultImage by remember { mutableStateOf(keyImageId == "null" || naPasDimage) }

    // Handle changes in naPasDimage
    LaunchedEffect(naPasDimage) {
        if (naPasDimage != shouldUseDefaultImage) {
            shouldUseDefaultImage = naPasDimage
            forceReload++
            isLoading = true
            reloadSuccess = true
        }
    }

    LaunchedEffect(keyImageId) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val currentTrigger = imageGlidReloadTigger

            if (currentTime - lastReloadTimestamp > MIN_RELOAD_INTERVAL &&
                currentTrigger != previousTrigger
            ) {
                lastReloadTimestamp = currentTime
                previousTrigger = currentTrigger
                forceReload++
                isLoading = true
                reloadSuccess = true
            }
            delay(MIN_RELOAD_INTERVAL)
        }
    }

    LaunchedEffect(keyImageId, forceReload, shouldUseDefaultImage) {
        withContext(Dispatchers.IO) {
            imageFile = when {
                shouldUseDefaultImage -> File("$imagesProduitsLocalExternalStorageBasePath/$DEFAULT_IMAGE")
                keyImageId == "null" -> File("$imagesProduitsLocalExternalStorageBasePath/$DEFAULT_IMAGE")
                else -> {
                    val imagePath = "$imagesProduitsLocalExternalStorageBasePath/${keyImageId}"
                    listOf("jpg", "jpeg", "png", "webp")
                        .map { File("$imagePath.$it") }
                        .firstOrNull { it.exists() && it.length() > 0 }
                }
            }
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            model = imageFile ?: File("$imagesProduitsLocalExternalStorageBasePath/$DEFAULT_IMAGE"),
            contentDescription = "Product $keyImageId",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        ) { builder ->
            builder
                .downsample(com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.AT_MOST)
                .encodeQuality(IMAGE_QUALITY)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey("${keyImageId}_${forceReload}_${if(shouldUseDefaultImage) "default" else "custom"}"))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        isLoading = false
                        loadProgress = 0f
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        isLoading = false
                        loadProgress = 1f
                        if (reloadSuccess) {
                            onLoadComplete()
                            reloadSuccess = false
                        }
                        return false
                    }
                })
        }

        if (isLoading) {
            CircularProgressIndicator(
                progress = { loadProgress },
                modifier = Modifier.size(48.dp),
                color = Color.Blue,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
    }
}
