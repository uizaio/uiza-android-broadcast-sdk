@file:JvmName("StringKt")

package io.uiza.core.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser


@Deprecated("Use emptyString instead", ReplaceWith("emptyString"), level = DeprecationLevel.WARNING)
fun emptyString() = ""

const val emptyString = ""


fun String.toPrettyFormat(): String? {
    val parser = JsonParser()
    val json = parser.parse(this).asJsonObject
    return GsonBuilder().setPrettyPrinting().create().toJson(json)
}