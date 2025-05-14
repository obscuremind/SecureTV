package com.secureiptv.player.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.secureiptv.player.data.models.Category
import com.secureiptv.player.data.models.LiveStream
import com.secureiptv.player.ui.components.*
import com.secureiptv.player.ui.viewmodels.MainViewModel

/**
 * Live TV screen
 */
@Composable
fun LiveTVScreen(
    viewModel: MainViewModel,
    onStreamClick: (LiveStream) -> Unit
) {
    val liveCategoriesState by viewModel.liveCategoriesState.collectAsState()
    val liveStreamsState by viewModel.liveStreamsState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    
    // Load categories on first composition
    LaunchedEffect(Unit) {
        viewModel.loadLiveCategories()
        viewModel.loadAllLiveStreams()
    }
    
    // Load streams when category changes
    LaunchedEffect(selectedCategoryId) {
        if (selectedCategoryId != null) {
            viewModel.loadLiveStreamsByCategory(selectedCategoryId!!)
        } else {
            viewModel.loadAllLiveStreams()
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Categories
        item {
            when (val state = liveCategoriesState) {
                is MainViewModel.ResourceState.Loading -> {
                    Box(modifier = Modifier.height(50.dp)) {
                        LoadingIndicator()
                    }
                }
                is MainViewModel.ResourceState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.loadLiveCategories() }
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
        
        // Live streams
        item {
            when (val state = liveStreamsState) {
                is MainViewModel.ResourceState.Loading -> {
                    LoadingIndicator()
                }
                is MainViewModel.ResourceState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = {
                            if (selectedCategoryId != null) {
                                viewModel.loadLiveStreamsByCategory(selectedCategoryId!!)
                            } else {
                                viewModel.loadAllLiveStreams()
                            }
                        }
                    )
                }
                is MainViewModel.ResourceState.Success -> {
                    LiveStreamGrid(
                        streams = state.data,
                        favorites = favorites,
                        onStreamClick = onStreamClick,
                        onFavoriteClick = { streamId ->
                            viewModel.toggleFavorite(streamId)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Category selector
 */
@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                name = category.categoryName,
                isSelected = category.categoryId == selectedCategoryId,
                onClick = { onCategorySelected(category.categoryId) }
            )
        }
    }
}

/**
 * Live stream grid
 */
@Composable
fun LiveStreamGrid(
    streams: List<LiveStream>,
    favorites: Set<String>,
    onStreamClick: (LiveStream) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(streams.chunked(2)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (stream in row) {
                    ContentCard(
                        title = stream.name,
                        imageUrl = stream.streamIcon,
                        isFavorite = favorites.contains(stream.streamId.toString()),
                        onFavoriteClick = { onFavoriteClick(stream.streamId.toString()) },
                        onClick = { onStreamClick(stream) },
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