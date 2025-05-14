package com.secureiptv.player.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.secureiptv.player.data.models.Category
import com.secureiptv.player.data.models.Movie
import com.secureiptv.player.ui.components.*
import com.secureiptv.player.ui.viewmodels.MainViewModel

/**
 * Movies screen
 */
@Composable
fun MoviesScreen(
    viewModel: MainViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val vodCategoriesState by viewModel.vodCategoriesState.collectAsState()
    val vodStreamsState by viewModel.vodStreamsState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    
    // Load categories on first composition
    LaunchedEffect(Unit) {
        viewModel.loadVodCategories()
        viewModel.loadAllVodStreams()
    }
    
    // Load streams when category changes
    LaunchedEffect(selectedCategoryId) {
        if (selectedCategoryId != null) {
            viewModel.loadVodStreamsByCategory(selectedCategoryId!!)
        } else {
            viewModel.loadAllVodStreams()
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Categories
        item {
            when (val state = vodCategoriesState) {
                is MainViewModel.ResourceState.Loading -> {
                    Box(modifier = Modifier.height(50.dp)) {
                        LoadingIndicator()
                    }
                }
                is MainViewModel.ResourceState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.loadVodCategories() }
                    )
                }
                is MainViewModel.ResourceState.Success -> {
                    CategorySelector(
                        categories = state.data,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = { categoryId ->
                            selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
                        }
                    )
                }
            }
        }
        
        // Movies
        item {
            when (val state = vodStreamsState) {
                is MainViewModel.ResourceState.Loading -> {
                    LoadingIndicator()
                }
                is MainViewModel.ResourceState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = {
                            if (selectedCategoryId != null) {
                                viewModel.loadVodStreamsByCategory(selectedCategoryId!!)
                            } else {
                                viewModel.loadAllVodStreams()
                            }
                        }
                    )
                }
                is MainViewModel.ResourceState.Success -> {
                    MovieGrid(
                        movies = state.data,
                        favorites = favorites,
                        onMovieClick = onMovieClick,
                        onFavoriteClick = { movieId ->
                            viewModel.toggleFavorite(movieId)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Movie grid
 */
@Composable
fun MovieGrid(
    movies: List<Movie>,
    favorites: Set<String>,
    onMovieClick: (Movie) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(movies.chunked(2)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (movie in row) {
                    ContentCard(
                        title = movie.name,
                        imageUrl = movie.streamIcon ?: movie.coverUrl,
                        isFavorite = favorites.contains(movie.streamId.toString()),
                        onFavoriteClick = { onFavoriteClick(movie.streamId.toString()) },
                        onClick = { onMovieClick(movie) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // If the row has only one item, add an empty space
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}