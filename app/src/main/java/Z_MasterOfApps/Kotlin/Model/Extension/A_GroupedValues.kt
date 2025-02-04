package Z_MasterOfApps.Kotlin.Model.Extension

import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import android.util.Log

private const val TAG = "ProductsDebug"

val _ModelAppsFather.groupedProductsParGrossist: List<Map.Entry<C_GrossistsDataBase, List<_ModelAppsFather.ProduitModel>>>
    get() = grossistsDataBase.map { grossist ->
        // Find all products for this grossist
        val matchingProducts = produitsMainDataBase.filter { product ->
            // Log only for products 23 and 64
            if (product.id == 23L || product.id == 64L) {
                Log.d(TAG, """
                    Product Check:
                    ID: ${product.id}
                    Name: ${product.nom}
                    Current Grossist: ${grossist.nom} (ID: ${grossist.id})
                    Has BonCommend: ${product.bonCommendDeCetteCota != null}
                    BonCommend ID: ${product.bonCommendDeCetteCota?.idGrossistChoisi}
                    Match Result: ${product.bonCommendDeCetteCota?.idGrossistChoisi == grossist.id}
                    ------------------------
                """.trimIndent())
            }

            product.bonCommendDeCetteCota?.idGrossistChoisi == grossist.id
        }

        java.util.AbstractMap.SimpleEntry(grossist, matchingProducts)
    }.sortedBy { entry ->
        entry.key.statueDeBase.itPositionInParentList
    }

val _ModelAppsFather.groupedProductsParClients: List<Map.Entry<B_ClientsDataBase, List<_ModelAppsFather.ProduitModel>>>
    get() = clientDataBase.map { client ->
        val matchingProducts = produitsMainDataBase.filter { product ->
            product.bonsVentDeCetteCota.any { bonVent ->
                bonVent.clientIdChoisi == client.id
            }
        }

        java.util.AbstractMap.SimpleEntry(client, matchingProducts)
    }.sortedBy { entry ->
        entry.key.statueDeBase.positionDonClientsList
    }
