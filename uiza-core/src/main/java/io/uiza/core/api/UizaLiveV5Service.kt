package io.uiza.core.api

import io.reactivex.Observable
import io.uiza.core.models.*
import retrofit2.http.*


interface UizaLiveV5Service {

    @GET("/api/v5/live/entities/{id}")
    fun getEntity(@Path("id") id: String): Observable<UizaLiveEntity>

    @GET("/api/v5/live/entities")
    fun getEntities(): Observable<ListEntityResponse>

    @POST("/api/v5/live/entities")
    fun createEntity(@Body createEntity: CreateEntityBody): Observable<UizaLiveEntity>

    @PUT("/api/v5/live/entities/{id}")
    fun updateEntity(@Path("id") id: String, @Query("name") name: String): Observable<UizaLiveEntity>

    @DELETE("/api/v5/live/entities/{id}")
    fun deleteEntity(@Path("id") id: String): Observable<DeleteEntityResponse>

    @PUT("/api/v5/live/entities/{id}/stream-key")
    fun resetStreamKey(@Path("id") id: String): Observable<UizaLiveEntity>

    @GET("/api/v5/live/entities/{entity_id}/sessions/{id}")
    fun getSession(@Path("entity_id") entityId: String, @Path("id") id: String): Observable<UizaLiveSession>

    @GET("/api/v5/live/entities/{entity_id}/sessions")
    fun getSessions(@Path("entity_id") entityId: String, @Query("page_size") pageSize: Int): Observable<ListSessionResponse>
}