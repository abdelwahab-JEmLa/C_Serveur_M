package com.example.App_Produits_Main._1.Model.Extensions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Audio_DatasModel(
    var vid: Long = 0,
    init_fileInfos: FileInfos_Model? = null,
    init_audioMarks: List<AudioMarks_Model> = emptyList(),
) {
    var fileInfos: FileInfos_Model? by mutableStateOf(init_fileInfos)

    class FileInfos_Model(
        var nom: String = "",
        var path: String = ""
    )

    var audioMarks: SnapshotStateList<AudioMarks_Model> =
        init_audioMarks.toMutableStateList()

    class AudioMarks_Model(
        var id: Long = 0,
        var tag: String = "",
        var positionTime: Int = 0,
    )

    private val ref_AudioInfo_Model = Firebase.database
        .getReference("_2_3ilm_Char3i")
        .child("1_AudioInfo_Model")

}
