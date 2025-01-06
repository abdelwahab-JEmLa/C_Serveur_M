package com.example.Apps_Head._1.Model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import java.util.Objects

@IgnoreExtraProperties
class AppsHeadModel(
    initial_Produits_Main_DataBase: List<ProduitModel> = emptyList()
) {
    @get:Exclude
    var produitsMainDataBase: SnapshotStateList<ProduitModel> =
        initial_Produits_Main_DataBase.toMutableStateList()

    // Property for Firebase serialization
    var produitsDataBaseList: List<ProduitModel>
        get() = produitsMainDataBase.toList()
        set(value) {
            produitsMainDataBase.clear()
            produitsMainDataBase.addAll(value)
        }

    @IgnoreExtraProperties
    class ProduitModel(
        var id: Long = 0,
        var itsTempProduit: Boolean = false,
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_colours_Et_Gouts: List<ColourEtGout_Model> = emptyList(),
        init_bonCommendDeCetteCota: GrossistBonCommandes? = null,
        initBonsVentDeCetteCota: List<ClientBonVentModel> = emptyList(),
        init_visible: Boolean = true,
        init_historiqueBonsVents: List<ClientBonVentModel> = emptyList(),
        init_historiqueBonsCommend: List<GrossistBonCommandes> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoinToBeUpdated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var isVisible: Boolean by mutableStateOf(init_visible)

        var statuesBase: StatuesBase by mutableStateOf(StatuesBase())

        @get:Exclude
        var coloursEtGouts: SnapshotStateList<ColourEtGout_Model> =
            init_colours_Et_Gouts.toMutableStateList()

        var coloursEtGoutsList: List<ColourEtGout_Model>
            get() = coloursEtGouts.toList()
            set(value) {
                coloursEtGouts.clear()
                coloursEtGouts.addAll(value)
            }

        var bonCommendDeCetteCota: GrossistBonCommandes? by mutableStateOf(init_bonCommendDeCetteCota)

        @get:Exclude
        var bonsVentDeCetteCota: SnapshotStateList<ClientBonVentModel> =
            initBonsVentDeCetteCota.toMutableStateList()

        var bonsVentDeCetteCotaList: List<ClientBonVentModel>
            get() = bonsVentDeCetteCota.toList()
            set(value) {
                bonsVentDeCetteCota.clear()
                bonsVentDeCetteCota.addAll(value)
            }

        @get:Exclude
        var historiqueBonsVents: SnapshotStateList<ClientBonVentModel> =
            init_historiqueBonsVents.toMutableStateList()

        var historiqueBonsVentsList: List<ClientBonVentModel>
            get() = historiqueBonsVents.toList()
            set(value) {
                historiqueBonsVents.clear()
                historiqueBonsVents.addAll(value)
            }

        @get:Exclude
        var historiqueBonsCommend: SnapshotStateList<GrossistBonCommandes> =
            init_historiqueBonsCommend.toMutableStateList()

        var historiqueBonsCommendList: List<GrossistBonCommandes>
            get() = historiqueBonsCommend.toList()
            set(value) {
                historiqueBonsCommend.clear()
                historiqueBonsCommend.addAll(value)
            }

        @IgnoreExtraProperties
        class StatuesBase(
            var ilAUneCouleurAvecImage: Boolean = false,
        ) {
            var naAucunImage: Boolean by mutableStateOf(false)
            var sonImageBesoinActualisation: Boolean by mutableStateOf(false)
            var imageGlidReloadTigger: Int by mutableStateOf(0)
            var prePourCameraCapture: Boolean by mutableStateOf(false)
        }

        @IgnoreExtraProperties
        class GrossistBonCommandes(
            var vid: Long = 0,
            init_grossistInformations: GrossistInformations? = null,
            var date: String = "",
            var date_String_Divise: String = "",
            var time_String_Divise: String = "",
            var currentCreditBalance: Double = 0.0,
            init_position_Grossist_Don_Parent_Grossists_List: Int = 0,
            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int = 0,
            init_coloursEtGoutsCommendee: List<ColoursGoutsCommendee> = emptyList(),
        ) {
            var grossistInformations: GrossistInformations? by mutableStateOf(init_grossistInformations)
            var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int by mutableStateOf(
                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            )
            var position_Grossist_Don_Parent_Grossists_List: Int by mutableStateOf(
                init_position_Grossist_Don_Parent_Grossists_List
            )

            @get:Exclude
            var coloursEtGoutsCommendee: SnapshotStateList<ColoursGoutsCommendee> =
                init_coloursEtGoutsCommendee.toMutableStateList()

            var coloursEtGoutsCommendeList: List<ColoursGoutsCommendee>
                get() = coloursEtGoutsCommendee.toList()
                set(value) {
                    coloursEtGoutsCommendee.clear()
                    coloursEtGoutsCommendee.addAll(value)
                }

            // Rest of the nested classes remain the same
            @IgnoreExtraProperties
            data class GrossistInformations(
                val id: Long = 0,
                val nom: String = "",
                val couleur: String = ""
            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is GrossistInformations) return false
                    return id == other.id &&
                            nom == other.nom &&
                            couleur == other.couleur
                }

                override fun hashCode(): Int {
                    return Objects.hash(id, nom, couleur)
                }
            }

            @IgnoreExtraProperties
            class ColoursGoutsCommendee(
                val id: Long = 0,
                val nom: String = "",
                val emoji: String = "",
                init_quantityAchete: Int = 0
            ) {
                var quantityAchete: Int by mutableIntStateOf(init_quantityAchete)
            }
        }

        @IgnoreExtraProperties
        class ColourEtGout_Model(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var nom: String = "",
            var imogi: String = "",
            var sonImageNeExistPas: Boolean = false,
        )

        @IgnoreExtraProperties
        class ClientBonVentModel(
            init_clientInformations: ClientInformations? = null,
            init_colours_achete: List<ColorAchatModel> = emptyList(),
        ) {
            var clientInformations: ClientInformations? by mutableStateOf(init_clientInformations)

            @get:Exclude
            var colours_Achete: SnapshotStateList<ColorAchatModel> =
                init_colours_achete.toMutableStateList()

            var coloursAcheteList: List<ColorAchatModel>
                get() = colours_Achete.toList()
                set(value) {
                    colours_Achete.clear()
                    colours_Achete.addAll(value)
                }

            @IgnoreExtraProperties
            data class ClientInformations(
                val id: Long = 0,
                val nom: String = "",
                val couleur: String = ""
            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is ClientInformations) return false
                    return id == other.id &&
                            nom == other.nom &&
                            couleur == other.couleur
                }

                override fun hashCode(): Int {
                    return Objects.hash(id, nom, couleur)
                }
            }

            @IgnoreExtraProperties
            class ColorAchatModel(
                var vidPosition: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            )
        }
    }

    companion object {
        val produitsFireBaseRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("produits")

        val imagesProduitsFireBaseStorageRef = Firebase.storage.reference
            .child("Images Articles Data Base")
            .child("produits")

        const val imagesProduitsLocalExternalStorageBasePath =
            "/storage/emulated/0/" +
                    "Abdelwahab_jeMla.com" +
                    "/IMGs" +
                    "/BaseDonne"

        fun SnapshotStateList<ProduitModel>.updateProduitsFireBase() {
            try {
                val updatedProducts = this.filter { it.besoinToBeUpdated }

                updatedProducts.forEach { product ->
                    try {
                        produitsFireBaseRef.child(product.id.toString()).setValue(product)
                        product.besoinToBeUpdated = false
                        Log.d("Firebase", "Successfully updated product ${product.id}")
                    } catch (e: Exception) {
                        Log.e("Firebase", "Failed to update product ${product.id}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error updating products", e)
                throw e
            }
        }
    }
}
