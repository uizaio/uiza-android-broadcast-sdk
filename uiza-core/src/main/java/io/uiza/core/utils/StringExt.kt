@file:JvmName("StringKt")

package io.uiza.core.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.uiza.core.deserializers.DateTimeAdapter


@Deprecated("Use emptyString instead", ReplaceWith("emptyString"), level = DeprecationLevel.WARNING)
fun emptyString() = ""

const val emptyString = ""


inline fun <reified T> T.toJson(): String {
    return (Moshi.Builder().add(DateTimeAdapter())).build()
        .adapter(T::class.java)
        .indent("    ")
        .toJson(this)
}

inline fun <reified T> List<T>.toJson(): String {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    return (Moshi.Builder().add(DateTimeAdapter())).build()
        .adapter<List<T>>(type)
        .indent("   ")
        .toJson(this)
}