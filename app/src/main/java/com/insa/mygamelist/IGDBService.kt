package com.insa.mygamelist

import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.Genre
import com.insa.mygamelist.data.Logo
import com.insa.mygamelist.data.Platform
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IGDBService {
    @Headers(
        "Client-ID: (client-id)",
        "Authorization: Bearer (token)"
    )
    @POST("games")
    suspend fun getGames(@Body body: RequestBody): List<Games>

    @Headers(
        "Client-ID: (client-id)",
        "Authorization: Bearer (token)"
    )
    @POST("genres")
    suspend fun getGenres(@Body body: RequestBody): List<Genre>

    @Headers(
        "Client-ID: (client-id)",
        "Authorization: Bearer (token)"
    )
    @POST("platforms")
    suspend fun getPlatforms(@Body body: RequestBody): List<Platform>

    @Headers(
        "Client-ID: (client-id)",
        "Authorization: Bearer (token)"
    )
    @POST("platform_logos")
    suspend fun getLogos(@Body body: RequestBody): List<Logo>

    @Headers(
        "Client-ID: (client-id)",
        "Authorization: Bearer (token)"
    )
    @POST("covers")
    suspend fun getCovers(@Body body: RequestBody): List<Cover>
}