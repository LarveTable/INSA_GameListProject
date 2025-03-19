package com.insa.mygamelist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCells(controller: MyController, viewModel: GameListViewModel) {
    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = Color.Magenta,
            titleContentColor = Color.Black,
        ), title = {
            if (viewModel.searchBarToggle.value) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                ) {
                    TextField(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(5.dp)
                            .clip(CircleShape)
                            .fillMaxWidth(),
                        value = viewModel.searchQuery.value,
                        placeholder = { Text("Search") },
                        onValueChange = { viewModel.searchQuery.value = it }
                    )
                }
            }
            else {
                Text(text = viewModel.defaultTitle)
            } },
            actions = {
                IconButton(onClick = {
                    viewModel.searchBarToggle.value = !viewModel.searchBarToggle.value
                }) {
                    Icon(
                        imageVector = Icons.Default.Search, /* Search icon */
                        contentDescription = "Search bar"
                    )
                }
            }
        )
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (viewModel.isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column{
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text(
                        text = "Fetching games...",
                        modifier = Modifier.padding(top = 20.dp).align(Alignment.CenterHorizontally),
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        else {
            GenerateCells(
                innerPadding,
                viewModel.repository.igdb,
                viewModel.searchQuery.value,
                controller,
                viewModel
            )
        }
    }
}

@Composable
fun GenerateCells(innerPadding : PaddingValues, igdb : IGDB, searchQuery : String, controller: MyController, viewModel: GameListViewModel) {
    /* If no game is found matching the query, display "No match ;(" */
    if (!matchOrNot(igdb, searchQuery)) {
        /* Centered Box item with text "No match ;(" italic grey */
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "No match ;(",
                color = Color.Gray,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 30.sp
            )
        }
    }
    else {
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            /* For each game found, scrape the cover (if it exists) and generate the cell */
            items(igdb.games) { game ->
                if (checkQueryMatch(game, igdb, searchQuery)) {
                    Log.d("zozo", igdb.covers.toString())
                    val foundCover: Cover? = getPossibleCover(igdb.covers, game.cover)
                    GenerateRow(game, igdb, controller, viewModel, foundCover)
                }
            }
        }
    }
}

@Composable
fun GenerateRow(game: Games, igdb: IGDB, controller: MyController, viewModel: GameListViewModel, foundCover: Cover?) {
    Row {
        foundCover?.let { /* If a cover is found, display it */
            GetCover(foundCover, "cells")
        } ?: run { /* Display "Missing" if cover not found (for fun) */
            MissingCover("cells")
        }
        Box(
            modifier = Modifier.getBoxModifier().clickable(onClick = {
                controller.navigateToGamePage(game.id)
            })
        ) {
            Box(modifier = Modifier.align(Alignment.CenterStart).padding(end=20.dp)) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    Column() {
                        Text(
                            text = game.name,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                        )
                        Text(
                            text = "Genres : " + getGenresString(game.genres, igdb),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            /*Add favorite star icon*/
            Box(modifier=Modifier
                .align(Alignment.CenterEnd)
                .padding(end=5.dp)
                .clickable(onClick = {
                    if (viewModel.repository.favorites.contains(game.id)){
                        viewModel.repository.favorites.remove(game.id)
                    }
                    else { viewModel.repository.favorites.add(game.id) }
                })
            ) {
                FavoriteIconCells(viewModel, game)
            }
        }
    }
}

@Composable
fun FavoriteIconCells(viewModel: GameListViewModel, game: Games) {
    if (viewModel.repository.favorites.contains(game.id)){
        Icon(
            imageVector = Icons.Default.Favorite, /* Search icon */
            contentDescription = "Favorite"
        )
    }
    else {
        Icon(
            imageVector = Icons.Default.FavoriteBorder, /* Search icon */
            contentDescription = "Favorite"
        )
    }
}

fun matchOrNot(igdb: IGDB, searchQuery: String) : Boolean {
    for (game in igdb.games) {
        if (checkQueryMatch(game, igdb, searchQuery)) {
            return true
        }
    }
    return false
}