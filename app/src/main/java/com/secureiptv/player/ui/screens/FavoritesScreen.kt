package com.secureiptv.player.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.secureiptv.player.ui.components.ContentCard
import com.secureiptv.player.ui.components.SectionHeader
import com.secureiptv.player.ui.viewmodels.MainViewModel

/**
 * Favorites screen
 */
@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onItemClick: (MainViewModel.RecentlyWatchedItem) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val recentlyWatched by viewModel.recentlyWatched.collectAsState()
    
    // Filter recently watched items that are in favorites
    val favoriteItems = recentlyWatched.filter { item ->
        favorites.contains(item.id)
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            SectionHeader(title = "Favorites")
        }
        
        if (favoriteItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No favorites yet. Add some by clicking the heart icon.")
                }
            }
        } else {
            items(favoriteItems.chunked(2)) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (item in row) {
                        ContentCard(
                            title = item.title,
                            imageUrl = item.posterUrl,
                            isFavorite = true,
                            onFavoriteClick = { viewModel.toggleFavorite(item.id) },
                            onClick = { onItemClick(item) },
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
        
        item {
            SectionHeader(title = "Recently Watched")
        }
        
        if (recentlyWatched.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recently watched items.")
                }
            }
        } else {
            items(recentlyWatched.chunked(2)) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (item in row) {
                        ContentCard(
                            title = item.title,
                            imageUrl = item.posterUrl,
                            isFavorite = favorites.contains(item.id),
                            onFavoriteClick = { viewModel.toggleFavorite(item.id) },
                            onClick = { onItemClick(item) },
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
}