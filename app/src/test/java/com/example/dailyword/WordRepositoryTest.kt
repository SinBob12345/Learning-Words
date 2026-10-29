package com.example.dailyword

import com.example.dailyword.data.api.DictionaryApi
import com.example.dailyword.data.model.Definition
import com.example.dailyword.data.model.Meaning
import com.example.dailyword.data.model.Word
import com.example.dailyword.data.repository.WordRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WordRepositoryTest {

    private lateinit var repository: WordRepositoryImpl
    private lateinit var fakeApi: FakeDictionaryApi

    @Before
    fun setUp() {
        fakeApi = FakeDictionaryApi()
        repository = WordRepositoryImpl(fakeApi)
    }

    @Test
    fun getRandomWord_freeTierFiltersDefinitionsAndExamples() = runBlocking {
        val result = repository.getRandomWord("easy", fullDefinition = false)
        assertTrue(result.isSuccess)
        val word = result.getOrNull()!!

        // Free tier: Each meaning should have at most 1 definition, and example should be null
        word.meanings.forEach { meaning ->
            assertTrue(meaning.definitions.size <= 1)
            meaning.definitions.forEach { definition ->
                assertNull(definition.example)
            }
        }
    }

    @Test
    fun getRandomWord_premiumTierProvidesFullDefinitions() = runBlocking {
        val result = repository.getRandomWord("easy", fullDefinition = true)
        assertTrue(result.isSuccess)
        val word = result.getOrNull()!!

        assertTrue(word.meanings.isNotEmpty())
    }

    @Test
    fun getRandomWord_cyclesSequentiallyThroughWordList() = runBlocking {
        val word1 = repository.getRandomWord("easy", fullDefinition = true).getOrThrow()
        val word2 = repository.getRandomWord("easy", fullDefinition = true).getOrThrow()
        val word3 = repository.getRandomWord("easy", fullDefinition = true).getOrThrow()

        Assert.assertEquals("apple", word1.word)
        Assert.assertEquals("happy", word2.word)
        Assert.assertEquals("water", word3.word)
    }

    private class FakeDictionaryApi : DictionaryApi {
        override suspend fun getWord(word: String): List<Word> {
            return listOf(
                Word(
                    word = word,
                    phonetic = "/$word/",
                    meanings = listOf(
                        Meaning(
                            partOfSpeech = "noun",
                            definitions = listOf(
                                Definition("Def 1", "Example 1"),
                                Definition("Def 2", "Example 2")
                            )
                        )
                    )
                )
            )
        }
    }
}
