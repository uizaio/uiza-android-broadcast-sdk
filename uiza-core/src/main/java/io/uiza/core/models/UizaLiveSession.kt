package io.uiza.core.models

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UizaLiveSession @JvmOverloads constructor(
    @JvmField @SerializedName("id") var id: String,
    @JvmField @SerializedName("entity_id") var entityId: String,
    @JvmField @SerializedName("stream_key") var streamKey: String,
    @JvmField @SerializedName("server") var server: String,
    @JvmField @SerializedName("duration") var duration: Float = 0.0F,
    @JvmField @SerializedName("created_at") var createdAt: Date? = null,
    @JvmField @SerializedName("updated_at") var updateAt: Date? = null
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
}


class ListSessionResponse @JvmOverloads constructor(
    @JvmField @SerializedName("next_page_token") var nextPageToken: String? = null,
    @JvmField @SerializedName("data") var sessions: List<UizaLiveSession>? = null
)
