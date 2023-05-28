package com.example.bookshelf.data

import com.example.bookshelf.network.BookService
import com.example.bookshelf.network.model.BookShelf
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

interface AppContainer {
    val booksRepository: BooksRepository
}

class DefaultAppContainer : AppContainer {
        private val BASE_URL = "https://www.googleapis.com/books/v1/"

        private val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        private val retrofitService: BookService by lazy { //вызов к Api с помощью ретрофит
            retrofit.create(BookService::class.java)
        }

        override val booksRepository: BooksRepository by lazy {
            NetworkBooksRepository(retrofitService)
        }
    }

