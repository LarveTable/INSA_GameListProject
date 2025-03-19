package com.insa.mygamelist.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R
import kotlinx.serialization.Serializable

object IGDB {

    lateinit var covers: List<Cover>
    lateinit var games: List<Games>
    lateinit var genres: List<Genre>
    lateinit var logos: List<Logo>
    lateinit var platforms: List<Platform>

    fun load(context: Context) {
        val coversFromJson: List<Cover> = Gson().fromJson(
            context.resources.openRawResource(R.raw.covers).bufferedReader(),
            object : TypeToken<List<Cover>>() {}.type
        )
        val gamesFromJson: List<Games> = Gson().fromJson(
            context.resources.openRawResource(R.raw.games).bufferedReader(),
            object : TypeToken<List<Games>>() {}.type
        )
        val genreFromJson: List<Genre> = Gson().fromJson(
            context.resources.openRawResource(R.raw.genres).bufferedReader(),
            object : TypeToken<List<Genre>>() {}.type
        )
        val logoFromJson: List<Logo> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platform_logos).bufferedReader(),
            object : TypeToken<List<Logo>>() {}.type
        )
        val platformFromJson: List<Platform> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platforms).bufferedReader(),
            object : TypeToken<List<Platform>>() {}.type
        )

        covers = coversFromJson
        games = gamesFromJson
        genres = genreFromJson
        logos = logoFromJson
        platforms = platformFromJson
    }
}

@Serializable
data class Cover(val id: Long, val url: String)
@Serializable
data class Games(val id: Long,
                 val cover: Long,
                val first_release_date: Long = -1,
                val genres: List<Long>,
                val name: String,
                val platforms: List<Long>,
                val summary: String = "No summary available",
                val total_rating: Float = -1f)
@Serializable
data class Genre(val id: Long, val name: String)
@Serializable
data class Logo(val id: Long, val url: String)
@Serializable
data class Platform(val id: Long, val name: String, val platform_logo: Long)