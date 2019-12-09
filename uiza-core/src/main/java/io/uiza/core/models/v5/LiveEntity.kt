package io.uiza.core.models.v5

import android.os.Parcelable
import android.text.TextUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.uiza.core.utils.toJson
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Uiza live entity
 * insert @JvmField into variable if you want direct access
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class LiveEntity @JvmOverloads constructor(
    @Json(name = "id") var id: String,
    @Json(name = "name") var name: String,
    @Json(name = "description") var description: String? = null,
    @Json(name = "ingest") var ingest: LiveIngest? = null,
    @Json(name = "playback") var playback: LivePlayback? = null,
    @Json(name = "region") var region: String? = null,
    @Json(name = "status") var status: String? = null,
    @Json(name = "broadcast") var broadcast: String? = null,
    @Json(name = "created_at") var createdAt: Date? = null,
    @Json(name = "updated_at") var updatedAt: Date? = null
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (other is LiveEntity) {
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

    fun hasLive(): Boolean {
        return status.equals("ready", true) && broadcast.equals(
            "offline",
            true
        )
    }

    fun needGetInfo(): Boolean {
        return status.equals("init", true)
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class LivePlayback @JvmOverloads constructor(
    @Json(name = "hls") var hls: String? = null
) : Parcelable {
    override fun toString(): String {
        return this.toJson()
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class LiveIngest @JvmOverloads constructor(
    @Json(name = "stream_url") var streamUrl: String? = null,
    @Json(name = "stream_key") var streamKey: String? = null
) : Parcelable {
    override fun toString(): String {
        return this.toJson()
    }

    fun getLiveUrl(): String? {
        return if (TextUtils.isEmpty(streamKey) || TextUtils.isEmpty(streamUrl)) {
            null
        } else {
            streamUrl?.replace("hls", "transcode") + "/" + streamKey
        }
    }
}

/**
 * Request body when call create an entity
 */
class CreateLiveEntityBody @JvmOverloads constructor(
    @Json(name = "name") var name: String? = null,
    @Json(name = "description") var description: String? = null,
    @Json(name = "region") var region: String? = null,
    @Json(name = "app_id") var appId: String? = null,
    @Json(name = "user_id") var userId: String? = null
)

/**
 * Request body when call on_publish done
 */
class OnPubDoneLiveEntityBody @JvmOverloads constructor(
    @Json(name = "call") var call: String? = null,
    @Json(name = "addr") var addr: String? = null,
    @Json(name = "client_id") var clientId: String? = null,
    @Json(name = "app_name") var appName: String? = null,
    @Json(name = "stream_key") var streamKey: String? = null,
    @Json(name = "session_id") var sessionId: String? = null,
    @Json(name = "node_id") var nodeId: String? = null
)

/**
 * Response of delete an entity
 */
class DeleteLiveEntityResponse @JvmOverloads constructor(
    @Json(name = "id") var id: String? = null,
    @Json(name = "deleted") var deleted: Boolean? = null
)
