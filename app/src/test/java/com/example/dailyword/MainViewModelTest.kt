package com.example.dailyword

import com.example.dailyword.data.model.Definition
import com.example.dailyword.data.model.Meaning
import com.example.dailyword.data.model.Word
import com.example.dailyword.data.repository.WordRepository
import com.example.dailyword.viewmodel.MainViewModel
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var mockRepository: FakeWordRepository

    @Before
    fun setUp() {
        mockRepository = FakeWordRepository()
        viewModel = MainViewModel(mockRepository)
    }

    @Test
    fun canRefresh_freeUserDefaultIsTrue() {
        assertTrue(viewModel.canRefresh(isPremium = false))
    }

    @Test
    fun canRefresh_premiumUserIsAlwaysTrue() {
        assertTrue(viewModel.canRefresh(isPremium = true))
    }

    @Test
    fun canRefresh_easyLevelIsAlwaysTrueForFreeUser() {
        assertTrue(viewModel.canRefresh(isPremium = false, level = "easy"))
    }

    private class FakeWordRepository : WordRepository {
        override suspend fun getRandomWord(level: String, fullDefinition: Boolean): Result<Word> {
            return Result.success(
                Word("test", "/test/", listOf(Meaning("noun", listOf(Definition("test def", "test ex")))))
            )
        }
    }
}
