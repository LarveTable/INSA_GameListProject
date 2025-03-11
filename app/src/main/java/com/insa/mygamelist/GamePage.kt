package com.insa.mygamelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.Logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePage(controller: MyController, game: Long, viewModel: GamePageViewModel) {
    val g : Games? = viewModel.repository.igdb.games.find {
        it.id == game
    }
    g?.let {
        val name = g.name
        Scaffold(topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color.Magenta,
                titleContentColor = Color.Black,
            ), title = {
                Text(text = name)
            },
                navigationIcon = {
                    IconButton(onClick = { controller.navigateToGameList() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                }
            )
        }, modifier = Modifier.fillMaxSize()) { innerPadding ->
            GamePageContent(innerPadding, viewModel, g)
        }
    } ?: run {
        controller.navigateToGameList()
    }
}

@Composable
fun GamePageContent(innerPadding: PaddingValues, viewModel: GamePageViewModel, game: Games) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        Column (modifier = Modifier
            .padding(10.dp)
            .align(Alignment.TopCenter)
            .fillMaxWidth()) {
            Box(modifier=Modifier.fillMaxWidth()) {
                Row (modifier = Modifier.align(Alignment.Center)) {
                    /*Add favorite star icon*/
                    Text(
                        text = game.name,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    ) /* Text align allows the text itself to be centered and not just the TEXT component */
                    Box(
                        modifier = Modifier
                            .clickable(onClick = {
                                if (viewModel.repository.favorites.contains(game.id)) {
                                    viewModel.repository.favorites.remove(game.id)
                                } else {
                                    viewModel.repository.favorites.add(game.id)
                                }
                            })
                            .padding(6.dp)
                    ) {
                        FavoriteIconPage(viewModel, game)
                    }
                }
            }
            val foundCover: Cover? = getPossibleCover(viewModel.repository.igdb.covers, game.cover)
            Box(modifier = Modifier
                .padding(top = 30.dp)
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
            ) {
                foundCover?.let { /* If a cover is found, display it */
                    GetCover(foundCover, "page")
                } ?: run { /* Display "Missing" if cover not found (for fun) */
                    MissingCover("page")
                }
            }
            val genres : String = getGenresString(game.genres, viewModel.repository.igdb)
            Text(text=genres, modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally), color = Color.Gray, fontStyle = FontStyle.Italic, fontSize = 15.sp)
            LazyRow (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                items(game.platforms) { platform ->
                    DisplayPlatforms(platform, viewModel)
                }
            }
            LazyColumn (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                item {
                    Text(text=game.summary, modifier = Modifier.padding(10.dp), fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun DisplayPlatforms(platform: Long, viewModel: GamePageViewModel) {
    val foundPlatform = viewModel.repository.igdb.platforms.find {
        it.id == platform
    }
    foundPlatform?.let {
        val foundLogo: Logo? = getPossibleLogo(viewModel.repository.igdb.logos, it.platform_logo)
        foundLogo?.let {
            AsyncImage(
                model = "https:" + foundLogo.url,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .size(80.dp)
            )
        }
    }
}

@Composable
fun FavoriteIconPage(viewModel: GamePageViewModel, game: Games) {
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