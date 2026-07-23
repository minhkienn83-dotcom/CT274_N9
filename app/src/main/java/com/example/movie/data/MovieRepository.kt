package com.example.movie.data

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    fun getMovieById(id: Int): Flow<Movie?> = movieDao.getMovieById(id)

    suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie)

    suspend fun deleteMovie(movie: Movie) = movieDao.deleteMovie(movie)

    suspend fun updateMovie(movie: Movie) = movieDao.insertMovie(movie)

    suspend fun deleteAllMovies() = movieDao.deleteAllMovies()
}
