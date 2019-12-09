package io.uiza.core.models.v5

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ListWrapper<T> {
    @Json(name = "data")
    var data: List<T>? = null
    @Json(name = "next_page_token")
    var nextPageToken: String? = null
}