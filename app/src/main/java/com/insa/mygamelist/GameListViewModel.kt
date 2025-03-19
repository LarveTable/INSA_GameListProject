package com.insa.mygamelist

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GameListViewModel(val repository: SimpleRepository, context : Context) : ViewModel() {
    val defaultTitle = "My Game List"
    val searchBarToggle = mutableStateOf(false)
    val searchQuery = mutableStateOf("")
    val isLoading = mutableStateOf(true) // Loading state

    init {
        viewModelScope.launch {
            try {
                repository.fetchGames(IGDBClient.service)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching data: ${e.message}", e)
                repository.igdb.load(context)
            }
            isLoading.value = false // Loading done
        }
    }
}