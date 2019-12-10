package io.uiza.core.api

import io.reactivex.Observable
import io.uiza.core.models.v3.*
import retrofit2.http.*

interface UizaV3Service {
    //https://docs.uiza.io/#list-all-users
    @GET("/api/public/v3/admin/user")
    fun getUsers(): Observable<ListV3Wrapper<User>>

    //https://docs.uiza.io/#retrieve-an-user
    @GET("/api/public/v3/admin/user")
    fun getUser(@Query("id") id: String): Observable<ObjectV3Wrapper<User>>

    //https://docs.uiza.io/#create-an-user
    @POST("/api/public/v3/admin/user")
    fun createUser(@Body createUserBody: RequestUserBody): Observable<ObjectV3Wrapper<IdResponse>>

    //https://docs.uiza.io/#update-an-user
    @PUT("/api/public/v3/admin/user")
    fun updateUser(@Body updateUser: RequestUserBody?): Observable<ObjectV3Wrapper<User>>

    //https://docs.uiza.io/#update-an-user
    @HTTP(method = "DELETE", path = "/api/public/v3/admin/user", hasBody = true)
//    @DELETE("/api/public/v3/admin/user")
    fun deleteUser(@Body deleteUserBody: DeleteUserBody): Observable<ObjectV3Wrapper<IdResponse>>

    //https://docs.uiza.io/#update-password
    @PUT("/api/public/v3/admin/user/changepassword")
    fun updatePassword(@Body updatePassword: UpdatePasswordBody): Observable<ObjectV3Wrapper<IdResponse>>

    // METADATA
    //http://docs.uizadev.io/#get-list-metadata
    @GET("/api/public/v3/media/metadata")
    fun getMetadatas(@Query("limit") limit: Int = 10, @Query("page") page: Int = 0): Observable<ListV3Wrapper<MetadataDetail>>

    //http://dev-docs.uizadev.io/#create-metadata
    @POST("/api/public/v3/media/metadata")
    fun createMetadata(@Body createMetadata: CreateMetadataBody): Observable<ObjectV3Wrapper<IdResponse>>

    //http://dev-docs.uizadev.io/#get-detail-of-metadata
    @GET("/api/public/v3/media/metadata")
    fun getDetailOfMetadata(@Query("id") id: String): Observable<ObjectV3Wrapper<MetadataDetail>>

    //http://dev-docs.uizadev.io/#update-metadata
    @PUT("/api/public/v3/media/metadata")
    fun updateMetadata(@Body createMetadata: CreateMetadataBody): Observable<ObjectV3Wrapper<MetadataDetail>>


    //http://dev-docs.uizadev.io/#delete-an-metadata
    @DELETE("/api/public/v3/media/metadata")
    fun deleteMetadata(@Query("id") id: String): Observable<ObjectV3Wrapper<IdResponse>>


    //http://dev-docs.uizadev.io/#list-all-entity
    @GET("/api/public/v3/media/entity")
    fun getEntities(): Observable<ListV3Wrapper<LiveV3Entity>>

    //http://dev-docs.uizadev.io/#list-all-entity
    @GET("/api/public/v3/media/entity")
    fun getEntities(
        @Query("metadataId") metadataid: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("orderBy") orderBy: String?,
        @Query("orderType") orderType: String?,
        @Query("publishToCdn") publishToCdn: String?
    ): Observable<ListV3Wrapper<LiveV3Entity>>


    //http://dev-docs.uizadev.io/#list-all-entity
    @GET("/api/public/v3/media/entity")
    fun getEntities(
        @Query("metadataId") metadataid: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Observable<ListV3Wrapper<LiveV3Entity>>

    //http://dev-docs.uizadev.io/#list-all-entity
    @GET("/api/public/v3/media/entity")
    fun getEntities(@Query("metadataId") metadataid: String): Observable<ListV3Wrapper<LiveV3Entity>>

    //http://dev-docs.uizadev.io/#retrieve-an-entity
    @GET("/api/public/v3/media/entity")
    fun getEntity(@Query("id") id: String): Observable<ObjectV3Wrapper<LiveV3Entity>>

    //http://dev-docs.uizadev.io/#search-entity
    @GET("/api/public/v3/media/entity/search")
    fun searchEntities(@Query("keyword") keyword: String): Observable<ListV3Wrapper<LiveV3Entity>>

    @POST("/api/public/v3/media/entity/playback/token")
    fun getTokenStreaming(@Body tokenStreamBody: TokenStreamBody): Observable<ObjectV3Wrapper<DataToken>>
}