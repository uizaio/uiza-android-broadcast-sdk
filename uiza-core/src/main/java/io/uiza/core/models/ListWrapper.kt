package io.uiza.core.models

import com.google.gson.annotations.SerializedName

class ListWrapper<T> {
    @SerializedName("data")
    var data: List<T>? = null
    @SerializedName("next_page_token")
    var nextPageToken: String? = null
}