package com.example.Apps_Head._1.Model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Exclude
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import java.util.Objects

@IgnoreExtraProperties
class AppsHeadModel(
    initial_Produits_Main_DataBase: List<ProduitModel> = emptyList()
) {
    @get:Exclude
    @set:Exclude
    var produits_Main_DataBase: SnapshotStateList<ProduitModel> =
        initial_Produits_Main_DataBase.toMutableStateList()

    var produitsMainDataBaseList: List<ProduitModel>
        get() = produits_Main_DataBase
        set(value) {
            produits_Main_DataBase = value.toMutableStateList()
        }
    val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    val ref_Produits_Main_DataBase = Firebase.database.getReference(CHEMIN_BASE)


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
        init_visible: Boolean = true,
        init_historiqueBonsVents: List<ClientBonVent_Model> = emptyList(),
        init_historiqueBonsCommend: List<GrossistBonCommandes> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoin_To_Be_Updated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var it_Image_besoin_To_Be_Updated: Boolean by mutableStateOf(init_it_Image_besoin_To_Be_Updated)
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var isVisible: Boolean by mutableStateOf(init_visible)

        @get:Exclude
        @set:Exclude
        var coloursEtGouts: SnapshotStateList<ColourEtGout_Model> =
            init_colours_Et_Gouts.toMutableStateList()

        var coloursEtGoutsList: List<ColourEtGout_Model>
            get() = coloursEtGouts
            set(value) {
                coloursEtGouts = value.toMutableStateList()
            }

        var bonCommendDeCetteCota: GrossistBonCommandes? by mutableStateOf(init_bonCommendDeCetteCota)

        @get:Exclude
        @set:Exclude
        var bonsVentDeCetteCota: SnapshotStateList<ClientBonVent_Model> =
            init_bonS_Vent_De_Cette_Cota.toMutableStateList()

        var bonsVentDeCetteCotaList: List<ClientBonVent_Model>
            get() = bonsVentDeCetteCota
            set(value) {
                bonsVentDeCetteCota = value.toMutableStateList()
            }

        @get:Exclude
        @set:Exclude
        var historiqueBonsVents: SnapshotStateList<ClientBonVent_Model> =
            init_historiqueBonsVents.toMutableStateList()

        var historiqueBonsVentsList: List<ClientBonVent_Model>
            get() = historiqueBonsVents
            set(value) {
                historiqueBonsVents = value.toMutableStateList()
            }

        @get:Exclude
        @set:Exclude
        var historiqueBonsCommend: SnapshotStateList<GrossistBonCommandes> =
            init_historiqueBonsCommend.toMutableStateList()

        var historiqueBonsCommendList: List<GrossistBonCommandes>
            get() = historiqueBonsCommend
            set(value) {
                historiqueBonsCommend = value.toMutableStateList()
            }

        companion object {
            fun fromSnapshot(snapshot: DataSnapshot, DEBUG_LIMIT: Int): ProduitModel? {
                return try {
                    val productId = snapshot.key?.toIntOrNull() ?: -1
                    val shouldLog = productId <= DEBUG_LIMIT

                    if (shouldLog) {
                        Log.d("ProduitModel", "Starting to parse snapshot: ${snapshot.key}")
                    }

                    val model = snapshot.getValue(ProduitModel::class.java)
                    model?.apply {
                        val coloursType = object : GenericTypeIndicator<List<ColourEtGout_Model>>() {}
                        val bonsVentType = object : GenericTypeIndicator<List<ClientBonVent_Model>>() {}
                        val historiqueVentType = object : GenericTypeIndicator<List<ClientBonVent_Model>>() {}
                        val historiqueCommendType = object : GenericTypeIndicator<List<GrossistBonCommandes>>() {}

                        try {
                            snapshot.child("coloursEtGoutsList").getValue(coloursType)?.let {
                                coloursEtGouts = it.toMutableStateList()
                                if (shouldLog) {
                                    Log.d("ProduitModel", "Loaded ${it.size} colours")
                                }
                            }

                            // Parse bonCommendDeCetteCota separately using its own fromSnapshot method
                            snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
                                if (bonCommendSnapshot.exists()) {
                                    bonCommendDeCetteCota = GrossistBonCommandes.fromSnapshot(bonCommendSnapshot)
                                    if (shouldLog) {
                                        Log.d("ProduitModel", "Loaded bonCommendDeCetteCota")
                                    }
                                }
                            }

                            snapshot.child("bonsVentDeCetteCotaList").getValue(bonsVentType)?.let {
                                bonsVentDeCetteCota = it.toMutableStateList()
                                if (shouldLog) {
                                    Log.d("ProduitModel", "Loaded ${it.size} bons vents")
                                }
                            }
                            snapshot.child("historiqueBonsVentsList").getValue(historiqueVentType)?.let {
                                historiqueBonsVents = it.toMutableStateList()
                                if (shouldLog) {
                                    Log.d("ProduitModel", "Loaded ${it.size} historique vents")
                                }
                            }
                            snapshot.child("historiqueBonsCommendList").getValue(historiqueCommendType)?.let {
                                historiqueBonsCommend = it.toMutableStateList()
                                if (shouldLog) {
                                    Log.d("ProduitModel", "Loaded ${it.size} historique commends")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ProduitModel", "Error parsing lists: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                    model
                } catch (e: Exception) {
                    Log.e("ProduitModel", "Error parsing snapshot: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
        }

        @IgnoreExtraProperties
        class ColourEtGout_Model(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var nom: String = "",
            var imogi: String = ""
        )

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
            var position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int by mutableStateOf(
                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            )
            var position_Grossist_Don_Parent_Grossists_List: Int by mutableStateOf(
                init_position_Grossist_Don_Parent_Grossists_List
            )

            @get:Exclude
            @set:Exclude
            var coloursEtGoutsCommendee: SnapshotStateList<ColoursGoutsCommendee> =
                init_coloursEtGoutsCommendee.toMutableStateList()

            var coloursEtGoutsCommendeeList: List<ColoursGoutsCommendee>
                get() = coloursEtGoutsCommendee
                set(value) {
                    coloursEtGoutsCommendee = value.toMutableStateList()
                }

            companion object {
                fun fromSnapshot(snapshot: DataSnapshot): GrossistBonCommandes? {
                    return try {
                        val model = snapshot.getValue(GrossistBonCommandes::class.java)

                        model?.apply {
                            // Load GrossistInformations
                            snapshot.child("grossistInformations").let { grossistSnap ->
                                if (grossistSnap.exists()) {
                                    grossistInformations = grossistSnap.getValue(GrossistInformations::class.java)
                                }
                            }

                            // Load position values
                            position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                                snapshot.child("position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit")
                                    .getValue(Int::class.java) ?: 0

                            position_Grossist_Don_Parent_Grossists_List =
                                snapshot.child("position_Grossist_Don_Parent_Grossists_List")
                                    .getValue(Int::class.java) ?: 0

                            // Load colors and orders list
                            val coloursType = object : GenericTypeIndicator<List<ColoursGoutsCommendee>>() {}
                            snapshot.child("coloursEtGoutsCommendeeList").getValue(coloursType)?.let { colors ->
                                coloursEtGoutsCommendee = colors.toMutableStateList()
                            }

                            // Load other primitive fields if needed
                            vid = snapshot.child("vid").getValue(Long::class.java) ?: 0
                            date = snapshot.child("date").getValue(String::class.java) ?: ""
                            date_String_Divise = snapshot.child("date_String_Divise").getValue(String::class.java) ?: ""
                            time_String_Divise = snapshot.child("time_String_Divise").getValue(String::class.java) ?: ""
                            currentCreditBalance = snapshot.child("currentCreditBalance").getValue(Double::class.java) ?: 0.0

                            Log.d("GrossistBonCommandes", """
                        Loaded GrossistBonCommandes:
                        - VID: $vid
                        - Grossist Info: ${grossistInformations?.nom}
                        - Colors Count: ${coloursEtGoutsCommendee.size}
                        - Position in List: $position_Grossist_Don_Parent_Grossists_List
                        - Product Position: $position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                    """.trimIndent())
                        }

                        model
                    } catch (e: Exception) {
                        Log.e("GrossistBonCommandes", "Error parsing snapshot: ${e.message}")
                        e.printStackTrace()
                        null
                    }
                }
            }

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
                val couleur: String = "",
                init_quantityAchete: Int = 0
            ) {
                var quantityAchete: Int by mutableStateOf(init_quantityAchete)
            }
        }

        @IgnoreExtraProperties
        class ClientBonVent_Model(
            var vid: Long = 0,
            var id_Acheteur: Long = 0,
            var nom_Acheteur: String = "",
            var time_String: String = "",
            var inseartion_Temp: Long = 0,
            var inceartion_Date: Long = 0,
            init_colours_achete: List<Color_Achat_Model> = emptyList(),
        ) {
            @get:Exclude
            @set:Exclude
            var colours_Achete: SnapshotStateList<Color_Achat_Model> =
                init_colours_achete.toMutableStateList()

            var coloursAcheteList: List<Color_Achat_Model>
                get() = colours_Achete
                set(value) {
                    colours_Achete = value.toMutableStateList()
                }

            companion object {
                fun fromSnapshot(snapshot: DataSnapshot): ClientBonVent_Model? {
                    return try {
                        val model = snapshot.getValue(ClientBonVent_Model::class.java)
                        model?.apply {
                            val colorsType = object : GenericTypeIndicator<List<Color_Achat_Model>>() {}
                            snapshot.child("coloursAcheteList").getValue(colorsType)?.let {
                                colours_Achete = it.toMutableStateList()
                            }
                        }
                        model
                    } catch (e: Exception) {
                        Log.e("ClientBonVent", "Error parsing snapshot: ${e.message}")
                        e.printStackTrace()
                        null
                    }
                }
            }

            @IgnoreExtraProperties
            class Color_Achat_Model(
                var vidPosition: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            )
        }
    }

    companion object {
         fun SnapshotStateList<ProduitModel>.updateProduitsFireBase() {
            try {
                val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
                val baseRef = Firebase.database.getReference(CHEMIN_BASE)

                val updatedProducts = this.filter { it.besoin_To_Be_Updated }

                updatedProducts.forEach { product ->
                    try {
                        baseRef.child(product.id.toString()).setValue(product)
                        product.besoin_To_Be_Updated = false
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
           /*
        fun setupDatabaseListener(ref: DatabaseReference, onUpdate: (List<ProduitModel>) -> Unit) {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val products = mutableListOf<ProduitModel>()
                        snapshot.children.forEach { productSnapshot ->
                            ProduitModel.fromSnapshot(productSnapshot)?.let {
                                products.add(it)
                            }
                        }
                        onUpdate(products)
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error in database listener", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Database error: ${error.message}")
                }
            })
        }  */
    }
}
