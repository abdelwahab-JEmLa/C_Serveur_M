package Z_MasterOfApps.Kotlin.Model.Extension

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather

val _ModelAppsFather.clientsDisponible: List<_ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations>
    get() = produitsMainDataBase
        .flatMap { product ->
            // Get all current client information
            val currentClients = product.bonsVentDeCetteCota.mapNotNull { it.clientInformations }

            // Get all historical client information
            val historicalClients = product.historiqueBonsVents.mapNotNull { it.clientInformations }

            // Combine both lists
            currentClients + historicalClients
        }
        .distinctBy { it.id } // Remove duplicates based on client ID
        .sortedBy { it.positionDonClientsList }
val _ModelAppsFather.grossistsDisponible: List<_ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations>
    get() = produitsMainDataBase
        .flatMap { product ->
            // Get grossist info from current bon commande
            listOfNotNull(product.bonCommendDeCetteCota?.grossistInformations) +
                    // Get grossist info from historical bon commandes
                    product.historiqueBonsCommend.mapNotNull { it.grossistInformations }
        }
        .distinctBy { it.id } // Remove duplicates based on grossist ID
        .sortedBy { it.positionInGrossistsList }

val _ModelAppsFather.groupedProductsPatGrossist: List<Pair<_ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations, List<_ModelAppsFather.ProduitModel>>>
    get() = produitsMainDataBase
        .mapNotNull { product ->
            product.bonCommendDeCetteCota?.grossistInformations?.let { grossistInfo ->
                grossistInfo to product
            }
        }
        .groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
        .toList()
        .sortedBy { (grossist, _) ->
            grossist.positionInGrossistsList
        }


val _ModelAppsFather.groupedProductsParClients: List<Map.Entry<ClientsDataBase, List<_ModelAppsFather.ProduitModel>>>
    get() = clientDataBaseSnapList.map { client ->
        // Get all products where this client has associated bon vents
        val matchingProducts = produitsMainDataBase.filter { product ->
            // Check current bon vents
            product.bonsVentDeCetteCota.any { bonVent ->
                bonVent.clientInformations?.id == client.id
            }
        }

        // Create a map entry using AbstractMap.SimpleEntry
        java.util.AbstractMap.SimpleEntry(client, matchingProducts)
    }.sortedBy { entry ->
        entry.key.statueDeBase.positionDonClientsList
    }
