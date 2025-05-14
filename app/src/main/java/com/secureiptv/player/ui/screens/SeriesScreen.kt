package com.secureiptv.player.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.secureiptv.player.data.models.*
import com.secureiptv.player.ui.components.*
import com.secureiptv.player.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

/**
 * Series screen
 */
@Composable
fun SeriesScreen(
    viewModel: MainViewModel,
    onEpisodeClick: (Episode) -> Unit
) {
    val seriesCategoriesState by viewModel.seriesCategoriesState.collectAsState()
    val seriesState by viewModel.seriesState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedSeries by remember { mutableStateOf<Series?>(null) }
    var seriesInfo by remember { mutableStateOf<SeriesInfo?>(null) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // Load categories on first composition
    LaunchedEffect(Unit) {
        viewModel.loadSeriesCategories()
        viewModel.loadAllSeries()
    }
    
    // Load series when category changes
    LaunchedEffect(selectedCategoryId) {
        if (selectedCategoryId != null) {
            viewModel.loadSeriesByCategory(selectedCategoryId!!)
        } else {
            viewModel.loadAllSeries()
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Categories
        item {
            when (val state = seriesCategoriesState) {
                is MainViewModel.ResourceState.Loading -> {
                    Box(modifier = Modifier.height(50.dp)) {
                        LoadingIndicator()
                    }
                }
                is MainViewModel.ResourceState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.loadSeriesCategories() }
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
        
        // Series list
        item {
            when (val state = seriesState) {
                is MainViewModel.ResourceState.Loading -> {
                    LoadingIndicator()
                }
                is MainViewModel.ResourceState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = {
                            if (selectedCategoryId != null) {
                                viewModel.loadSeriesByCategory(selectedCategoryId!!)
                            } else {
                                viewModel.loadAllSeries()
                            }
                        }
                    )
                }
                is MainViewModel.ResourceState.Success -> {
                    SeriesGrid(
                        seriesList = state.data,
                        favorites = favorites,
                        onSeriesClick = { series ->
                            selectedSeries = series
                            coroutineScope.launch {
                                try {
                                    // Use a public method in the ViewModel instead of accessing repository directly
                                    val result = viewModel.getSeriesInfo(series.seriesId)
                                    result.fold(
                                        onSuccess = { info ->
                                            seriesInfo = info
                                        },
                                        onFailure = {
                                            // Handle error
                                        }
                                    )
                                } catch (e: Exception) {
                                    // Handle exception
                                }
                            }
                        },
                        onFavoriteClick = { seriesId ->
                            viewModel.toggleFavorite(seriesId)
                        }
                    )
                }
            }
        }
        
        // Series details
        if (selectedSeries != null && seriesInfo != null) {
            item {
                SeriesDetails(
                    series = selectedSeries!!,
                    seriesInfo = seriesInfo!!,
                    onEpisodeClick = onEpisodeClick,
                    onClose = {
                        selectedSeries = null
                        seriesInfo = null
                    }
                )
            }
        }
    }
}

/**
 * Series grid
 */
@Composable
fun SeriesGrid(
    seriesList: List<Series>,
    favorites: Set<String>,
    onSeriesClick: (Series) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(seriesList.chunked(2)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (series in row) {
                    ContentCard(
                        title = series.name,
                        imageUrl = series.cover,
                        isFavorite = favorites.contains(series.seriesId.toString()),
                        onFavoriteClick = { onFavoriteClick(series.seriesId.toString()) },
                        onClick = { onSeriesClick(series) },
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

/**
 * Series details
 */
@Composable
fun SeriesDetails(
    series: Series,
    seriesInfo: SeriesInfo,
    onEpisodeClick: (Episode) -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = series.name,
                    style = MaterialTheme.typography.titleLarge
                )
                
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Close"
                    )
                }
            }
            
            // Series info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Cover image
                AsyncImage(
                    model = series.cover,
                    contentDescription = series.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Plot: ${seriesInfo.info?.plot ?: "No plot available"}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Cast: ${seriesInfo.info?.cast ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Director: ${seriesInfo.info?.director ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Genre: ${seriesInfo.info?.genre ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Rating: ${seriesInfo.info?.rating ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Episodes by season
            seriesInfo.episodes?.forEach { (season, episodes) ->
                var expanded by remember { mutableStateOf(false) }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Season header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Season $season",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                    
                    // Episodes
                    if (expanded) {
                        episodes.forEach { episode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEpisodeClick(episode) }
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${episode.episodeNum}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.width(32.dp)
                                )
                                
                                Text(
                                    text = episode.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}