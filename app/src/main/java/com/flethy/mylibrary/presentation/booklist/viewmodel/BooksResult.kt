package com.flethy.mylibrary.presentation.booklist.viewmodel

import com.flethy.mylibrary.model.booklist.entities.Book

sealed class BooksResult
class ValidResult(val result: List<Book>) : BooksResult()
object EmptyResult : BooksResult()
object EmptyQuery : BooksResult()
class ErrorResult(val e: Throwable) : BooksResult()