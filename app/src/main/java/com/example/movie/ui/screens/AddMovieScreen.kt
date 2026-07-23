package com.example.movie.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.movie.R
import com.example.movie.ui.MovieViewModel
import com.example.movie.ui.saveImageToInternalStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    viewModel: MovieViewModel,
    onMovieAdded: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    val suggestions by viewModel.tmdbSuggestions.collectAsState()
    val isSearching by viewModel.isSearchingTmdb.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            saveImageToInternalStorage(context, it)?.let { savedPath ->
                imageUrl = savedPath
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_add_movie)) },
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
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Khối Tìm kiếm thông minh
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it 
                        viewModel.searchTmdb(it) 
                    },
                    label = { Text(stringResource(R.string.label_movie_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    },
                    singleLine = true
                )

                AnimatedVisibility(visible = suggestions.isNotEmpty() || (title.length >= 2 && !isSearching && suggestions.isEmpty())) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (suggestions.isEmpty() && title.length >= 2 && !isSearching) {
                            Text(
                                "Không tìm thấy phim mẫu. Vui lòng kiểm tra kết nối mạng.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        } else {
                            Column {
                                suggestions.forEach { movieDto ->
                                    ListItem(
                                        headlineContent = { Text(movieDto.title, fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text(movieDto.releaseDate?.take(4) ?: "N/A") },
                                        leadingContent = {
                                            AsyncImage(
                                                model = "https://image.tmdb.org/t/p/w92${movieDto.posterPath}",
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp, 60.dp).clip(RoundedCornerShape(4.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        },
                                        modifier = Modifier.clickable {
                                            title = movieDto.title
                                            year = movieDto.releaseDate?.take(4) ?: ""
                                            imageUrl = if (movieDto.posterPath != null) "https://image.tmdb.org/t/p/w500${movieDto.posterPath}" else ""
                                            viewModel.clearSuggestions()
                                        }
                                    )
                                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text(stringResource(R.string.label_release_year_input)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // UI Nhập thời gian
            Column {
                Text(text = stringResource(R.string.label_last_watched), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimeInputUnit(value = hours, onValueChange = { if (it.length <= 2) hours = it }, label = "Giờ")
                    Text(":", fontWeight = FontWeight.Bold)
                    TimeInputUnit(value = minutes, onValueChange = { if (it.length <= 2 && (it.toIntOrNull() ?: 0) < 60) minutes = it }, label = "Phút")
                    Text(":", fontWeight = FontWeight.Bold)
                    TimeInputUnit(value = seconds, onValueChange = { if (it.length <= 2 && (it.toIntOrNull() ?: 0) < 60) seconds = it }, label = "Giây")
                }
            }

            // Phần Hình ảnh
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp, 180.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = androidx.compose.ui.res.painterResource(R.drawable.loading),
                            error = androidx.compose.ui.res.painterResource(R.drawable.loading)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_select_image))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.hint_image_url)) }, textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp))
                }
            }

            Column {
                Text(text = stringResource(R.string.label_rating, rating), fontWeight = FontWeight.Medium)
                Slider(value = rating.toFloat(), onValueChange = { rating = it.toInt() }, valueRange = 1f..5f, steps = 3)
            }

            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.label_notes)) }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Button(
                onClick = {
                    if (title.isNotBlank() && year.isNotBlank()) {
                        val finalTime = "${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}:${seconds.padStart(2, '0')}"
                        viewModel.addMovie(title, year, rating, notes, imageUrl, finalTime)
                        onMovieAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = title.isNotBlank() && year.isNotBlank()
            ) {
                Text(stringResource(R.string.btn_save_movie), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
