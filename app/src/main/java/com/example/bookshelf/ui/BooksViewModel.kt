package com.example.bookshelf.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BooksApplication
import com.example.bookshelf.data.Book
import com.example.bookshelf.data.BooksRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface BooksUiState {                                                                     // изолированый интерфейс
    data class Success(val bookSearch: List<Book>) : BooksUiState
    object Error : BooksUiState
    object Loading : BooksUiState
}


class BooksViewModel(
    private val booksRepository: BooksRepository

) : ViewModel() {

    var booksUiState: BooksUiState by mutableStateOf(BooksUiState.Loading)
        private set

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> =
        mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    init {
        getBooks("books")
    }

    fun getBooks(query: String = "book", maxResults: Int = 40) {                                    // сервер может вернуть максимум 40 элементов
        viewModelScope.launch {
            booksUiState = BooksUiState.Loading
            booksUiState = try {
                BooksUiState.Success(booksRepository.getBooks(query, maxResults))
            } catch (e: IOException) {                                                              // ошибка ввода-вывода
                BooksUiState.Error
            } catch (e: HttpException) {                                                            // ошибка сервера
                BooksUiState.Error
            }
        }
    }


    companion object {                                                                              // companion позволяет создать и пользоваться одним экземпляром объекта
        val Factory: ViewModelProvider.Factory = viewModelFactory {     // Андроид не позволяет ViewModel передавать значения в конструктор при создании
            initializer {
                val application = (this[APPLICATION_KEY] as BooksApplication)
                val booksRepository = application.container.booksRepository
                BooksViewModel(booksRepository = booksRepository)
            }
        }

    }
}

enum class SearchWidgetState {
    OPENED,
    CLOSED
}