package com.example.Packages.Z.Archives.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatasNeedFireBaseUpdate(
    @PrimaryKey var vid: Long = 0,
    val nameTable: String = "",
    val vidData: Long = 0,
    val timeRemain: String = "", 
    
) {
    // No-argument constructor for Firebase
    constructor() : this(0)
}
