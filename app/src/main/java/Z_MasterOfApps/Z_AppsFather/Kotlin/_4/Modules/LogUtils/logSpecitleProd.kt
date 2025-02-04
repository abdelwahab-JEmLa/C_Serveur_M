package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.LogUtils

import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import android.content.ContentValues.TAG
import android.util.Log

fun logSpecitleProd(
    product: _ModelAppsFather.ProduitModel,
    grossist: C_GrossistsDataBase
    ) {
        // Log only for products 23 and 64
        if (product.id == 23L || product.id == 64L) {
            Log.d(
                TAG, """
                    Product Check:
                    ID: ${product.id}
                    Name: ${product.nom}
                    Current Grossist: ${grossist.nom} (ID: ${grossist.id})
                    Has BonCommend: ${product.bonCommendDeCetteCota != null}
                    BonCommend ID: ${product.bonCommendDeCetteCota?.idGrossistChoisi}
                    Match Result: ${product.bonCommendDeCetteCota?.idGrossistChoisi == grossist.id}
                    ------------------------
                """.trimIndent()
            )
        }
    }
