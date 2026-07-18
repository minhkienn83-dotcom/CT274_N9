package com.example.movie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.movie.R
import com.example.movie.ui.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieViewModel,
    onBackClick: () -> Unit
) {
    val movie by viewModel.getMovieById(movieId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_movie_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.desc_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        movie?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = stringResource(R.string.desc_movie_poster),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = androidx.compose.ui.res.painterResource(R.drawable.loading),
                    error = androidx.compose.ui.res.painterResource(R.drawable.loading)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Hiển thị Năm phát hành
                Text(
                    text = stringResource(R.string.label_release_year, it.year),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Hiển thị Mốc thời gian đã xem (Xuống dòng)
                val timeDisplayText = if (it.lastWatchedTime == "00:00:00" || it.lastWatchedTime.isEmpty()) {
                    stringResource(R.string.no_last_watched)
                } else {
                    stringResource(R.string.label_last_watched_display, it.lastWatchedTime)
                }
                
                Text(
                    text = timeDisplayText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (it.lastWatchedTime == "00:00:00" || it.lastWatchedTime.isEmpty()) Color.Gray else Color(0xFFE91E63)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = stringResource(R.string.label_personal_rating), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                StarRating(rating = it.rating)

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.label_personal_notes), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = if (it.notes.isNotEmpty()) it.notes else stringResource(R.string.no_notes),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.error_movie_not_found))
        }
    }
}
