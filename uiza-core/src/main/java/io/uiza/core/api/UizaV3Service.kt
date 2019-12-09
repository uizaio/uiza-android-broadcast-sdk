package io.uiza.core.api

import io.reactivex.Observable
import io.uiza.core.models.v3.*
import retrofit2.http.*

interface UizaV3Service {
    //https://docs.uiza.io/#list-all-users
    @GET("/api/public/v3/admin/user")
    fun listAllUsers(): Observable<ListV3Wrapper<User>>

    //https://docs.uiza.io/#retrieve-an-user
    @GET("/api/public/v3/admin/user")
    fun retrieveAnUser(@Query("id") id: String): Observable<ObjectV3Wrapper<User>>

    //https://docs.uiza.io/#create-an-user
    @POST("/api/public/v3/admin/user")
    fun createAnUser(@Body createUserBody: RequestUserBody): Observable<ObjectV3Wrapper<IdResponse>>

    //https://docs.uiza.io/#update-an-user
    @PUT("/api/public/v3/admin/user")
    fun updateAnUser(@Body updateUser: RequestUserBody?): Observable<ObjectV3Wrapper<User>>

    //https://docs.uiza.io/#update-an-user
    @HTTP(method = "DELETE", path = "/api/public/v3/admin/user", hasBody = true)
//    @DELETE("/api/public/v3/admin/user")
    fun deleteAnUser(@Body deleteUserBody: DeleteUserBody): Observable<ObjectV3Wrapper<IdResponse>>

    //https://docs.uiza.io/#update-password
    @PUT("/api/public/v3/admin/user/changepassword")
    fun updatePassword(@Body updatePassword: UpdatePasswordBody): Observable<ObjectV3Wrapper<IdResponse>>

    // METADATA
    //http://docs.uizadev.io/#get-list-metadata
    @GET("/api/public/v3/media/metadata")
    fun getListMetadata(@Query("limit") limit: Int = 10, @Query("page") page: Int = 0): Observable<ListV3Wrapper<MetadataDetail>>

    //http://dev-docs.uizadev.io/#create-metadata
    @POST("/api/public/v3/media/metadata")
    fun createMetadata(@Body createMetadata: CreateMetadataBody): Observable<ObjectV3Wrapper<IdResponse>>

    //http://dev-docs.uizadev.io/#get-detail-of-metadata
    @GET("/api/public/v3/media/metadata")
    fun getDetailOfMetadata(@Query("id") id: String): Observable<ObjectV3Wrapper<MetadataDetail>>
}