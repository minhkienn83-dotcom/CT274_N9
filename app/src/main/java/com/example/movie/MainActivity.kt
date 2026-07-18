package com.example.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.movie.data.MovieDatabase
import com.example.movie.data.MovieRepository
import com.example.movie.ui.MovieViewModel
import com.example.movie.ui.screens.AddMovieScreen
import com.example.movie.ui.screens.MovieDetailScreen
import com.example.movie.ui.screens.MovieListScreen
import com.example.movie.ui.theme.MovieTheme
import kotlinx.serialization.Serializable

// Định nghĩa các đích đến (Routes) một cách an toàn
@Serializable object MovieListRoute
@Serializable data class MovieDetailRoute(val id: Int)
@Serializable object AddMovieRoute

class MainActivity : ComponentActivity() {
    private val movieViewModel: MovieViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = MovieDatabase.getDatabase(applicationContext)
                val repository = MovieRepository(database.movieDao())
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieTheme {
                AppNavigation(viewModel = movieViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MovieViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController, 
        startDestination = MovieListRoute,
        // Loại bỏ hiệu ứng chuyển màn hình (Chớp sáng/Trượt)
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<MovieListRoute> {
            MovieListScreen(
                viewModel = viewModel,
                onAddMovieClick = { navController.navigate(AddMovieRoute) },
                onMovieClick = { movieId -> navController.navigate(MovieDetailRoute(movieId)) }
            )
        }

        composable<MovieDetailRoute> { backStackEntry ->
            val route: MovieDetailRoute = backStackEntry.toRoute()
            MovieDetailScreen(
                movieId = route.id,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<AddMovieRoute> {
            AddMovieScreen(
                viewModel = viewModel,
                onMovieAdded = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
