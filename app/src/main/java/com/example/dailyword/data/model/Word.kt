package com.example.dailyword.data.model

data class Word(
    val word: String,
    val phonetic: String? = null,
    val meanings: List<Meaning> = emptyList(),
    val imageUrl: String? = null
)
