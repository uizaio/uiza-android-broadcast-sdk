package io.uiza.core.models.v3

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.uiza.core.utils.toJson
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class LiveV3Entity constructor(
    @Json(name = "id") var id: String? = null,
    @Json(name = "name") var name: String? = null,
    @Json(name = "description") var description: String? = null,
    @Json(name = "shortDescription") var shortDescription: String? = null,
    @Json(name = "view") var view: Long? = null,
    @Json(name = "poster") var poster: String? = null,
    @Json(name = "thumbnail") var thumbnail: String? = null,
    @Json(name = "type") var type: String? = null,
    @Json(name = "duration") var duration: String? = null,
    @Json(name = "embedMetadata") var embedMetadata: String? = null,
    @Json(name = "publishToCdn") var publishToCdn: String? = null,
    @Json(name = "extendMetadata") var extendMetadata: String? = null,
    @Json(name = "createdAt") var createdAt: Date? = null,
    @Json(name = "updatedAt") var updatedAt: Date? = null
) : Parcelable {

    override fun toString(): String {
        return this.toJson()
    }

    companion object {
        @JvmStatic
        fun toJson(list: List<LiveV3Entity>): String {
            return list.toJson()
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
class DataToken constructor(
    @Json(name = "token") var token: String? = null
) : Parcelable {
    override fun toString(): String {
        return this.toJson()
    }
}

@JsonClass(generateAdapter = true)
class TokenStreamBody constructor(
    @Json(name = "entity_id") var entityId: String? = null,
    @Json(name = "app_id") var appId: String? = null,
    @Json(name = "content_type") var contentType: String? = null
) {
    companion object {
        const val STREAM = "stream"
        const val STATIC = "static"
        const val CATCHUP = "catchup"
        const val LIVE = "live"
    }
}
