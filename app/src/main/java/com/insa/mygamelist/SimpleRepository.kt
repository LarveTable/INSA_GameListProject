package com.insa.mygamelist

import androidx.compose.runtime.mutableStateListOf
import com.insa.mygamelist.data.IGDB

class SimpleRepository(val igdb : IGDB) {
    val favorites = mutableStateListOf<Long>()
}