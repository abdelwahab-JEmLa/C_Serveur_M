package com.example.c_serveur.Modules.Z.Archives

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Packages.Z.Archives.Models.AppSettingsSaverModel
import com.example.Packages.Z.Archives.Models.ClientsDataBase
import com.example.Packages.Z.Archives.Models.DiviseurDeDisplayProductForEachClient
import com.example.Packages.Z.Archives.Models.Grossissts_DataBAse
import com.example.Packages.Z.Archives.Models.ProductsCategoriesDataBase
import com.example.Packages.Z.Archives.Models.Produits_DataBase
import com.example.Packages.Z.Archives.P3.Historique_D_Achate_Grossisst_DataBase
import com.example.Packages._1.Fragment.Z.Archives.Model.Archives.Commende_Produits_Au_Grossissts_DataBase

@Database(
    entities = [
        AppSettingsSaverModel::class,
        ClientsDataBase::class,
        Produits_DataBase::class,
        DiviseurDeDisplayProductForEachClient::class,
        ProductsCategoriesDataBase::class,
        Historique_D_Achate_Grossisst_DataBase::class,
        Grossissts_DataBAse::class,
        Commende_Produits_Au_Grossissts_DataBase::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    // All DAOs

    abstract fun appSettingsSaverModelDao(): AppSettingsSaverModelDao
    abstract fun clientsDataBaseDao(): ClientsDataBaseDao
    abstract fun productsDataBaseDao(): ProductsDataBaseDao
    abstract fun diviseurDeDisplayProductForEachClientDao(): DiviseurDeDisplayProductForEachClientDao
    abstract fun productsCategoriesDataBaseDao(): ProductsCategoriesDataBaseDao
    abstract fun historique_D_Achate_Grossisst_DataBase_Dao(): Historique_D_Achate_Grossisst_DataBaseDao
    abstract fun grossissts_DataBAse_Dao(): Grossissts_DataBAse_Dao
    abstract fun commende_Produits_Au_Grossissts_DataBase_Dao(): Commende_Produits_Au_Grossissts_DataBase_Dao

    // DatabaseModule.kt
    object DatabaseModule {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

