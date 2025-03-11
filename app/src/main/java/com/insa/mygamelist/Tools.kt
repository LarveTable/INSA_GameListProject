package com.insa.mygamelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.data.Logo

/*IGDB search functions*/

fun checkQueryMatch(game : Games, igdb : IGDB, searchQuery : String) : Boolean {
    return game.name.contains(
        searchQuery,
        ignoreCase = true
    ) or getGenresString(game.genres, igdb).contains(
        searchQuery,
        ignoreCase = true
    ) or getPlatformsString(game.platforms, igdb).contains(
        searchQuery,
        ignoreCase = true
    )
}

fun getPlatformsString(platforms : List<Long>, igdb : IGDB) : String {
    var res = ""
    platforms.forEach {
        val platform = it
        val foundPlatform = igdb.platforms.find {
            it.id == platform
        }
        foundPlatform?.let {
            res += foundPlatform.name + " "
        }
    }
    return res
}

fun getGenresString(g : List<Long>, igdb : IGDB) : String {
    var res : Array<String> = emptyArray()
    g.forEach {
        val searchingId = it
        val genreFound = igdb.genres.find {
            it.id == searchingId
        }
        genreFound?.let {
            res += genreFound.name
        }
    }
    return res.joinToString()
}

val getPossibleCover: (List<Cover>, Long) -> (Cover?) = {
        liste, id ->
    liste.find { co ->
        co.id == id
    }
}

val getPossibleLogo: (List<Logo>, Long) -> (Logo?) = {
        liste, id ->
    liste.find { l ->
        l.id == id
    }
}

/*Modifier extension functions*/

fun Modifier.getCoverModifier() : Modifier {
    val coverModifier = Modifier
        .padding(4.dp)
        .height(100.dp)
        .width(80.dp)
        .clip(RoundedCornerShape(8.dp))
        .border(
            BorderStroke(4.dp, Brush.verticalGradient(listOf(Color.Magenta, Color.Blue))),
            RoundedCornerShape(8.dp)
        )

    return then(coverModifier)
}

fun Modifier.getBoxModifier(): Modifier {
    val boxModifier = Modifier
        .padding(top = 4.dp, end = 4.dp, start = 4.dp)
        .height(100.dp)
        .border(
            BorderStroke(1.dp, Color.Black),
            RoundedCornerShape(8.dp),
        )
        .padding(4.dp)
        .fillMaxWidth()

    return then(boxModifier)
}

/* Visuals */

@Composable
fun MissingCover(context : String) {
    if (context == "cells") {
        Image(
            painter = painterResource(R.drawable.missing),
            contentDescription = "Not found",
            modifier = Modifier.getCoverModifier()
        )
    }
    else if (context == "page") {
        Image(
            painter = painterResource(R.drawable.missing),
            contentDescription = "Not found",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GetCover(foundCover: Cover, context : String) {
    if (context == "cells") {
        AsyncImage(
            model = "https:" + foundCover.url, /* Add "https:" since it's not present in the JSON file */
            contentDescription = null,
            modifier = Modifier.getCoverModifier()
        )
    }
    else if (context == "page") {
        AsyncImage(
            model = "https:" + foundCover.url, /* Add "https:" since it's not present in the JSON file */
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
