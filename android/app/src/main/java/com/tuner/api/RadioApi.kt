package com.tuner.api

import com.tuner.model.Station
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioApi {
    @GET("stations/topvote/80")
    suspend fun getTopStations(): List<Station>

    @GET("stations/search")
    suspend fun advancedSearch(
        @Query("countrycode") countrycode: String? = null,
        @Query("language") language: String? = null,
        @Query("hidebroken") hidebroken: Boolean = true,
        @Query("limit") limit: Int = 80,
        @Query("order") order: String = "clickcount",
        @Query("reverse") reverse: Boolean = true
    ): List<Station>

    @GET("stations/byname/{name}")
    suspend fun searchStations(
        @Path("name") name: String,
        @Query("hidebroken") hidebroken: Boolean = true,
        @Query("limit") limit: Int = 80,
        @Query("order") order: String = "clickcount",
        @Query("reverse") reverse: Boolean = true
    ): List<Station>

    @GET("stations/bytagexact/{tag}")
    suspend fun getByTag(
        @Path("tag") tag: String,
        @Query("hidebroken") hidebroken: Boolean = true,
        @Query("limit") limit: Int = 80,
        @Query("order") order: String = "clickcount",
        @Query("reverse") reverse: Boolean = true
    ): List<Station>

    @GET("stations/bycountrycodeexact/{code}")
    suspend fun getByCountry(
        @Path("code") countryCode: String,
        @Query("hidebroken") hidebroken: Boolean = true,
        @Query("limit") limit: Int = 60,
        @Query("order") order: String = "clickcount",
        @Query("reverse") reverse: Boolean = true
    ): List<Station>
}
