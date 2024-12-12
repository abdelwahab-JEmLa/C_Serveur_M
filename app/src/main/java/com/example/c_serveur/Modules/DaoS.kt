package com.example.clientjetpack.Modules

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.Models.AppSettingsSaverModel
import com.example.Models.ClientsDataBase
import com.example.Models.DiviseurDeDisplayProductForEachClient
import com.example.Models.Grossissts_DataBAse
import com.example.Models.ProductsCategoriesDataBase
import com.example.Models.Produits_DataBase
import com.example.Packages.P3.Historique_D_Achate_Grossisst_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Commende_Produits_Au_Grossissts_DataBase
import kotlinx.coroutines.flow.Flow

@Dao
interface Commende_Produits_Au_Grossissts_DataBase_Dao {
    @Query("SELECT COUNT(*) FROM Commende_Produits_Au_Grossissts_DataBase")
    suspend fun count(): Int

    @Query("SELECT * FROM Commende_Produits_Au_Grossissts_DataBase")
    fun getAllFlow(): Flow<List<Commende_Produits_Au_Grossissts_DataBase>>

    @Query("SELECT * FROM Commende_Produits_Au_Grossissts_DataBase")
    suspend fun getAll(): List<Commende_Produits_Au_Grossissts_DataBase>

    @Upsert
    suspend fun upsert(item: Commende_Produits_Au_Grossissts_DataBase)

    @Update
    suspend fun update(item: Commende_Produits_Au_Grossissts_DataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: Commende_Produits_Au_Grossissts_DataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Commende_Produits_Au_Grossissts_DataBase>)

    @Upsert
    suspend fun upsertAll(categories: List<Commende_Produits_Au_Grossissts_DataBase>)

    @Delete
    suspend fun delete(item: Commende_Produits_Au_Grossissts_DataBase)

    @Query("DELETE FROM Commende_Produits_Au_Grossissts_DataBase")
    suspend fun deleteAll()
}
@Dao
interface Grossissts_DataBAse_Dao {
    @Query("SELECT COUNT(*) FROM Grossissts_DataBAse")
    suspend fun count(): Int

    @Query("SELECT * FROM Grossissts_DataBAse")
    fun getAllFlow(): Flow<List<Grossissts_DataBAse>>

    @Query("SELECT * FROM Grossissts_DataBAse")
    suspend fun getAll(): List<Grossissts_DataBAse>

    @Upsert
    suspend fun upsert(item: Grossissts_DataBAse)

    @Update
    suspend fun update(item: Grossissts_DataBAse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: Grossissts_DataBAse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Grossissts_DataBAse>)

    @Upsert
    suspend fun upsertAll(categories: List<Grossissts_DataBAse>)

    @Delete
    suspend fun delete(item: Grossissts_DataBAse)

    @Query("DELETE FROM Grossissts_DataBAse")
    suspend fun deleteAll()
}


@Dao
interface Historique_D_Achate_Grossisst_DataBaseDao {

    @Query("SELECT COUNT(*) FROM Historique_D_Achate_Grossisst_DataBase")
    suspend fun count(): Int

    @Query("SELECT * FROM Historique_D_Achate_Grossisst_DataBase")
    fun getAllFlow(): Flow<List<Historique_D_Achate_Grossisst_DataBase>>

    @Query("SELECT * FROM Historique_D_Achate_Grossisst_DataBase")
    suspend fun getAll(): List<Historique_D_Achate_Grossisst_DataBase>

    @Upsert
    suspend fun upsert(item: Historique_D_Achate_Grossisst_DataBase)

    @Update
    suspend fun update(item: Historique_D_Achate_Grossisst_DataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: Historique_D_Achate_Grossisst_DataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Historique_D_Achate_Grossisst_DataBase>)

    @Upsert
    suspend fun upsertAll(categories: List<Historique_D_Achate_Grossisst_DataBase>)

    @Delete
    suspend fun delete(item: Historique_D_Achate_Grossisst_DataBase)
    @Query("DELETE FROM Historique_D_Achate_Grossisst_DataBase")
    suspend fun deleteAll()
}

