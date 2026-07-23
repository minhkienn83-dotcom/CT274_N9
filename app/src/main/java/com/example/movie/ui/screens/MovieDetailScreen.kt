package com.example.movie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // 1. Dialog cập nhật mốc thời gian (Đã có)
    if (showUpdateDialog) {
        val currentTimeParts = movie?.lastWatchedTime?.split(":") ?: listOf("", "", "")
        var h by remember { mutableStateOf(currentTimeParts.getOrNull(0) ?: "") }
        var m by remember { mutableStateOf(currentTimeParts.getOrNull(1) ?: "") }
        var s by remember { mutableStateOf(currentTimeParts.getOrNull(2) ?: "") }

        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Cập nhật mốc thời gian") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeInputUnit(value = h, onValueChange = { if (it.length <= 2) h = it }, label = "Giờ")
                    Text(":", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                    TimeInputUnit(value = m, onValueChange = { if (it.length <= 2 && (it.toIntOrNull() ?: 0) < 60) m = it }, label = "Phút")
                    Text(":", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                    TimeInputUnit(value = s, onValueChange = { if (it.length <= 2 && (it.toIntOrNull() ?: 0) < 60) s = it }, label = "Giây")
                }
            },
            confirmButton = {
                Button(onClick = {
                    val finalTime = "${h.padStart(2, '0')}:${m.padStart(2, '0')}:${s.padStart(2, '0')}"
                    viewModel.updateWatchedTime(movieId, finalTime)
                    showUpdateDialog = false
                }) { Text(stringResource(R.string.btn_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) { Text(stringResource(R.string.dialog_cancel)) }
            }
        )
    }

    // 2. Dialog chỉnh sửa đánh giá và ghi chú (MỚI)
    if (showEditDialog && movie != null) {
        var editRating by remember { mutableIntStateOf(movie!!.rating) }
        var editNotes by remember { mutableStateOf(movie!!.notes) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.title_edit_movie)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(text = "Đánh giá: $editRating Sao", fontWeight = FontWeight.Medium)
                        Slider(
                            value = editRating.toFloat(),
                            onValueChange = { editRating = it.toInt() },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                    }
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text(stringResource(R.string.label_notes)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateMovie(movie!!.copy(rating = editRating, notes = editNotes))
                    showEditDialog = false
                }) { Text(stringResource(R.string.btn_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text(stringResource(R.string.dialog_cancel)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_movie_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.desc_back))
                    }
                },
                actions = {
                    // Nút Chỉnh sửa (Mới)
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa phim")
                    }
                    // Nút Lịch sử
                    IconButton(onClick = { showUpdateDialog = true }) {
                        Icon(Icons.Default.History, contentDescription = "Cập nhật thời gian")
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
                
                Text(
                    text = stringResource(R.string.label_release_year, it.year),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
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
