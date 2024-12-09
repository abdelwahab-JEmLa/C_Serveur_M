package com.example.clientjetpack.Modules

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Models.AppSettingsSaverModel
import com.example.Models.ClientsDataBase
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.Model.Commende_Produits_Au_Grossissts_DataBase
import com.example.Models.DiviseurDeDisplayProductForEachClient
import com.example.Models.Grossissts_DataBAse
import com.example.Models.ProductsCategoriesDataBase
import com.example.Models.Produits_DataBase
import com.example.Packages.P3.Historique_D_Achate_Grossisst_DataBase

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

