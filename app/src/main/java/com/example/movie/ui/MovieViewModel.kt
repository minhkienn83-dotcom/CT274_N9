package com.example.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.data.Movie
import com.example.movie.data.MovieRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {
    
    val movies: StateFlow<List<Movie>> = repository.allMovies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMovie(title: String, year: String, rating: Int, notes: String, imageUrl: String, lastWatchedTime: String) {
        viewModelScope.launch {
            val newMovie = Movie(
                title = title, 
                year = year, 
                rating = rating, 
                notes = notes, 
                imageUrl = imageUrl,
                lastWatchedTime = lastWatchedTime
            )
            repository.insertMovie(newMovie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
        }
    }

    fun getMovieById(id: Int) = repository.getMovieById(id)
}
