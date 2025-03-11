package com.insa.mygamelist

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
object GameListView

@Serializable
data class GamePageView(val id: Long)

class MyController(private val navController: NavController) {

    fun navigateToGameList() {
        navController.navigate(GameListView)
    }

    fun navigateToGamePage(id: Long) {
        navController.navigate(GamePageView(id))
    }
}