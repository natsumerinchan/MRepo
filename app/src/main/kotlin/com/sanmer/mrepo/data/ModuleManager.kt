package com.sanmer.mrepo.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.data.database.ModuleDatabase
import com.sanmer.mrepo.data.database.entity.toEntity
import com.sanmer.mrepo.data.database.entity.toModule
import com.sanmer.mrepo.data.module.LocalModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ModuleManager {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: ModuleDatabase
    private val moduleDao get() = db.moduleDao()

    var local by mutableStateOf(0)
        private set
    var online by mutableStateOf(0)
        private set

    fun init(context: Context): ModuleDatabase {
        db = ModuleDatabase.getDatabase(context)
        coroutineScope.launch {
            getLocalAll()
            getOnlineAll()
        }

        return db
    }

    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        moduleDao.getLocalAll().map { it.toModule() }.apply {
            local = size
        }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        moduleDao.insertLocal(value.toEntity())
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        local = list.size
        moduleDao.deleteLocalAll()
        moduleDao.insertLocal(list.map { it.toEntity() })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        moduleDao.deleteLocalAll()
    }

    suspend fun getOnlineAll() = RepoManger.getModuleAll().apply {
        online = size
    }
}