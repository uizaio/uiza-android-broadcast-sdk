package io.uiza.core.parsers

import com.google.gson.annotations.SerializedName
import io.uiza.core.models.UizaEntity


public class ListEntityResponse @JvmOverloads constructor(
    @JvmField @SerializedName("next_page_token") var nextPageToken: String? = null,
    @JvmField @SerializedName("data") var entities: List<UizaEntity>? = null
)
