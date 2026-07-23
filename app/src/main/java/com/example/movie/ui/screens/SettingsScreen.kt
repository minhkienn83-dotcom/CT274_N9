package com.example.movie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movie.R
import com.example.movie.ui.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MovieViewModel,
    onBackClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val showImages by viewModel.showImages.collectAsState()
    val currentPrimaryColor by viewModel.primaryColor.collectAsState()

    val availableColors = listOf(
        0xFF6750A4, // Tím mặc định
        0xFF388E3C, // Xanh lá
        0xFFD32F2F, // Đỏ
        0xFF1976D2, // Xanh dương
        0xFFF57C00, // Cam
        0xFFC2185B, // Hồng
        0xFF0097A7  // Xanh cổ vịt
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.btn_clear_all)) },
            text = { Text(stringResource(R.string.confirm_clear_all)) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteAllMovies(); showDeleteDialog = false }) {
                    Text(stringResource(R.string.dialog_confirm), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.dialog_cancel)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
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
        ) {
            // Mục Giao diện
            Text(text = "Giao diện", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            ListItem(
                headlineContent = { Text("Chế độ tối (Dark Mode)") },
                leadingContent = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                trailingContent = { Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode() }) }
            )

            ListItem(
                headlineContent = { Text("Hiển thị dạng Lưới (Grid)") },
                supportingContent = { Text("Xem phim theo 2 cột") },
                leadingContent = { Icon(Icons.Default.GridView, contentDescription = null) },
                trailingContent = { Switch(checked = isGridView, onCheckedChange = { viewModel.toggleGridView() }) }
            )

            ListItem(
                headlineContent = { Text("Hiển thị hình ảnh") },
                supportingContent = { Text("Bật/Tắt poster phim") },
                leadingContent = { Icon(Icons.Default.Image, contentDescription = null) },
                trailingContent = { Switch(checked = showImages, onCheckedChange = { viewModel.toggleShowImages() }) }
            )

            // PHẦN CHỌN MÀU (MỚI)
            ListItem(
                headlineContent = { Text("Màu sắc chủ đạo") },
                leadingContent = { Icon(Icons.Default.ColorLens, contentDescription = null) },
                supportingContent = {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        availableColors.forEach { colorLong ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorLong))
                                    .border(
                                        width = if (currentPrimaryColor == colorLong) 3.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setPrimaryColor(colorLong) }
                            )
                        }
                    }
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Mục Dữ liệu
            Text(text = stringResource(R.string.section_data), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            ListItem(
                headlineContent = { Text(stringResource(R.string.btn_clear_all)) },
                supportingContent = { Text("Xóa sạch danh sách phim đã lưu") },
                leadingContent = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red) },
                modifier = Modifier.clickable { showDeleteDialog = true }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Mục Thông tin
            Text(text = stringResource(R.string.section_about), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            ListItem(
                headlineContent = { Text(stringResource(R.string.app_author)) },
                supportingContent = { Text(stringResource(R.string.app_version)) },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
        }
    }
}
