package com.insa.mygamelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    @Serializable
    object Home
    // Define a profile route that takes a game id as a parameter
    @Serializable
    data class GamePage(val id: Long, val name: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IGDB.load(this)

        enableEdgeToEdge()
        setContent {
            MyGamesListTheme {
                val title = remember { /* Title change, using remember to allow for redraw */
                    mutableStateOf("My Games List")
                }
                val back = remember { /* To display or not to display the back button */
                    mutableStateOf(false)
                }
                var backAction = remember { /* Action to perform */
                    {}
                }
                Scaffold(topBar = {
                    TopAppBar(colors = topAppBarColors(
                        containerColor = Color.Magenta,
                        titleContentColor = Color.Black,
                    ), title = { Text(text=title.value) },
                        navigationIcon = {
                            if (back.value) {
                                IconButton(onClick = backAction) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        }
                    )
                }, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Home) {
                        composable<Home> {
                            title.value = "My Games List"
                            back.value = false
                            DisplayAllGameCells(innerPadding, IGDB, onNavigateToGamePage = { id : Long, name : String ->
                                navController.navigate(GamePage(id, name)) /* On passe une fonction à la page principale pour naviguer sur les pages des jeux */
                            } )
                        }
                        composable<GamePage> { backStackEntry ->
                            val game : GamePage = backStackEntry.toRoute()
                            title.value = game.name
                            back.value = true
                            backAction = {
                                navController.popBackStack()
                            }
                            DisplayGamePage(game.id, innerPadding, IGDB)
                        }
                    }
                }
            }
        }
    }

    val getPossibleCover: (List<Cover>, Long) -> (Cover?) = {
        liste, id ->
            liste.find { co ->
                co.id == id
            }
    }

    @Composable
    fun DisplayGamePage(id : Long, innerPadding: PaddingValues, igdb: IGDB) : Unit {
        Text("Game page for game $id", modifier = Modifier.padding(innerPadding))
    }

    @Composable
    fun DisplayAllGameCells(innerPadding: PaddingValues, igdb: IGDB, onNavigateToGamePage: (Long, String) -> Unit ) : Unit {
        val coverModifier = Modifier.getCoverModifier()
        val boxModifier = Modifier.getBoxModifier()
        LazyColumn (modifier=Modifier.padding(innerPadding)) {
            items(igdb.games) { game ->
                val foundCover: Cover? = getPossibleCover(igdb.covers, game.cover)
                    Row {
                        foundCover?.let { /* If a cover is found, display it */
                            AsyncImage(
                                model = "https:" + foundCover.url, /* Add "https:" since it's not present in the JSON file */
                                contentDescription = null,
                                modifier = coverModifier
                            )
                        } ?: run { /* Display "Missing" if cover not found (for fun) */
                            Image(
                                painter = painterResource(R.drawable.missing),
                                contentDescription = "Contact profile picture",
                                modifier = coverModifier
                            )
                        }
                        Box(
                            modifier = boxModifier.clickable(onClick = { onNavigateToGamePage(game.id, game.name) })
                        ) {
                            Column (modifier = Modifier.align(Alignment.CenterStart)) {
                                Text(
                                    text = game.name,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Text(
                                    text = "Genres : "+getGenresString(game.genres, igdb),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
            }
        }
    }

    fun getGenresString(g : List<Long>, igdb : IGDB) : String {
        var res : Array<String> = emptyArray()
        g.forEach {
            val id = it
            val genreFound = igdb.genres.find {
                it.id == id
            }
            genreFound?.let {
                res += genreFound.name
            }

        }
        return res.joinToString()
    }

    @Composable /* Need to be there for the remember function */
    fun Modifier.getCoverModifier() : Modifier {
        val rainbowColorsBrush = remember { /* Colorful border for images. For cosmetic purposes only ! */
            Brush.sweepGradient(
                listOf(
                    Color(0xFF9575CD),
                    Color(0xFFBA68C8),
                    Color(0xFFE57373),
                    Color(0xFFFFB74D),
                    Color(0xFFFFF176),
                    Color(0xFFAED581),
                    Color(0xFF4DD0E1),
                    Color(0xFF9575CD)
                )
            )
        }

        val coverModifier = Modifier
            .padding(4.dp)
            .height(100.dp)
            .width(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                BorderStroke(4.dp, rainbowColorsBrush),
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
}