package com.example.dailyword.data.model

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition> = emptyList()
)
