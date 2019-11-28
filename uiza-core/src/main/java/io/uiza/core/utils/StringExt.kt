@file:JvmName("StringKt")

package io.uiza.core.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser


@Deprecated("Use emptyString instead", ReplaceWith("emptyString"), level = DeprecationLevel.WARNING)
fun emptyString() = ""

const val emptyString = ""


fun toPrettyFormat(jsonString: String?): String? {
    val parser = JsonParser()
    val json = parser.parse(jsonString).asJsonObject
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(json)
}