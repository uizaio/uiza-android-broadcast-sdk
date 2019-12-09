package io.uiza.core.models.v3

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.uiza.core.utils.toJson
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
@JsonClass(generateAdapter = true)
data class MetadataV3 @JvmOverloads constructor(
    @Json(name = "total") var total: Int = 0,
    @Json(name = "result") var result: Int = 0,
    @Json(name = "page") var page: Int = 0,
    @Json(name = "limit") var limit: Int = 0
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class User @JvmOverloads constructor(
    @Json(name = "id") var id: String,
    @Json(name = "isAdmin") var isAdmin: Int,
    @Json(name = "username") var username: String,
    @Json(name = "email") var email: String,
    @Json(name = "avatar") var avatar: String?,
    @Json(name = "fullName") var fullName: String?,
    @Json(name = "updatedAt") var updatedAt: Date? = null,
    @Json(name = "createdAt") var createdAt: Date? = null,
    @Json(name = "status") var status: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other is User) {
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


    companion object {
        @JvmStatic
        fun toJson(list: List<User>): String {
            return list.toJson()
        }
    }
}

@JsonClass(generateAdapter = true)
class IdResponse constructor(
    @Json(name = "id") var id: String? = null,
    @Json(name = "result") var result: Boolean? = null
) {
    override fun toString(): String {
        return this.toJson()
    }
}

@JsonClass(generateAdapter = true)
class RequestUserBody(
    @Json(name = "id") var id: String? = null,
    @Json(name = "status") var status: Long,
    @Json(name = "username") var username: String,
    @Json(name = "email") var email: String,
    @Json(name = "password") var password: String,
    @Json(name = "avatar") var avatar: String? = null,
    @Json(name = "fullname") var fullname: String? = null,
    @Json(name = "dob") var dob: String? = null,
    @Json(name = "gender") var gender: Long = 0,
    @Json(name = "isAdmin") var isAdmin: Long = 0
)

@JsonClass(generateAdapter = true)
class DeleteUserBody constructor(
    @Json(name = "id") var id: String? = null
)

@JsonClass(generateAdapter = true)
class UpdatePasswordBody constructor(
    @Json(name = "id") var id: String? = null,
    @Json(name = "oldPassword") var oldPassword: String? = null,
    @Json(name = "newPassword") var newPassword: String? = null
)