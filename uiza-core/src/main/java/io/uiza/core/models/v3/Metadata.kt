package io.uiza.core.models.v3

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.uiza.core.utils.toJson
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class MetadataDetail constructor(
    @Json(name = "id") var id: String? = null,
    @Json(name = "name") var name: String? = null,
    @Json(name = "description") var description: String? = null,
    @Json(name = "slug") var slug: String? = null,
    @Json(name = "type") var type: String? = null,
    @Json(name = "orderNumber") var orderNumber: Int? = null,
    @Json(name = "icon") var icon: String? = null,
    @Json(name = "status") var status: Int? = null,
    @Json(name = "createdAt") var createdAt: Date? = null,
    @Json(name = "updatedAt") var updatedAt: Date? = null
) : Parcelable {

    override fun toString(): String {
        return this.toJson()
    }

    companion object {
        @JvmStatic
        fun toJson(list: List<MetadataDetail>): String {
            return list.toJson()
        }
    }
}

@JsonClass(generateAdapter = true)
class CreateMetadataBody constructor(
    @Json(name = "id") var id: String? = null,
    @Json(name = "name") var name: String? = null,
    @Json(name = "type") var type: String? = null,
    @Json(name = "description") var description: String? = null,
    @Json(name = "orderNumber") var orderNumber: Int? = null,
    @Json(name = "icon") var icon: String? = null
) {
    companion object {
        const val TYPE_FOLDER = "folder"
        const val TYPE_PLAYLIST = "playlist"
        const val TYPE_TAG = "tag"
    }
}