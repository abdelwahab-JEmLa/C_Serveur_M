package com.example.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

// AppSettingsSaverModel.kt
@Entity
data class AppSettingsSaverModel(
    @PrimaryKey var id: Long = 0,
    val name: String = "",
    val dateForNewEntries: String = "", //yyyy-mm-dd
    val displayStatisticsDate: String = "", //yyyy-mm-dd

) {
    // No-argument constructor for Firebase
    constructor() : this(0)
}
