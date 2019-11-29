package io.uiza.core.models

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UizaEntity @JvmOverloads constructor(
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
    override fun toString(): String {
        return (Gson().toJson(this))
    }
}

@Parcelize
data class UizaPlayback @JvmOverloads constructor(@JvmField @SerializedName("hls") var hls: String? = null) :
    Parcelable {
    override fun toString(): String {
        return (Gson().toJson(this))
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
}

class CreateEntityBody @JvmOverloads constructor(
    @JvmField @SerializedName("app_id") var appId: String? = null,
    @JvmField @SerializedName("name") var name: String? = null,
    @JvmField @SerializedName("region") var region: String? = null,
    @JvmField @SerializedName("user_id") var userId: String? = null,
    @JvmField @SerializedName("description") var description: String? = null
)
