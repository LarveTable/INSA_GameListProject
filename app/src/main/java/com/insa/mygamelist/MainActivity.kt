package com.insa.mygamelist

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.data.Logo
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
                val searchIcon = remember {
                    mutableStateOf(true)
                }
                val searchBar = remember {
                    mutableStateOf(false)
                }
                val query = remember {
                    mutableStateOf("")
                }
                Scaffold(topBar = {
                    TopAppBar(colors = topAppBarColors(
                        containerColor = Color.Magenta,
                        titleContentColor = Color.Black,
                    ), title = {
                        if (searchBar.value) {
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
                                    value = query.value,
                                    placeholder = { Text("Search") },
                                    onValueChange = { query.value = it }
                                )
                            }
                        }
                        else {
                            Text(text = title.value)
                        } },
                        navigationIcon = {
                            if (back.value) {
                                IconButton(onClick = backAction) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        },
                        actions = {
                            if (searchIcon.value)
                            IconButton(onClick = {
                                if (!searchBar.value) {
                                    searchBar.value = true
                                    title.value = ""
                                }
                                else {
                                    searchBar.value = false
                                    title.value = "My Games List"
                                }
                                    }) {
                                Icon(
                                    imageVector = Icons.Default.Search, /* Search icon */
                                    contentDescription = "Search bar"
                                )
                            }
                        }
                    )
                }, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Home) {
                        composable<Home> {
                            title.value = "My Games List"
                            back.value = false
                            searchIcon.value = true
                            if (query.value != "") {
                                searchBar.value = true
                            }
                            DisplayAllGameCells(innerPadding, IGDB, onNavigateToGamePage = { id : Long, name : String ->
                                navController.navigate(GamePage(id, name)) /* On passe une fonction à la page principale pour naviguer sur les pages des jeux */
                            }, query)
                        }
                        composable<GamePage> { backStackEntry ->
                            val game : GamePage = backStackEntry.toRoute()
                            title.value = game.name
                            back.value = true
                            searchIcon.value = false
                            searchBar.value = false
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

    val getPossibleLogo: (List<Logo>, Long) -> (Logo?) = {
            liste, id ->
        liste.find { l ->
            l.id == id
        }
    }

    @Composable
    fun DisplayGamePage(id : Long, innerPadding: PaddingValues, igdb: IGDB) : Unit {
        val game : Games? = igdb.games.find {
            it.id == id
        }
        game?.let {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column (modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()) {
                    Text(text=game.name, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, modifier = Modifier.fillMaxWidth(), fontSize = 30.sp, textAlign = TextAlign.Center) /* Text align allows the text itself to be centered and not just the TEXT component */
                    val foundCover: Cover? = getPossibleCover(igdb.covers, game.cover)
                    foundCover?.let { /* If a cover is found, display it */
                        AsyncImage(
                            model = "https:" + foundCover.url, /* Add "https:" since it's not present in the JSON file */
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .size(250.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } ?: run { /* Display "Missing" if cover not found (for fun) */
                        Image(
                            painter = painterResource(R.drawable.missing),
                            contentDescription = "Not found",
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .size(250.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    val genres : String = getGenresString(game.genres, igdb)
                    Text(text=genres, modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally), color = Color.Gray, fontStyle = FontStyle.Italic, fontSize = 15.sp)
                    LazyRow (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        items(game.platforms) { platform ->
                            val foundPlatform = igdb.platforms.find {
                                it.id == platform
                            }
                            foundPlatform?.let {
                                val foundLogo: Logo? = getPossibleLogo(igdb.logos, it.platform_logo)
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
                    }
                    LazyColumn (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        item {
                            Text(text=game.summary, modifier = Modifier.padding(10.dp), fontSize = 20.sp)
                        }
                    }
                }
            }
        }?: run {
            Text("Game not found", modifier = Modifier.padding(innerPadding))
        }
    }

    @Composable
    fun DisplayAllGameCells(innerPadding: PaddingValues, igdb: IGDB, onNavigateToGamePage: (Long, String) -> Unit, query : MutableState<String>) : Unit {
        val coverModifier = Modifier.getCoverModifier()
        val boxModifier = Modifier.getBoxModifier()
        if (igdb.games.all { game -> /*Dégeu à modif*/
                !game.name.contains(
                    query.value,
                    ignoreCase = true
                ) and !getGenresString(game.genres, igdb).contains(
                    query.value,
                    ignoreCase = true
                ) and !getPlatformsString(game.platforms, igdb).contains(
                    query.value,
                    ignoreCase = true
                )
            }) {
            Box(modifier = Modifier
                .fillMaxSize()) {
                Text(text="No match ;(", color = Color.LightGray, modifier = Modifier.align(Alignment.Center), fontSize = 30.sp)
            }
        }
        else {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(igdb.games) { game ->
                    if (game.name.contains(
                            query.value,
                            ignoreCase = true
                        ) or getGenresString(game.genres, igdb).contains(
                            query.value,
                            ignoreCase = true
                        ) or getPlatformsString(game.platforms, igdb).contains(
                            query.value,
                            ignoreCase = true
                        )
                    ) {
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
                                    contentDescription = "Not found",
                                    modifier = coverModifier
                                )
                            }
                            Box(
                                modifier = boxModifier.clickable(onClick = {
                                    onNavigateToGamePage(
                                        game.id,
                                        game.name
                                    )
                                })
                            ) {
                                Column(modifier = Modifier.align(Alignment.CenterStart)) {
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

    fun getPlatformsString(platforms : List<Long>, igdb : IGDB) : String {
        var res : String = ""
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