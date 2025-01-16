// LogUtils.kt
package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules

object LogUtils {
    private const val APP_TAG = "MasterOfApps"

    object Tags {
        const val BON_COMMANDES = "BonCommandes"
        const val QUANTITY_BUTTON = "QuantityButton"
        const val DELETE_SALE = "DeleteSale"
        const val PRODUCT_UPDATE = "ProductUpdate"
    }

    fun logBonCommandes(message: String) {
        android.util.Log.d("$APP_TAG/${Tags.BON_COMMANDES}", message)
    }

    fun logQuantity(message: String) {
        android.util.Log.d("$APP_TAG/${Tags.QUANTITY_BUTTON}", message)
    }

    fun logDelete(message: String) {
        android.util.Log.d("$APP_TAG/${Tags.DELETE_SALE}", message)
    }

    fun logProduct(message: String) {
        android.util.Log.d("$APP_TAG/${Tags.PRODUCT_UPDATE}", message)
    }

    fun logError(tag: String, message: String, error: Throwable? = null) {
        android.util.Log.e("$APP_TAG/$tag", message, error)
    }

    fun logProductState(product: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel) {
        logProduct("""
            Product State:
            - ID: ${product.id}
            - Name: ${product.nom}
            - Active Sales: ${product.bonsVentDeCetteCota.size}
            - Has BonCommande: ${product.bonCommendDeCetteCota != null}
            - Colors Count: ${product.coloursEtGouts.size}
        """.trimIndent())
    }
}
