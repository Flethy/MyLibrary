package com.flethy.mylibrary.presentation.booklist.viewmodel

sealed class SearchState
object Loading : SearchState()
object Ready : SearchState()