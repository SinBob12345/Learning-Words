package com.example.dailyword.di

import com.example.dailyword.data.api.DictionaryApi
import com.example.dailyword.data.repository.WordRepository
import com.example.dailyword.data.repository.WordRepositoryImpl
import com.example.dailyword.viewmodel.MainViewModel
import com.example.dailyword.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModules = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<DictionaryApi> {
        get<Retrofit>().create(DictionaryApi::class.java)
    }

    single<WordRepository> {
        WordRepositoryImpl(get())
    }

    viewModel { MainViewModel(get()) }
    viewModel { SettingsViewModel() }
}
