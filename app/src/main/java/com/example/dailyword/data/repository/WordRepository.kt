package com.example.dailyword.data.repository

import com.example.dailyword.data.model.Word

interface WordRepository {
    suspend fun getRandomWord(level: String, fullDefinition: Boolean = true): Result<Word>
}
