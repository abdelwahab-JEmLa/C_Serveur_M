package Z_MasterOfApps.Kotlin.ViewModel.Partage.Functions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import com.google.firebase.Firebase
import com.google.firebase.database.database

class FunctionsPartageEntreFragment(
    val viewModelInitApp: ViewModelInitApp,
) {
    fun changeColours_AcheteQuantity_Achete(
        selectedBonVent: ProduitModel.ClientBonVentModel?,
        produit: ProduitModel,
        color: ProduitModel.ClientBonVentModel.ColorAchatModel,
        newQuantity: Int
    ) {
        val updatedProduit = produit.apply {
            bonsVentDeCetteCota.find { it==selectedBonVent }
                ?.let { bonVent ->
                    bonVent.colours_Achete.find { it == color }
                        ?.quantity_Achete = newQuantity
                }
        }
        updateProduit(updatedProduit,viewModelInitApp)
         Firebase.database.getReference("O_SoldArticlesTabelle") //-->
             //TODO(1): fait que ici de trouve le produit avec id et id client ==produit.id et selectedBonVent
               //a^re chercghe l id couleur si coresspond a   color1IdPicked  update  color1SoldQuantity
             // color2IdPicked     >  color2SoldQuantity
             //
             // color3IdPicked  > ect.. a 4


             // et update le si @Entity
             //data class SoldArticlesTabelle(
             //    @PrimaryKey(autoGenerate = true) val vid: Long = 0,
             //    val idArticle: Long = 0,
             //    val nameArticle: String = "",
             //    val clientSoldToItId: Long = 0,
             //    val date: String = "",
             //    val color1IdPicked: Long = 0,
             //    val color1SoldQuantity: Int = 0,
             //    val color2IdPicked: Long = 0,
             //    val color2SoldQuantity: Int = 0,
             //    val color3IdPicked: Long = 0,
             //    val color3SoldQuantity: Int = 0,
             //    val color4IdPicked: Long = 0,
             //    val color4SoldQuantity: Int = 0,
             //    val confimed: Boolean = false,
             //
             //    ) {
             //    constructor() : this(0)
             //}
             .child("")
    }
}
