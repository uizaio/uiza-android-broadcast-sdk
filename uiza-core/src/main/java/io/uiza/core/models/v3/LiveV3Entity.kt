package io.uiza.core.models.v3

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.uiza.core.utils.toJson
import java.util.*

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
) {

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

@JsonClass(generateAdapter = true)
class LastPullInfo constructor(
    @Json(name = "primaryInputUri") var primaryInputUri: String? = null,
    @Json(name = "secondaryInputUri") var secondaryInputUri: Any? = null
)

@JsonClass(generateAdapter = true)
class LastPushInfo constructor(
    @Json(name = "streamUrl") var streamUrl: String? = null,
    @Json(name = "streamKey") var streamKey: String? = null
)
