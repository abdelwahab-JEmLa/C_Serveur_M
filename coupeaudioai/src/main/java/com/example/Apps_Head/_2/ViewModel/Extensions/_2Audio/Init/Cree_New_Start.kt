package com.example.Apps_Head._2.ViewModel.Extensions._2Audio.Init

import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel
import kotlin.random.Random

suspend fun AppInitialize_ViewModel.cree_New_Start() {
    val TAG = "${_app_Initialize_Model.baseTAG_Audios_Model}/cree_New_Start"
    val NOMBRE_AUDIOS = 2
    val NOMBRE_MARQUES = 3
    val audio_DataModel = _app_Initialize_Model.audioDatasModel

    try {
        initializationProgress = 0.1f
        isInitializing = true

        // Clear and prepare the products list
        audio_DataModel.clear()

        // Initialize audio list with random data
        repeat(NOMBRE_AUDIOS) { audioIndex ->
            val nouvelAudio = AppInitializeModel.Audio_DatasModel(
                vid = (audioIndex + 1).toLong(),
                init_fileInfos = AppInitializeModel.Audio_DatasModel.FichieInfos_Model(
                    nom = "Audio ${audioIndex + 1}",
                    path = _app_Initialize_Model.audios_Model_LocalAppStorageCheminBase +
                            audioIndex + 1 +
                            ".mp3"
                )
            )

            // Generate random marks for each audio
            val marquesList = List(NOMBRE_MARQUES) { marqueIndex ->
                val randomTime = Random.nextInt(0, 3600) // Random time between 0-3600 seconds
                val randomTag = generateRandomTag(marqueIndex)

                AppInitializeModel.Audio_DatasModel.AudioMarks_Model(
                    id = (marqueIndex + 1).toLong(),
                    tag = randomTag,
                    positionTime = randomTime
                )
            }

            // Add the marks to the audio
            nouvelAudio.audioMarks.addAll(marquesList)

            // Add the new audio to the model
            audio_DataModel.add(nouvelAudio)
        }

        _app_Initialize_Model.update_AudiosInfo()

        initializationProgress = 1.0f
        isInitializing = false

    } catch (e: Exception) {
        isInitializing = false
        throw e
    }
}

private fun generateRandomTag(index: Int): String {
    val tags = listOf(
        "Introduction",
        "Chapter Start",
        "Important Point",
        "Question",
        "Summary",
        "Key Concept",
        "Example",
        "Definition"
    )
    return "${tags[index % tags.size]} ${index + 1}"
}