@Dao
interface ProductsCategoriesDataBaseDao {

    @Query("SELECT COUNT(*) FROM ProductsCategoriesDataBase")
    suspend fun count(): Int

    @Query("SELECT * FROM ProductsCategoriesDataBase")
    fun getAllFlow(): Flow<List<ProductsCategoriesDataBase>>

    @Query("SELECT * FROM ProductsCategoriesDataBase")
    suspend fun getAll(): List<ProductsCategoriesDataBase>


    @Upsert
    suspend fun upsert(item: ProductsCategoriesDataBase)

    @Update
    suspend fun update(item: ProductsCategoriesDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: ProductsCategoriesDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProductsCategoriesDataBase>)

    @Upsert
    suspend fun upsertAll(categories: List<ProductsCategoriesDataBase>)

    @Delete
    suspend fun delete(item: ProductsCategoriesDataBase)

    @Query("DELETE FROM ProductsCategoriesDataBase")
    suspend fun deleteAll()
}

@Dao
interface DiviseurDeDisplayProductForEachClientDao {

    @Query("SELECT COUNT(*) FROM DiviseurDeDisplayProductForEachClient")
    suspend fun count(): Int

    @Query("SELECT * FROM DiviseurDeDisplayProductForEachClient")
    fun getAllFlow(): Flow<List<DiviseurDeDisplayProductForEachClient>>

    @Query("SELECT * FROM DiviseurDeDisplayProductForEachClient")
    suspend fun getAll(): List<DiviseurDeDisplayProductForEachClient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DiviseurDeDisplayProductForEachClient)

    @Update
    suspend fun update(item: DiviseurDeDisplayProductForEachClient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: DiviseurDeDisplayProductForEachClient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DiviseurDeDisplayProductForEachClient>)

    @Delete
    suspend fun delete(item: DiviseurDeDisplayProductForEachClient)

    @Query("DELETE FROM DiviseurDeDisplayProductForEachClient")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(categories: List<ProductsCategoriesDataBase>)

}

@Dao
interface ClientsDataBaseDao {
    @Query("SELECT COUNT(*) FROM ClientsDataBase")
    suspend fun count(): Int

    @Query("SELECT * FROM ClientsDataBase")
    fun getAllFlow(): Flow<List<ClientsDataBase>>

    @Query("SELECT * FROM ClientsDataBase")
    suspend fun getAll(): List<ClientsDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ClientsDataBase)

    @Update
    suspend fun update(item: ClientsDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<ClientsDataBase>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ClientsDataBase>)

    @Delete
    suspend fun delete(item: ClientsDataBase)

    @Query("DELETE FROM ClientsDataBase")
    suspend fun deleteAll()

}

@Dao
interface ProductsDataBaseDao {

    @Query("SELECT COUNT(*) FROM Produits_DataBase")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM Produits_DataBase")
    suspend fun getCount(): Int

    @Query("SELECT * FROM Produits_DataBase")
    fun getAllFlow(): Flow<List<Produits_DataBase>>

    @Query("SELECT * FROM Produits_DataBase")
    suspend fun getAll(): List<Produits_DataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: Produits_DataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<Produits_DataBase>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Produits_DataBase>)

    @Delete
    suspend fun delete(item: Produits_DataBase)

    @Query("DELETE FROM Produits_DataBase")
    suspend fun deleteAll()
    @Upsert
    suspend fun upsertAll(categories: List<ProductsCategoriesDataBase>)

}



@Dao
interface AppSettingsSaverModelDao {
    @Query("SELECT * FROM AppSettingsSaverModel")
    fun getAllFlow(): Flow<List<AppSettingsSaverModel>>

    @Query("SELECT * FROM AppSettingsSaverModel")
    suspend fun getAll(): List<AppSettingsSaverModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: AppSettingsSaverModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<AppSettingsSaverModel>)

    @Delete
    suspend fun delete(item: AppSettingsSaverModel)

    @Query("DELETE FROM AppSettingsSaverModel")
    suspend fun deleteAll()

}





