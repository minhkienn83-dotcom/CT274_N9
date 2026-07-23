package com.example.movie.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.movie.R
import com.example.movie.data.Movie
import com.example.movie.ui.MovieViewModel
import com.example.movie.ui.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onAddMovieClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentSortOrder by viewModel.sortOrder.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val showImages by viewModel.showImages.collectAsState()
    var isSearchMode by remember { mutableStateOf(false) }

    BackHandler(enabled = isSearchMode) {
        viewModel.onSearchQueryChange("")
        isSearchMode = false
    }

    MovieContent(
        movies = movies,
        searchQuery = searchQuery,
        isSearchMode = isSearchMode,
        isGridView = isGridView,
        showImages = showImages,
        currentSortOrder = currentSortOrder,
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onSortOrderChange = { viewModel.onSortOrderChange(it) },
        onToggleSearch = { 
            if (isSearchMode) viewModel.onSearchQueryChange("")
            isSearchMode = !isSearchMode 
        },
        onAddMovieClick = onAddMovieClick,
        onMovieClick = onMovieClick,
        onDeleteMovie = { viewModel.deleteMovie(it) },
        onSettingsClick = onSettingsClick,
        onStatisticsClick = onStatisticsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieContent(
    movies: List<Movie>,
    searchQuery: String,
    isSearchMode: Boolean,
    isGridView: Boolean,
    showImages: Boolean,
    currentSortOrder: SortOrder,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onToggleSearch: () -> Unit,
    onAddMovieClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onDeleteMovie: (Movie) -> Unit,
    onSettingsClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showSortSheet by remember { mutableStateOf(false) }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 16.dp, end = 16.dp)) {
                Text(text = "Sắp xếp theo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                SortOptionItem(label = "Mới nhất đến cũ nhất", icon = Icons.Default.ArrowDownward, isSelected = currentSortOrder == SortOrder.NEWEST, onClick = { onSortOrderChange(SortOrder.NEWEST); showSortSheet = false })
                SortOptionItem(label = "Cũ nhất đến mới nhất", icon = Icons.Default.ArrowUpward, isSelected = currentSortOrder == SortOrder.OLDEST, onClick = { onSortOrderChange(SortOrder.OLDEST); showSortSheet = false })
                SortOptionItem(label = "Đánh giá cao nhất", icon = Icons.Default.Star, isSelected = currentSortOrder == SortOrder.RATING_HIGH, onClick = { onSortOrderChange(SortOrder.RATING_HIGH); showSortSheet = false })
                SortOptionItem(label = "Đánh giá thấp nhất", icon = Icons.Default.StarBorder, isSelected = currentSortOrder == SortOrder.RATING_LOW, onClick = { onSortOrderChange(SortOrder.RATING_LOW); showSortSheet = false })
                SortOptionItem(label = "Tên phim (A -> Z)", icon = Icons.Default.SortByAlpha, isSelected = currentSortOrder == SortOrder.TITLE_AZ, onClick = { onSortOrderChange(SortOrder.TITLE_AZ); showSortSheet = false })
                SortOptionItem(label = "Tên phim (Z -> A)", icon = Icons.Default.SortByAlpha, isSelected = currentSortOrder == SortOrder.TITLE_ZA, onClick = { onSortOrderChange(SortOrder.TITLE_ZA); showSortSheet = false })
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Film",
                        modifier = Modifier.height(100.dp).wrapContentWidth().padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart 
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.height(120.dp)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(110.dp).navigationBarsPadding(),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TaskbarIconBox(icon = Icons.Default.Search, onClick = onToggleSearch, isActive = isSearchMode)
                        TaskbarIconBox(icon = Icons.AutoMirrored.Filled.Sort, onClick = { showSortSheet = true }, isActive = showSortSheet)
                        Box(modifier = Modifier.weight(1.2f), contentAlignment = Alignment.Center) {
                            LargeFloatingActionButton(onClick = onAddMovieClick, containerColor = MaterialTheme.colorScheme.primary, shape = CircleShape, modifier = Modifier.size(56.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                            }
                        }
                        TaskbarIconBox(icon = Icons.Default.BarChart, onClick = onStatisticsClick)
                        TaskbarIconBox(icon = Icons.Default.Settings, onClick = onSettingsClick)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isSearchMode) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Nhập tên phim cần tìm...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { onSearchQueryChange("") }) { Icon(Icons.Default.Clear, contentDescription = null) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (movies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchQuery.isNotEmpty()) "Không tìm thấy phim." else stringResource(R.string.empty_movie_list), color = Color.Gray)
                }
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movies) { movie ->
                            MovieGridItem(movie, showImages, onMovieClick, onDeleteMovie)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp)
                    ) {
                        items(movies) { movie ->
                            MovieListItem(movie, showImages, onMovieClick, onDeleteMovie)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieListItem(movie: Movie, showImages: Boolean, onMovieClick: (Int) -> Unit, onDeleteMovie: (Movie) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onMovieClick(movie.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (showImages) {
                AsyncImage(
                    model = movie.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp, 120.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.loading),
                    error = painterResource(R.drawable.loading)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = movie.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.label_release_year, movie.year), fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                StarRating(rating = movie.rating)
            }
            IconButton(onClick = { onDeleteMovie(movie) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}

@Composable
fun MovieGridItem(movie: Movie, showImages: Boolean, onMovieClick: (Int) -> Unit, onDeleteMovie: (Movie) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onMovieClick(movie.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (showImages) {
                AsyncImage(
                    model = movie.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.loading),
                    error = painterResource(R.drawable.loading)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = movie.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    StarRating(rating = movie.rating)
                    IconButton(onClick = { onDeleteMovie(movie) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SortOptionItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() }, color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            if (isSelected) { Spacer(modifier = Modifier.weight(1f)); Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
fun RowScope.TaskbarIconBox(icon: ImageVector, onClick: () -> Unit, isActive: Boolean = false) {
    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        TaskbarIconBoxInternal(icon, onClick, isActive)
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
        MovieContent(movies = sampleMovies, searchQuery = "", isSearchMode = false, isGridView = false, showImages = true, currentSortOrder = SortOrder.NEWEST, onSearchQueryChange = {}, onSortOrderChange = {}, onToggleSearch = {}, onAddMovieClick = {}, onMovieClick = {}, onDeleteMovie = { sampleMovies.remove(it) }, onSettingsClick = {}, onStatisticsClick = {})
    }
}
