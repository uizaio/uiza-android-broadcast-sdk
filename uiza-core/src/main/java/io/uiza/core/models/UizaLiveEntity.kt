package io.uiza.core.models

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UizaLiveEntity @JvmOverloads constructor(
    @JvmField @SerializedName("id") var id: String,
    @JvmField @SerializedName("name") var name: String,
    @JvmField @SerializedName("description") var description: String? = null,
    @JvmField @SerializedName("ingest") var ingest: UizaIngest? = null,
    @JvmField @SerializedName("playback") var playback: UizaPlayback? = null,
    @JvmField @SerializedName("region") var region: String? = null,
    @JvmField @SerializedName("status") var status: String? = null,
    @JvmField @SerializedName("broadcast") var broadcast: String? = null,
    @JvmField @SerializedName("created_at") var createdAt: String? = null,
    @JvmField @SerializedName("updated_at") var updatedAt: String? = null
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other is UizaLiveEntity) {
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
data class UizaPlayback @JvmOverloads constructor(@JvmField @SerializedName("hls") var hls: String? = null) :
    Parcelable {
    override fun toString(): String {
        return (Gson().toJson(this))
    }

    fun getLinkPlay(): String? {
        return hls?.replace("/hls/", "/fmp4/")?.replace("index.m3u8", "master.m3u8")
    }
}

@Parcelize
data class UizaIngest @JvmOverloads constructor(
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

class CreateEntityBody @JvmOverloads constructor(
    @JvmField @SerializedName("name") var name: String? = null,
    @JvmField @SerializedName("description") var description: String? = null,
    @JvmField @SerializedName("region") var region: String? = null,
    @JvmField @SerializedName("app_id") var appId: String? = null,
    @JvmField @SerializedName("user_id") var userId: String? = null
)

class DeleteEntityResponse @JvmOverloads constructor(
    @JvmField @SerializedName("id") var id: String? = null,
    @JvmField @SerializedName("deleted") var deleted: Boolean? = null
)

class ListEntityResponse @JvmOverloads constructor(
    @JvmField @SerializedName("next_page_token") var nextPageToken: String? = null,
    @JvmField @SerializedName("data") var entities: List<UizaLiveEntity>? = null
)
