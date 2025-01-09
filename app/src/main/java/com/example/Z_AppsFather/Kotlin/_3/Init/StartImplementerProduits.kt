package com.example.Z_AppsFather.Kotlin._3.Init

import com.example.Z_AppsFather.Kotlin._1.Model.ProduitsModel
import com.example.Z_AppsFather.Kotlin._1.Model.ProduitsModel.Companion.updateProduitsFireBase
import com.example.Z_AppsFather.Kotlin._1.Model.Z.Parent.AncienResourcesDataBaseMain
import com.example.Z_AppsFather.Kotlin._3.Init.Z.Parent.GetAncienDataBasesMain
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

suspend fun initializer(
    _appsHeadModel: ProduitsModel,
    initializationProgress: Float,
    onInitProgress: () -> (Int, AncienResourcesDataBaseMain) -> Unit
) {

    val NOMBRE_ENTRE = 100

    if (NOMBRE_ENTRE != 0  ) {
        CreeNewStart(
            _appsHeadModel,
            NOMBRE_ENTRE,
            onInitProgress(),
        )
    } else {
        //  LoadFromFirebaseHandler.loadFromFirebase(this)
    }
}


suspend fun CreeNewStart(
    _appsHeadModel: ProduitsModel,
    NOMBRE_ENTRE: Int,
    onInitProgress: (Int, AncienResourcesDataBaseMain) -> Unit
) {
    try {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val ancienData = GetAncienDataBasesMain()
        // Predefined clients for consistent data
        val clients = listOf(
            Triple(1L, "Client Alpha", "#FF5733"),
            Triple(2L, "Client Beta", "#33FF57"),
            Triple(3L, "Client Gamma", "#5733FF"),
            Triple(4L, "Client Delta", "#FF33E6"),
            Triple(5L, "Client Epsilon", "#33FFF3")
        )

        // Predefined grossists
        val grossists = listOf(
            Triple(1L, "Grossist Alpha", "#FF5733"),
            Triple(2L, "Grossist Beta", "#33FF57"),
            Triple(3L, "Grossist Gamma", "#5733FF")
        )

        // Filter and limit the products database
        val halfCount = NOMBRE_ENTRE / 2
        val filteredProducts = ancienData.produitsDatabase.let { products ->
            val olderProducts = products.filter { it.idArticle < 2000 }.take(halfCount)
            val newerProducts = products.filter { it.idArticle > 2000 }.take(halfCount)
            (olderProducts + newerProducts).take(NOMBRE_ENTRE)
        }

        // Rest of your existing code, but use filteredProducts instead of ancienData.produitsDatabase
        filteredProducts.forEachIndexed { index, ancien ->
            val depuitAncienDataBase = ProduitsModel.ProduitModel(
                id = ancien.idArticle,
                itsTempProduit = ancien.idArticle > 2000,
                init_nom = ancien.nomArticleFinale,
                init_visible = false,
                init_besoin_To_Be_Updated = true
            )

            // Add colors/tastes
            listOf(
                ancien.idcolor1 to 1L,
                ancien.idcolor2 to 2L,
                ancien.idcolor3 to 3L,
                ancien.idcolor4 to 4L
            ).forEach { (colorId, position) ->
                ancienData.couleurs_List.find { it.idColore == colorId }?.let { couleur ->
                    depuitAncienDataBase.coloursEtGouts.add(
                        ProduitsModel.ProduitModel.ColourEtGout_Model(
                            position_Du_Couleur_Au_Produit = position,
                            nom = couleur.nameColore,
                            imogi = couleur.iconColore,
                            sonImageNeExistPas = depuitAncienDataBase.itsTempProduit && position == 1L,
                        )
                    )
                }
            }

            // Generate sales history
            repeat((1..5).random()) { historyIndex ->
                val saleDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -(1..30).random())
                }.time

                val (clientId, clientName, clientColor) = clients.random()

                val bonVent = ProduitsModel.ProduitModel.ClientBonVentModel(
                    init_clientInformations = ProduitsModel.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = clientId,
                        nom = clientName,
                        couleur = clientColor
                    ),
                    init_colours_achete = depuitAncienDataBase.coloursEtGouts.take((1..3).random())
                        .map { couleur ->
                            ProduitsModel.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                vidPosition = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                quantity_Achete = (1..10).random(),
                                imogi = couleur.imogi
                            )
                        }
                )
                depuitAncienDataBase.historiqueBonsVents.add(bonVent)
            }

            // Generate current sales
            repeat((1..3).random()) { currentIndex ->
                val (clientId, clientName, clientColor) = clients.random()

                val bonVent = ProduitsModel.ProduitModel.ClientBonVentModel(
                    init_clientInformations = ProduitsModel.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = clientId,
                        nom = clientName,
                        couleur = clientColor
                    ),
                    init_colours_achete = depuitAncienDataBase.coloursEtGouts.take((1..3).random())
                        .map { couleur ->
                            ProduitsModel.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                vidPosition = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                quantity_Achete = (1..10).random(),
                                imogi = couleur.imogi
                            )
                        }
                )
                depuitAncienDataBase.bonsVentDeCetteCota.add(bonVent)
            }

            val (grossistId, grossistName, grossistColor) = grossists.random()
            val currentDate = dateFormat.format(Calendar.getInstance().time)

            val grossiste = ProduitsModel.ProduitModel.GrossistBonCommandes(
                vid = grossistId,
                init_grossistInformations = ProduitsModel.ProduitModel.GrossistBonCommandes.GrossistInformations(
                    id = grossistId,
                    nom = grossistName,
                    couleur = grossistColor
                ).apply {
                    positionInGrossistsList = grossistId.toInt() - 1
                },
                date = currentDate,
                date_String_Divise = currentDate.split(" ")[0],
                time_String_Divise = currentDate.split(" ")[1],
                currentCreditBalance = (1000..2000).random().toDouble(),
                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                if (Math.random() < 0.4) 0 else (1..10).random(),
                init_coloursEtGoutsCommendee = depuitAncienDataBase.coloursEtGouts
                    .take(if (depuitAncienDataBase.itsTempProduit) 1 else (1..4).random())
                    .map { couleur ->
                        ProduitsModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                            id = couleur.position_Du_Couleur_Au_Produit,
                            nom = couleur.nom,
                            emoji = couleur.imogi,
                            init_quantityAchete = (10..50).random()
                        )
                    }
            )

            depuitAncienDataBase.let { pro ->
                pro.statuesBase.prePourCameraCapture =
                    (pro.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: 0) > 0
                            && pro.itsTempProduit
            }

            depuitAncienDataBase.bonCommendDeCetteCota = grossiste
            depuitAncienDataBase.historiqueBonsCommend.add(grossiste)

            _appsHeadModel.produitsMainDataBase.add(depuitAncienDataBase)
            onInitProgress(index, ancienData)
        }

        // Clear and update Firebase database
        ProduitsModel.produitsFireBaseRef.removeValue()
        _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()


    } catch (e: Exception) {
        throw e
    } finally {
    }
}
