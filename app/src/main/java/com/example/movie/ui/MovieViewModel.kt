package com.example.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.data.Movie
import com.example.movie.data.MovieRepository
import com.example.movie.data.TmdbMovieDto
import com.example.movie.data.TmdbService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOrder {
    NEWEST, OLDEST, RATING_HIGH, RATING_LOW, TITLE_AZ, TITLE_ZA
}

@OptIn(FlowPreview::class)
class MovieViewModel(private val repository: MovieRepository) : ViewModel() {
    
    private val tmdbService = TmdbService()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Thứ tự sắp xếp
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)
    val sortOrder = _sortOrder.asStateFlow()

    private val _tmdbSearchFlow = MutableSharedFlow<String>()
    private val _tmdbSuggestions = MutableStateFlow<List<TmdbMovieDto>>(emptyList())
    val tmdbSuggestions = _tmdbSuggestions.asStateFlow()

    private val _isSearchingTmdb = MutableStateFlow(false)
    val isSearchingTmdb = _isSearchingTmdb.asStateFlow()

    init {
        viewModelScope.launch {
            _tmdbSearchFlow
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.length >= 2) {
                        _isSearchingTmdb.value = true
                        val results = tmdbService.searchMovies(query)
                        _tmdbSuggestions.value = results
                        _isSearchingTmdb.value = false
                    } else {
                        _tmdbSuggestions.value = emptyList()
                        _isSearchingTmdb.value = false
                    }
                }
        }
    }

    // Kết hợp Tìm kiếm và Sắp xếp trong cùng một luồng dữ liệu
    val movies: StateFlow<List<Movie>> = combine(
        repository.allMovies,
        _searchQuery,
        _sortOrder
    ) { movieList, query, order ->
        val filtered = if (query.isBlank()) movieList else movieList.filter { it.title.contains(query, ignoreCase = true) }
        
        when (order) {
            SortOrder.NEWEST -> filtered.sortedByDescending { it.id } // ID tăng dần tương ứng với ngày nhập
            SortOrder.OLDEST -> filtered.sortedBy { it.id }
            SortOrder.RATING_HIGH -> filtered.sortedByDescending { it.rating }
            SortOrder.RATING_LOW -> filtered.sortedBy { it.rating }
            SortOrder.TITLE_AZ -> filtered.sortedBy { it.title }
            SortOrder.TITLE_ZA -> filtered.sortedByDescending { it.title }
        }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSortOrderChange(newOrder: SortOrder) { _sortOrder.value = newOrder }
    fun onSearchQueryChange(newQuery: String) { _searchQuery.value = newQuery }
    fun searchTmdb(title: String) { viewModelScope.launch { _tmdbSearchFlow.emit(title) } }
    fun clearSuggestions() { _tmdbSuggestions.value = emptyList() }

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    // Trạng thái hiển thị Grid (Lưới) hay List (Danh sách)
    private val _isGridView = MutableStateFlow(false)
    val isGridView = _isGridView.asStateFlow()

    // Trạng thái hiển thị hình ảnh
    private val _showImages = MutableStateFlow(true)
    val showImages = _showImages.asStateFlow()

    // Màu chủ đạo đang chọn (Mặc định là Tím)
    private val _primaryColor = MutableStateFlow(0xFF6750A4)
    val primaryColor = _primaryColor.asStateFlow()

    fun toggleDarkMode() { _isDarkMode.value = !_isDarkMode.value }
    fun toggleGridView() { _isGridView.value = !_isGridView.value }
    fun toggleShowImages() { _showImages.value = !_showImages.value }
    fun setPrimaryColor(colorLong: Long) { _primaryColor.value = colorLong }

    fun addMovie(title: String, year: String, rating: Int, notes: String, imageUrl: String, lastWatchedTime: String) {
        viewModelScope.launch {
            val newMovie = Movie(title = title, year = year, rating = rating, notes = notes, imageUrl = imageUrl, lastWatchedTime = lastWatchedTime)
            repository.insertMovie(newMovie)
        }
    }

    fun deleteMovie(movie: Movie) { viewModelScope.launch { repository.deleteMovie(movie) } }
    fun deleteAllMovies() { viewModelScope.launch { repository.deleteAllMovies() } }
    fun getMovieById(id: Int) = repository.getMovieById(id)
    
    fun updateMovie(movie: Movie) { viewModelScope.launch { repository.updateMovie(movie) } }

    fun updateWatchedTime(movieId: Int, newTime: String) {
        viewModelScope.launch {
            val movie = repository.getMovieById(movieId).firstOrNull()
            movie?.let { repository.updateMovie(it.copy(lastWatchedTime = newTime)) }
        }
    }
}
