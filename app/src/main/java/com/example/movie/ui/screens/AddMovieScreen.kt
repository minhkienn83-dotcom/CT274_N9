package com.example.movie.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    var rating by remember { mutableStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    
    // Trạng thái cho 3 ô nhập thời gian
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val savedPath = saveImageToInternalStorage(context, it)
                if (savedPath != null) {
                    imageUrl = savedPath
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_add_movie)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.desc_back))
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.label_movie_title)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text(stringResource(R.string.label_release_year_input)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // --- UI Nhập thời gian mới ---
            Column {
                Text(
                    text = stringResource(R.string.label_last_watched),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeInputUnit(value = hours, onValueChange = { if (it.length <= 2) hours = it }, label = "Giờ")
                    Text(":", fontWeight = FontWeight.Bold)
                    TimeInputUnit(value = minutes, onValueChange = { if (it.length <= 2 && (it.toIntOrNull() ?: 0) < 60) minutes = it }, label = "Phút")
                    Text(":", fontWeight = FontWeight.Bold)
                    TimeInputUnit(value = seconds, onValueChange = { if (it.length <= 2 && (it.toIntOrNull() ?: 0) < 60) seconds = it }, label = "Giây")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(R.string.label_image_preview),
                            modifier = Modifier
                                .size(120.dp, 180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_select_image))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Hoặc dán URL ảnh vào đây:", fontSize = 12.sp, color = Color.Gray)
                    
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.hint_image_url)) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )
                }
            }

            Column {
                Text(
                    text = stringResource(R.string.label_rating, rating),
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = rating.toFloat(),
                    onValueChange = { rating = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.label_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    if (title.isNotBlank() && year.isNotBlank()) {
                        // Hợp nhất 3 ô thành chuỗi HH:mm:ss
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

@Composable
fun TimeInputUnit(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.all { char -> char.isDigit() }) onValueChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, fontSize = 16.sp),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}
