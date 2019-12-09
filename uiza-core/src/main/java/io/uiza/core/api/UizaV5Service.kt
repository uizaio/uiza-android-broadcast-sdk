package io.uiza.core.api

import io.reactivex.Observable
import io.uiza.core.models.v5.*
import retrofit2.http.*

/**
 * Connect to Live API
 */
interface UizaV5Service {

    /**
     * Get an entity
     */
    @GET("/api/v5/live/entities/{id}")
    fun getEntity(@Path("id") id: String): Observable<LiveEntity>

    /**
     * Get list of entities
     */
    @GET("/api/v5/live/entities")
    fun getEntities(): Observable<ListWrapper<LiveEntity>>

    /**
     * Create an entity
     */
    @POST("/api/v5/live/entities")
    fun createEntity(@Body createEntity: CreateLiveEntityBody): Observable<LiveEntity>

    /**
     * Update an entity
     */
    @PUT("/api/v5/live/entities/{id}")
    fun updateEntity(@Path("id") id: String, @Query("name") name: String): Observable<LiveEntity>

    /**
     * Delete an entity
     */
    @DELETE("/api/v5/live/entities/{id}")
    fun deleteEntity(@Path("id") id: String): Observable<DeleteLiveEntityResponse>

    /**
     * Reset stream key of an entity
     */
    @PUT("/api/v5/live/entities/{id}/stream-key")
    fun resetStreamKey(@Path("id") id: String): Observable<LiveEntity>

    /**
     * Get an session
     */
    @GET("/api/v5/live/entities/{entity_id}/sessions/{id}")
    fun getSession(@Path("entity_id") entityId: String, @Path("id") id: String): Observable<LiveSession>

    /**
     * Get list of sessions
     */
    @GET("/api/v5/live/entities/{entity_id}/sessions")
    fun getSessions(@Path("entity_id") entityId: String, @Query("page_size") pageSize: Int): Observable<ListWrapper<LiveSession>>

}