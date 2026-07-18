package com.example.movie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.movie.R
import com.example.movie.data.Movie
import com.example.movie.ui.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onAddMovieClick: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    MovieContent(
        movies = movies,
        onAddMovieClick = onAddMovieClick,
        onMovieClick = onMovieClick,
        onDeleteMovie = { viewModel.deleteMovie(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieContent(
    movies: List<Movie>,
    onAddMovieClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onDeleteMovie: (Movie) -> Unit
) {
    Scaffold(
        topBar = {
            // Sử dụng TopAppBar thông thường để căn trái logo
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Film",
                        modifier = Modifier
                            .height(100.dp) 
                            .wrapContentWidth()
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart 
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.height(120.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMovieClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_movie), tint = Color.White)
            }
        }
    ) { innerPadding ->
        if (movies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_movie_list), color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movies) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovieClick(movie.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = movie.imageUrl,
                                contentDescription = stringResource(R.string.desc_movie_poster),
                                modifier = Modifier
                                    .size(80.dp, 120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                                // Hiển thị ảnh mặc định khi đang tải hoặc lỗi
                                placeholder = painterResource(R.drawable.loading),
                                error = painterResource(R.drawable.loading)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = movie.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.label_release_year, movie.year),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StarRating(rating = movie.rating)
                            }
                            IconButton(onClick = { onDeleteMovie(movie) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.desc_delete_movie), tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieListPreview() {
    val sampleMovies = remember {
        mutableStateListOf(
            Movie(1, "Phim Mẫu 1", "2024", 5, "Rất hay", ""),
            Movie(2, "Phim Mẫu 2", "2023", 4, "Bình thường", "")
        )
    }

    com.example.movie.ui.theme.MovieTheme {
        MovieContent(
            movies = sampleMovies,
            onAddMovieClick = {},
            onMovieClick = {},
            onDeleteMovie = { movie ->
                sampleMovies.remove(movie)
            }
        )
    }
}
