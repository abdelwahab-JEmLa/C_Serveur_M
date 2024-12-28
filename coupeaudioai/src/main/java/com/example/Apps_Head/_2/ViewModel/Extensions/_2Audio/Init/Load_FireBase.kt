package com.example.Apps_Head._2.ViewModel.Extensions._2Audio.Init

import android.util.Log
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel
import kotlinx.coroutines.tasks.await

suspend fun AppInitialize_ViewModel.load_FireBase() {
    val TAG = "${_app_Initialize_Model.baseTAG_Audios_Model}/load_FireBase"

    try {
        Log.d(TAG, "Starting Firebase data fetch")

        // Initial data fetch for audio info
        val existingData = _app_Initialize_Model.baseRef_AudioMarks_Model.get().await()

        if (existingData.exists()) {
            // Clear existing data before loading new data
            _app_Initialize_Model.audioDatasModel.clear()

            // Parse and load the data
            existingData.children.forEach { snapshot ->
                // Process each audio data entry
                snapshot.getValue(AppInitializeModel.Audio_DatasModel::class.java)?.let { audioData ->
                    _app_Initialize_Model.audioDatasModel.add(audioData)
                }
            }

            Log.d(TAG, "Successfully loaded ${_app_Initialize_Model.audioDatasModel.size} audio entries")
        } else {
            Log.d(TAG, "No existing audio data found")
        }

    } catch (e: Exception) {
        Log.e(TAG, "Error loading Firebase audio data", e)
        throw e
    }
}
