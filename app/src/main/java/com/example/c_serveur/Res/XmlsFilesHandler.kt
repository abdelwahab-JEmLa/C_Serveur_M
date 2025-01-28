package com.example.c_serveur.Res

import com.example.c_serveur.R

class XmlsFilesHandler {

    companion object {
        // Moved to companion object to allow static access
        val xmlResources = listOf(
            Pair("marker_info_window", R.layout.marker_info_window),
            Pair("info_window_container", R.id.info_window_container)
        )
    }
}
