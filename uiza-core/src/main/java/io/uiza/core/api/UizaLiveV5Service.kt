package io.uiza.core.api

import io.reactivex.Observable
import io.uiza.core.models.CreateEntityBody
import io.uiza.core.models.UizaEntity
import io.uiza.core.parsers.ListEntityResponse
import okhttp3.Response
import retrofit2.http.*


interface UizaLiveV5Service {

    @GET("/api/v5/live/entities/{id}")
    fun getEntity(@Path("id") id: String): Observable<UizaEntity>

    @GET("/api/v5/live/entities")
    fun getEntities(): Observable<ListEntityResponse>

    @POST("/api/v5/live/entities")
    fun createEntity(@Body createEntity: CreateEntityBody): Observable<UizaEntity>

    @PUT("/api/v5/live/entities/{id}")
    fun updateEntity(@Path("id") id: String, @Query("name") name: String): Observable<Response>

    @DELETE("/api/v5/live/entities/{id}")
    fun deleteEntity(@Path("id") id: String): Observable<Response>

    @PUT("/api/v5/live/entities/{id}/stream-key")
    fun resetStreamKey(@Path("id") id: String): Observable<Response>
}