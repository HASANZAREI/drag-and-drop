package com.example.dragitem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var isCurrentlyDragging by mutableStateOf(false)
        private set

    var items by mutableStateOf(emptyList<PersonUiItem>())
        private set

    var addedPersons = mutableStateListOf<PersonUiItem>()
        private set

    init {
        items = listOf(
            PersonUiItem(name = "William", id = "1", backgroundColor = Color.Red),
            PersonUiItem(name = "Ashly", id = "2", backgroundColor = Color.Blue),
            PersonUiItem(name = "Peter", id = "3", backgroundColor = Color.Yellow)
        )
    }

    fun startDragging() {
        isCurrentlyDragging = true
    }
    fun stopDragging() {
        isCurrentlyDragging = false
    }

    fun addPerson(person: PersonUiItem) {
        addedPersons.add(person)
    }
}