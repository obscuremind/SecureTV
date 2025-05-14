package com.secureiptv.player.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secureiptv.player.ui.viewmodels.MainViewModel

/**
 * Main screen with bottom navigation
 */
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToPlayer: (String, String) -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            BottomAppBar {
                NavigationBar {
                    TabItem.values().forEachIndexed { index, tabItem ->
                        NavigationBarItem(
                            icon = { Icon(tabItem.icon, contentDescription = tabItem.title) },
                            label = { Text(tabItem.title) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        },
        topBar = {
            androidx.compose.material.TopAppBar(
                title = { Text(TabItem.values()[selectedTab].title) },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> LiveTVScreen(
                    viewModel = viewModel,
                    onStreamClick = { stream ->
                        val streamUrl = viewModel.getLiveStreamUrl(stream.streamId)
                        onNavigateToPlayer(streamUrl, stream.name)
                    }
                )
                1 -> MoviesScreen(
                    viewModel = viewModel,
                    onMovieClick = { movie ->
                        val streamUrl = viewModel.getVodStreamUrl(movie.streamId)
                        onNavigateToPlayer(streamUrl, movie.name)
                    }
                )
                2 -> SeriesScreen(
                    viewModel = viewModel,
                    onEpisodeClick = { episode ->
                        val streamUrl = viewModel.getSeriesStreamUrl(episode.id)
                        onNavigateToPlayer(streamUrl, episode.title)
                    }
                )
                3 -> FavoritesScreen(
                    viewModel = viewModel,
                    onItemClick = { item ->
                        val streamUrl = when (item.type) {
                            MainViewModel.ItemType.LIVE -> viewModel.getLiveStreamUrl(item.streamId)
                            MainViewModel.ItemType.VOD -> viewModel.getVodStreamUrl(item.streamId)
                            MainViewModel.ItemType.SERIES -> viewModel.getSeriesStreamUrl(item.streamId)
                        }
                        onNavigateToPlayer(streamUrl, item.title)
                    }
                )
            }
        }
    }
}

/**
 * Tab items for bottom navigation
 */
enum class TabItem(val title: String, val icon: ImageVector) {
    LIVE_TV("Live TV", Icons.Default.LiveTv),
    MOVIES("Movies", Icons.Default.Movie),
    SERIES("Series", Icons.Default.Tv),
    FAVORITES("Favorites", Icons.Default.Favorite)
}