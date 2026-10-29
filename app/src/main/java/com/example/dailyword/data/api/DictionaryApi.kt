package com.example.dailyword.data.api

import com.example.dailyword.data.model.Word
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWord(@Path("word") word: String): List<Word>
}
