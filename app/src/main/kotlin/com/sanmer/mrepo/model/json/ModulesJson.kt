package com.sanmer.mrepo.model.json

import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.RepoMetadata
import com.sanmer.mrepo.model.module.OnlineModule
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModulesJson(
    val name: String,
    val timestamp: Float,
    val modules: List<OnlineModule>,
    val metadata: ModulesJsonMetadata = ModulesJsonMetadata.default
)

@JsonClass(generateAdapter = true)
data class ModulesJsonMetadata(
    val version: String,
    val versionCode: Int
) {
    companion object {
        val default = ModulesJsonMetadata(
            version = RepoMetadata.default.version,
            versionCode = RepoMetadata.default.versionCode
        )
    }
}

fun Repo.copy(modulesJson: ModulesJson) = copy(
    name = modulesJson.name,
    size = modulesJson.modules.size,
    timestamp = modulesJson.timestamp,
    version = modulesJson.metadata.version,
    versionCode = modulesJson.metadata.versionCode
)