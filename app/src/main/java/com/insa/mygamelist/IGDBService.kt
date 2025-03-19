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
        "Client-ID: 86hqkdnkauut9o8j2ve4gpyl2f87y9",
        "Authorization: Bearer e1o60tnhrtu9l3eao5ocaysnvm1i7q"
    )
    @POST("games")
    suspend fun getGames(@Body body: RequestBody): List<Games>

    @Headers(
        "Client-ID: 86hqkdnkauut9o8j2ve4gpyl2f87y9",
        "Authorization: Bearer e1o60tnhrtu9l3eao5ocaysnvm1i7q"
    )
    @POST("genres")
    suspend fun getGenres(@Body body: RequestBody): List<Genre>

    @Headers(
        "Client-ID: 86hqkdnkauut9o8j2ve4gpyl2f87y9",
        "Authorization: Bearer e1o60tnhrtu9l3eao5ocaysnvm1i7q"
    )
    @POST("platforms")
    suspend fun getPlatforms(@Body body: RequestBody): List<Platform>

    @Headers(
        "Client-ID: 86hqkdnkauut9o8j2ve4gpyl2f87y9",
        "Authorization: Bearer e1o60tnhrtu9l3eao5ocaysnvm1i7q"
    )
    @POST("platform_logos")
    suspend fun getLogos(@Body body: RequestBody): List<Logo>

    @Headers(
        "Client-ID: 86hqkdnkauut9o8j2ve4gpyl2f87y9",
        "Authorization: Bearer e1o60tnhrtu9l3eao5ocaysnvm1i7q"
    )
    @POST("covers")
    suspend fun getCovers(@Body body: RequestBody): List<Cover>
}