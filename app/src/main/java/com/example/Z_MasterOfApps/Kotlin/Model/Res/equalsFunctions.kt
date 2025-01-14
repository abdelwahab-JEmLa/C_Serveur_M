package com.example.Z_MasterOfApps.Kotlin.Model.Res

import com.example.Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import java.util.Objects

fun GrossistBonCommandes.GrossistInformations.equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is GrossistBonCommandes.GrossistInformations) return false
    return id == other.id &&
            nom == other.nom &&
            couleur == other.couleur
}

 fun GrossistBonCommandes.GrossistInformations.hashCode(): Int {
    return Objects.hash(id, nom, couleur)
}
