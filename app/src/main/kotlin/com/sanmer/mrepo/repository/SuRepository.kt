package com.sanmer.mrepo.repository

import com.sanmer.mrepo.api.local.ModulesLocalApi
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.provider.SuProvider
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuRepository @Inject constructor(
    private val suProvider: SuProvider
) {
    private val api: ModulesLocalApi get() = suProvider.getModulesApi()

    val state: StateFlow<Event> get() = suProvider.state
    val pid get() = suProvider.pid
    val context get() = suProvider.context
    val enforce get() = suProvider.enforce
    val fs: FileSystemManager get() = suProvider.getFileSystemManager()

    val version get() = api.version
    suspend fun getModules(): Result<List<LocalModule>> = api.getModules()
    fun enable(module: LocalModule) = api.enable(module)
    fun disable(module: LocalModule) = api.disable(module)
    fun remove(module: LocalModule) = api.remove(module)
    fun install(
        console: (String) -> Unit,
        onSuccess: (LocalModule) -> Unit,
        onFailure: () -> Unit,
        zipFile: File
    ) = api.install(console, onSuccess, onFailure, zipFile)
}