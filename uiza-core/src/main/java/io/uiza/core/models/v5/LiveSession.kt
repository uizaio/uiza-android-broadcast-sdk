package io.uiza.core.models.v5

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.uiza.core.utils.toJson
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class LiveSession @JvmOverloads constructor(
    @Json(name = "id") var id: String,
    @Json(name = "entity_id") var entityId: String,
    @Json(name = "stream_key") var streamKey: String,
    @Json(name = "server") var server: String,
    @Json(name = "duration") var duration: Float = 0.0F,
    @Json(name = "created_at") var createdAt: Date? = null,
    @Json(name = "updated_at") var updateAt: Date? = null
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other is LiveSession) {
            return other.id == this.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return this.toJson()
    }
}
