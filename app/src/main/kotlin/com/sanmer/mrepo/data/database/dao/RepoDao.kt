package com.sanmer.mrepo.data.database.dao

import androidx.room.*
import com.sanmer.mrepo.data.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.database.entity.RepoWithModule

@Dao
interface RepoDao {
    @Transaction
    @Query("SELECT * FROM repo")
    fun getAllRepoWithModule(): List<RepoWithModule>

    @Query("SELECT * FROM repo")
    fun getRepoAll(): List<Repo>

    @Query("SELECT * FROM repo WHERE url LIKE :url LIMIT 1")
    fun getRepoByUrl(url: String): Repo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(value: Repo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRepo(value: Repo)

    @Delete
    suspend fun deleteRepo(value: Repo)

    @Query("SELECT * FROM online_module")
    fun getModuleAll(): List<OnlineModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(value: OnlineModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(list: List<OnlineModuleEntity>)

    @Query("DELETE from online_module where repo_url = :repoUrl")
    suspend fun deleteModule(repoUrl: String)

    @Query("DELETE FROM online_module")
    suspend fun deleteModuleAll()
}