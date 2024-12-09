package com.example.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClientsDataBase(
    @PrimaryKey val vidSu: Long = 0,
    var idClientsSu: Long = 0,
    var position: Int = 0,
    var nomClientsSu: String = "",
    var nameAggregation: String = "",
    var bonDuClientsSu: String = "",
    val couleurSu: String = "#FFFFFF", // Default color
    var currentCreditBalance: Double = 0.0,
    var itsReadyForEdite: Boolean = false,
    ) {
    constructor() : this(0)
}
