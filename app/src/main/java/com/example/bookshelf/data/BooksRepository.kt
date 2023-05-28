package com.example.bookshelf.data

import com.example.bookshelf.network.BookService

interface BooksRepository {
    suspend fun getBooks(query: String, maxResults: Int) : List<Book>
}

class NetworkBooksRepository (                               //принимает вызов к сереверу и реализует интерфейс BooksRepository
    private val bookService: BookService
    ) : BooksRepository {
    override suspend fun getBooks(
        query: String,
        maxResults: Int
    ): List<Book> = bookService.bookSearch(query, maxResults).items.map { items ->            // трансормация BookShelf в объекты Book
        Book(
            title = items.volumeInfo?.title,
            previewLink = items.volumeInfo?.previewLink,
            imageLink = items.volumeInfo?.imageLinks?.thumbnail
        )
    }

}