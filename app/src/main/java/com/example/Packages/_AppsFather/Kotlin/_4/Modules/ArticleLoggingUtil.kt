// Add this to a new file: ArticleLoggingUtil.kt
package com.example.Packages._AppsFather.Kotlin._4.Modules

import android.util.Log
import com.example.Packages._AppsFather.Kotlin._1.Model.ArticleInfosModel
import com.example.Packages._AppsFather.Kotlin._1.Model.ColourEtGoutInfosModel

object ArticleLoggingUtil {
    private const val TAG = "ArticleChanges"

    fun logArticleListChange(
        grossistName: String,
        positionedArticles: List<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>,
        nonPositionedArticles: List<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>
    ) {
        Log.d(TAG, "=== Article List Update for Grossist: $grossistName ===")
        Log.d(TAG, "Positioned Articles (${positionedArticles.size}):")
        positionedArticles.forEachIndexed { index, article ->
            Log.d(TAG, "${index + 1}. ${article.key.nom} (ID: ${article.key.id})")
            article.value.forEach { colorEntry ->
                Log.d(TAG, "   - ${colorEntry.key.nom}: ${colorEntry.value}")
            }
        }
        
        Log.d(TAG, "Non-Positioned Articles (${nonPositionedArticles.size}):")
        nonPositionedArticles.forEach { article ->
            Log.d(TAG, "- ${article.key.nom} (ID: ${article.key.id})")
            article.value.forEach { colorEntry ->
                Log.d(TAG, "   - ${colorEntry.key.nom}: ${colorEntry.value}")
            }
        }
        Log.d(TAG, "=== End of Update ===")
    }

    fun logDisplayUpdate(
        positionedCount: Int,
        nonPositionedCount: Int
    ) {
        Log.d(TAG, "Display Update - Positioned: $positionedCount, Non-Positioned: $nonPositionedCount")
    }
}
