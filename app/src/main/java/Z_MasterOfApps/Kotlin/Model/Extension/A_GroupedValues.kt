package Z_MasterOfApps.Kotlin.Model.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather

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

val _ModelAppsFather.groupedProductsParClients: List<Pair<_ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations, List<_ModelAppsFather.ProduitModel>>>
    get() = produitsMainDataBase
        .asSequence()
        .filter { product ->
            product.bonsVentDeCetteCota.isNotEmpty() &&
                    product.bonsVentDeCetteCota.any { it.clientInformations != null }
        }
        .flatMap { product ->
            product.bonsVentDeCetteCota.mapNotNull { bonVent ->
                bonVent.clientInformations?.let { clientInfo ->
                    clientInfo to product
                }
            }
        }
        .groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
        .toList()
        .sortedBy { (client, _) ->
            client.positionDonClientsList
        }
        .toList()
