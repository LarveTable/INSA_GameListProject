package com.insa.mygamelist

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameListViewModel(val repository: SimpleRepository) : ViewModel() {
    val defaultTitle = "My Game List"
    val searchBarToggle = mutableStateOf(false)
    val searchQuery = mutableStateOf("")
}