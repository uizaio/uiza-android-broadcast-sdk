package io.uiza.core.models

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Uiza live entity
 */
@Parcelize
data class LiveEntity @JvmOverloads constructor(
    @JvmField @SerializedName("id") var id: String,
    @JvmField @SerializedName("name") var name: String,
    @JvmField @SerializedName("description") var description: String? = null,
    @JvmField @SerializedName("ingest") var ingest: LiveIngest? = null,
    @JvmField @SerializedName("playback") var playback: LivePlayback? = null,
    @JvmField @SerializedName("region") var region: String? = null,
    @JvmField @SerializedName("status") var status: String? = null,
    @JvmField @SerializedName("broadcast") var broadcast: String? = null,
    @JvmField @SerializedName("created_at") var createdAt: Date? = null,
    @JvmField @SerializedName("updated_at") var updatedAt: Date? = null
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
        return (Gson().toJson(this))
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
data class LivePlayback @JvmOverloads constructor(@JvmField @SerializedName("hls") var hls: String? = null) :
    Parcelable {
    override fun toString(): String {
        return (Gson().toJson(this))
    }
}

@Parcelize
data class LiveIngest @JvmOverloads constructor(
    @JvmField @SerializedName("stream_url") var streamUrl: String? = null,
    @JvmField @SerializedName("stream_key") var streamKey: String? = null
) : Parcelable {
    override fun toString(): String {
        return (Gson().toJson(this))
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
    @JvmField @SerializedName("name") var name: String? = null,
    @JvmField @SerializedName("description") var description: String? = null,
    @JvmField @SerializedName("region") var region: String? = null,
    @JvmField @SerializedName("app_id") var appId: String? = null,
    @JvmField @SerializedName("user_id") var userId: String? = null
)

/**
 * Request body when call on_publish done
 */
class OnPubDoneLiveEntityBody @JvmOverloads constructor(
    @JvmField @SerializedName("call") var call: String? = null,
    @JvmField @SerializedName("addr") var addr: String? = null,
    @JvmField @SerializedName("client_id") var clientId: String? = null,
    @JvmField @SerializedName("app_name") var appName: String? = null,
    @JvmField @SerializedName("stream_key") var streamKey: String? = null,
    @JvmField @SerializedName("session_id") var sessionId: String? = null,
    @JvmField @SerializedName("node_id") var nodeId: String? = null
)

/**
 * Response of delete an entity
 */
class DeleteLiveEntityResponse @JvmOverloads constructor(
    @JvmField @SerializedName("id") var id: String? = null,
    @JvmField @SerializedName("deleted") var deleted: Boolean? = null
)
