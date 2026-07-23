package com.example.movie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movie.R
import com.example.movie.data.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    movies: List<Movie>,
    onBackClick: () -> Unit
) {
    // Logic tính toán thống kê
    val totalMovies = movies.size
    val averageRating = if (totalMovies > 0) movies.map { it.rating }.average() else 0.0
    
    // Đếm số phim theo từng mức sao (1 đến 5)
    val ratingCounts = (1..5).associateWith { star ->
        movies.count { it.rating == star }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_statistics)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Tổng quan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Đảm bảo Column chiếm hết chiều ngang Card
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Canh giữa theo chiều ngang
                    verticalArrangement = Arrangement.Center // Canh giữa theo chiều dọc
                ) {
                    Text(
                        text = "$totalMovies",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.label_total_movies),
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = String.format("%.1f ★", averageRating),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.label_average_rating),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Text(
                text = stringResource(R.string.label_rating_distribution),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Biểu đồ phân bổ đánh giá
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    (5 downTo 1).forEach { star ->
                        val count = ratingCounts[star] ?: 0
                        val progress = if (totalMovies > 0) count.toFloat() / totalMovies else 0f
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(R.string.label_stars, star), modifier = Modifier.width(60.dp))
                            
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .padding(horizontal = 8.dp),
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            
                            Text(text = "$count", fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                        }
                    }
                }
            }
        }
    }
}
