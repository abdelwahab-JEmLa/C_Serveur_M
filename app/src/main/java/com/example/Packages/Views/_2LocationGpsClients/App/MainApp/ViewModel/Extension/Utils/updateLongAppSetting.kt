package com.example.Packages.Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.AppSettingsSaverModel
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.ViewModelExtension_App2_F1
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

fun ViewModelExtension_App2_F1.updateLongAppSetting(
    value: Long,
    name: String = "clientBuyerNowId",
) {
    viewModelScope.launch {
        try {
            val appSettingsSaverModel = AppSettingsSaverModel(
                id = 1,
                name = name,
                valueLong = value,
                date = Date()
            )

            Firebase.database.getReference("A_AppSettingsSaverModel")
                .child(appSettingsSaverModel.id.toString())
                .setValue(appSettingsSaverModel)
                .await()
        } catch (e: Exception) {
        }
    }
}
