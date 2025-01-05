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
import com.example.c_serveur.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val BASE_PATH = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
private const val IMAGE_QUALITY = 3
private const val TAG = "GlideImageDebug"

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideDisplayImageById(
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
    Log.d(TAG, "Démarrage GlideDisplayImageById - produit_Id: $produit_Id, index: $index")
    Log.d(TAG, "État initial - sonImageBesoinActualisation: $sonImageBesoinActualisation")

    var isLoading by remember { mutableStateOf(true) }
    var forceReload by remember { mutableStateOf(0) }
    var currentQuality by remember { mutableStateOf(IMAGE_QUALITY.toFloat()) }
    var reloadSuccess by remember { mutableStateOf(false) }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val boxModifier = modifier.then(
        if (height_Defini != null && width_Defini != null) {
            Modifier.size(width = width_Defini, height = height_Defini)
        } else {
            Modifier.fillMaxSize()
        }
    )

    LaunchedEffect(sonImageBesoinActualisation, reloadKey) {
        Log.d(TAG, "LaunchedEffect déclenché - sonImageBesoinActualisation: $sonImageBesoinActualisation")

        if (sonImageBesoinActualisation) {
            val baseImagePath = "$BASE_PATH/${produit_Id}_${index + 1}"
            val imageFile = File("$baseImagePath.jpg")
            Log.d(TAG, "Chemin du fichier: $baseImagePath.jpg")
            Log.d(TAG, "Le fichier existe: ${imageFile.exists()}")

            imageFile.parentFile?.mkdirs()
            val initialFileSize = if (imageFile.exists()) imageFile.length() else 0
            Log.d(TAG, "Taille initiale du fichier: $initialFileSize bytes")

            var shouldReload = false

            repeat(3) { attempt ->
                delay(1000)
                if (imageFile.exists()) {
                    val currentFileSize = imageFile.length()
                    Log.d(TAG, "Tentative $attempt - Nouvelle taille: $currentFileSize bytes")

                    if (currentFileSize > 0 && currentFileSize != initialFileSize) {
                        shouldReload = true
                        Log.d(TAG, "Changement de taille détecté - Reload nécessaire")
                    }
                } else {
                    Log.d(TAG, "Tentative $attempt - Fichier n'existe pas")
                }
            }

            if (shouldReload) {
                Log.d(TAG, "Préparation du reload - Attente de 500ms")
                delay(500)
                forceReload++
                reloadSuccess = true
                Log.d(TAG, "Reload initié - forceReload: $forceReload, reloadSuccess: $reloadSuccess")
            } else {
                Log.d(TAG, "Aucun reload nécessaire")
            }
        }
    }

    LaunchedEffect(reloadSuccess) {
        if (reloadSuccess) {
            Log.d(TAG, "Callback onRelodeDonne appelé")
            onRelodeDonne()
            reloadSuccess = false
            Log.d(TAG, "reloadSuccess réinitialisé à false")
        }
    }

    LaunchedEffect(forceReload, reloadKey) {
        if (forceReload > 0 || !sonImageBesoinActualisation) {
            Log.d(TAG, "Début du processus de reload - forceReload: $forceReload")
            isLoading = true
            currentQuality = 5f
            delay(300)
            currentQuality = IMAGE_QUALITY.toFloat()
            isLoading = false
            Log.d(TAG, "Fin du processus de reload - currentQuality: $currentQuality")
        }
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        keys = arrayOf(produit_Id, index, reloadKey, forceReload)
    ) {
        value = withContext(Dispatchers.IO) {
            val baseImagePath = "$BASE_PATH/${produit_Id}_${index + 1}"
            Log.d(TAG, "Recherche du fichier image - Chemin de base: $baseImagePath")

            listOf("jpg", "jpeg", "png", "webp")
                .asSequence()
                .map { ext -> File("$baseImagePath.$ext") }
                .firstOrNull { file ->
                    val exists = file.exists()
                    val canRead = file.canRead()
                    val length = file.length()
                    Log.d(TAG, "Test fichier ${file.name} - existe: $exists, lisible: $canRead, taille: $length")
                    exists && canRead && length > 0
                }
        }
    }

    Box(modifier = boxModifier) {
        val fileLength = imageFile?.length() ?: 0
        Log.d(TAG, "Taille du fichier à afficher: $fileLength bytes")

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
                    .error(R.drawable.ic_launcher_background)
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
                            Log.e(TAG, "Échec du chargement de l'image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d(TAG, "Image chargée avec succès - dataSource: $dataSource, isFirstResource: $isFirstResource")
                            isLoading = false
                            if (reloadSuccess) {
                                Log.d(TAG, "Exécution du callback après chargement réussi")
                                onRelodeDonne()
                                reloadSuccess = false
                            }
                            return false
                        }
                    })
            }
        } else {
            Log.d(TAG, "Affichage de l'image par défaut - fichier non trouvé ou vide")
            GlideImage(
                model = R.drawable.ic_launcher_background,
                contentDescription = "Fallback Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
            )
        }
    }
}
