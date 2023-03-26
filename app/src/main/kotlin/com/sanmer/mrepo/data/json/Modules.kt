package com.sanmer.mrepo.data.json

import com.sanmer.mrepo.data.module.OnlineModule
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Modules(
    val name: String,
    val timestamp: Float,
    val modules: List<OnlineModule>,
    @Json(ignore = true) val url: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Modules -> url == other.url
            else -> false
        }
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}