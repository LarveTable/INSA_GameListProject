package com.insa.mygamelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.insa.mygamelist.ui.theme.MyGamesListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //IGDB.load(this@MainActivity)

        enableEdgeToEdge()
        setContent {
            MyGamesListTheme {
                val repository = SimpleRepository()
                /* Déplacement de la logique dans un viewmodel, ici on gère uniquement l'affichage */
                val gameListviewModel = GameListViewModel(repository, this@MainActivity)
                val gamePageViewModel = GamePageViewModel(repository)
                val navController = rememberNavController()
                NavHost(navController, startDestination = GameListView) {
                    composable<GameListView> {
                        GameCells(MyController(navController), gameListviewModel)
                    }
                    composable<GamePageView> { backStackEntry ->
                        val game: GamePageView = backStackEntry.toRoute()
                        GamePage(MyController(navController), game.id, gamePageViewModel)
                    }
                }
            }
        }
    }
}
