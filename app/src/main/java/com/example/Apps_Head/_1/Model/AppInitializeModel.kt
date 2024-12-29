package com.example.Apps_Head._1.Model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Objects

@IgnoreExtraProperties
class AppInitializeModel(
    initial_Produits_Main_DataBase: List<ProduitModel> = emptyList()
) {
    var produits_Main_DataBase: SnapshotStateList<ProduitModel> =
        initial_Produits_Main_DataBase.toMutableStateList()

    val ref_Produits_Main_DataBase = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produit_DataBase")

        @IgnoreExtraProperties
        class ProduitModel(
            var id: Long = 0,
            val it_ref_Id_don_FireBase: Long = 0,
            val it_ref_don_FireBase: String = "",
            init_nom: String = "",
            init_besoin_To_Be_Updated: Boolean = false,
            init_it_Image_besoin_To_Be_Updated: Boolean = false,
            initialNon_Trouve: Boolean = false,
            init_colours_Et_Gouts: List<ColourEtGout_Model> = emptyList(),
            init_bonCommendDeCetteCota: GrossistBonCommandes? = null,
            init_bonS_Vent_De_Cette_Cota: List<ClientBonVent_Model> = emptyList(),

            init_historiqueBonsVents: List<ClientBonVent_Model> = emptyList(),
            init_historiqueBonsCommend: List<GrossistBonCommandes> = emptyList(),
        ) {

            var nom: String by mutableStateOf(init_nom)
            var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
            var it_Image_besoin_To_Be_Updated: Boolean by mutableStateOf(init_it_Image_besoin_To_Be_Updated)
            var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
            var auFilterFAB: Boolean by mutableStateOf( false)

            var coloursEtGouts: SnapshotStateList<ColourEtGout_Model> =
                init_colours_Et_Gouts.toMutableStateList()

            var bonCommendDeCetteCota: GrossistBonCommandes?
            by mutableStateOf(init_bonCommendDeCetteCota)
            var bonsVentDeCetteCota: SnapshotStateList<ClientBonVent_Model> =
                init_bonS_Vent_De_Cette_Cota.toMutableStateList()

            var historiqueBonsVents: SnapshotStateList<ClientBonVent_Model> =
                init_historiqueBonsVents.toMutableStateList()
            var historiqueBonsCommend: SnapshotStateList<GrossistBonCommandes> =
                init_historiqueBonsCommend.toMutableStateList()

                class ColourEtGout_Model(
                    var position_Du_Couleur_Au_Produit: Long = 0,
                    var nom: String = "",
                    var imogi: String = ""
                )

                class GrossistBonCommandes(
                    var vid: Long = 0,
                    init_grossistInformations: GrossistInformations? = null,
                    var date: String = "", //"yyyy-MM-dd HH:mm:ss"
                    var date_String_Divise: String = "", //"yyyy-MM-dd"
                    var time_String_Divise: String = "", //"HH:mm:ss"

                    var currentCreditBalance: Double = 0.0,
                    init_position_Grossist_Don_Parent_Grossists_List: Int = 0,
                    init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int = 0,
                    init_coloursEtGoutsCommendee: List<ColoursGoutsCommendee> = emptyList(),
                ) {
                    var grossistInformations: GrossistInformations?
                            by mutableStateOf(init_grossistInformations)

                    var position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int by mutableStateOf(
                        init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                    )
                    var position_Grossist_Don_Parent_Grossists_List: Int by mutableStateOf(
                        init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                    )
                    var coloursEtGoutsCommendee: SnapshotStateList<ColoursGoutsCommendee> =
                        init_coloursEtGoutsCommendee.toMutableStateList()

                        data class GrossistInformations(
                            val id: Long,
                            val nom: String,
                            val couleur: String
                        ) {
                            var auFilterFAB: Boolean by mutableStateOf( false)

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

                        class ColoursGoutsCommendee(
                                init_statues: ColourEtGout_Model? = null,
                                init_quantityAchete: Int = 0
                        ) {
                                var statues: ColourEtGout_Model?
                                        by mutableStateOf(init_statues)

                                var quantityAchete: Int by mutableStateOf(init_quantityAchete)
                        }
                }

                @IgnoreExtraProperties
                class ClientBonVent_Model(
                    var vid: Long = 0,
                    var id_Acheteur: Long = 0,
                    var nom_Acheteur: String = "",
                    var time_String: String = "", //"yyyy-MM-dd HH:mm:ss"
                    var inseartion_Temp: Long = 0,
                    var inceartion_Date: Long = 0,
                    init_colours_achete: List<Color_Achat_Model> = emptyList(),
                ) {
                    var colours_Achete: SnapshotStateList<Color_Achat_Model> =
                        init_colours_achete.toMutableStateList()

                        class Color_Achat_Model(
                            var vidPosition: Long = 0,
                            var nom: String = "",
                            var quantity_Achete: Int = 0,
                            var imogi: String = ""
                        )
                }
            }

    suspend fun update_Produits_FireBase() {
        try {
            val ref_Produit_Main_DataBase = Firebase.database
                .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
                .child("produit_DataBase")

            withContext(Dispatchers.IO) {
                ref_Produit_Main_DataBase.setValue(produits_Main_DataBase).await()
            }
        } catch (e: Exception) {
            Log.e("AppInitializeModel", "Failed to update Firebase", e)
            throw Exception("Failed to update Firebase: ${e.message}")
        }
    }


}
