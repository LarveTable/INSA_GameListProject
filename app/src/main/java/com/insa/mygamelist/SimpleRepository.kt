package com.insa.mygamelist

import androidx.compose.runtime.mutableStateListOf
import com.insa.mygamelist.data.IGDB
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class SimpleRepository() {
    val favorites = mutableStateListOf<Long>()
    val igdb = IGDB

    suspend fun fetchGames(service: IGDBService) = coroutineScope {
        var requestBody = ("fields id, cover, first_release_date, genres, name, " +
                "platforms, summary, total_rating; where cover != null & platforms != null & " +
                "genres != null & name != null; limit 50;").toRequestBody("application/json".toMediaType())
        igdb.games = service.getGames(requestBody)

        // Extract found cover IDs
        val coverIds = igdb.games.map { it.cover }.joinToString(",")
        // Extract found platforms IDs
        val platformIds = igdb.games.flatMap { it.platforms }.joinToString(",")
        // Extract found genre IDs
        val genreIds = igdb.games.flatMap { it.genres }.joinToString(",")

        requestBody = ("fields id, name; where id = ($genreIds); limit 50;").toRequestBody("application/json".toMediaType())
        igdb.genres = service.getGenres(requestBody)

        requestBody = ("fields id, name, platform_logo; where id = ($platformIds) & platform_logo != null; limit 50;").toRequestBody("application/json".toMediaType())
        igdb.platforms = service.getPlatforms(requestBody)

        // Extract found platform logo IDs
        val platformLogoIds = igdb.platforms.map { it.platform_logo }.joinToString(",")

        requestBody = ("fields id, url; where id = ($platformLogoIds) & url != null; limit 50;").toRequestBody("application/json".toMediaType())
        igdb.logos = service.getLogos(requestBody)

        requestBody = ("fields id, url; where id = ($coverIds) & url != null; limit 50;").toRequestBody("application/json".toMediaType())
        igdb.covers = service.getCovers(requestBody)
    }
}