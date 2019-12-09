package io.uiza.core.models.v3
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
class ListV3Wrapper<T> {
    @Json(name = "data")
    var data: List<T>? = null
    @Json(name = "metadata")
    var metadata: MetadataV3? = null
    @Json(name = "version")
    var version: Int = 0
    @Json(name = "datetime")
    var datetime: Date? = null
    @Json(name = "policy")
    var policy: String? = null
    @Json(name = "requestId")
    var requestId: String? = null
    @Json(name = "serviceName")
    var serviceName: String? = null
    @Json(name = "message")
    var message: String? = null
    @Json(name = "code")
    var code: Int = 0
    @Json(name = "type")
    var type: String? = null
}


@JsonClass(generateAdapter = true)
class ObjectV3Wrapper<T> {
    @Json(name = "data")
    var data: T? = null
    @Json(name = "version")
    var version: Int = 0
    @Json(name = "datetime")
    var datetime: Date? = null
    @Json(name = "policy")
    var policy: String? = null
    @Json(name = "requestId")
    var requestId: String? = null
    @Json(name = "serviceName")
    var serviceName: String? = null
    @Json(name = "message")
    var message: String? = null
    @Json(name = "code")
    var code: Int = 0
    @Json(name = "type")
    var type: String? = null
}