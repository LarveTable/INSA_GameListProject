package com.insa.mygamelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IGDB.load(this)

        enableEdgeToEdge()
        setContent {
            MyGamesListTheme {
                Scaffold(topBar = {
                    TopAppBar(colors = topAppBarColors(
                        containerColor = Color.Magenta,
                        titleContentColor = Color.Black,
                    ), title = { Text("My Games List") })
                }, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DisplayAllGameCells(innerPadding, IGDB)
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
    fun DisplayAllGameCells(innerPadding: PaddingValues, igdb: IGDB) : Unit {
        val coverModifier = getCoverModifier()
        val boxModifier = getBoxModifier()
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
                            modifier = boxModifier
                        ) {
                            Column (modifier = Modifier.align(Alignment.CenterStart)) {
                                Text(
                                    text = game.name,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Text(
                                    text = "Genres : "+game.genres.toString(),
                                )
                            }
                        }
                        /*Text(text = game.name)
                        Text(text= "Genres : ")*/
                    }
            }
        }
    }

    @Composable
    fun getCoverModifier() : Modifier {
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

        return coverModifier
    }

    fun getBoxModifier(): Modifier {
        val boxModifier = Modifier
            .padding(top=4.dp, end=4.dp)
            .height(100.dp)
            .background(Color.LightGray)
            .fillMaxWidth()

        return boxModifier
    }
}