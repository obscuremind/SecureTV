package com.secureiptv.player.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secureiptv.player.data.models.Category
import com.secureiptv.player.data.models.Episode
import com.secureiptv.player.data.models.LiveStream
import com.secureiptv.player.data.models.Movie
import com.secureiptv.player.data.models.Series
import com.secureiptv.player.data.models.StreamSource
import com.secureiptv.player.data.repository.IPTVRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen
 */
class MainViewModel : ViewModel() {

    private val repository = IPTVRepository()
    
    // Live TV
    private val _liveCategoriesState = MutableStateFlow<ResourceState<List<Category>>>(ResourceState.Loading)
    val liveCategoriesState: StateFlow<ResourceState<List<Category>>> = _liveCategoriesState.asStateFlow()
    
    private val _liveStreamsState = MutableStateFlow<ResourceState<List<LiveStream>>>(ResourceState.Loading)
    val liveStreamsState: StateFlow<ResourceState<List<LiveStream>>> = _liveStreamsState.asStateFlow()
    
    // Movies
    private val _vodCategoriesState = MutableStateFlow<ResourceState<List<Category>>>(ResourceState.Loading)
    val vodCategoriesState: StateFlow<ResourceState<List<Category>>> = _vodCategoriesState.asStateFlow()
    
    private val _vodStreamsState = MutableStateFlow<ResourceState<List<Movie>>>(ResourceState.Loading)
    val vodStreamsState: StateFlow<ResourceState<List<Movie>>> = _vodStreamsState.asStateFlow()
    
    // Series
    private val _seriesCategoriesState = MutableStateFlow<ResourceState<List<Category>>>(ResourceState.Loading)
    val seriesCategoriesState: StateFlow<ResourceState<List<Category>>> = _seriesCategoriesState.asStateFlow()
    
    private val _seriesState = MutableStateFlow<ResourceState<List<Series>>>(ResourceState.Loading)
    val seriesState: StateFlow<ResourceState<List<Series>>> = _seriesState.asStateFlow()
    
    // Favorites
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()
    
    // Recently watched
    private val _recentlyWatched = MutableStateFlow<List<RecentlyWatchedItem>>(emptyList())
    val recentlyWatched: StateFlow<List<RecentlyWatchedItem>> = _recentlyWatched.asStateFlow()
    
    /**
     * Loads live TV categories
     */
    fun loadLiveCategories() {
        _liveCategoriesState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getLiveCategories()
            
            result.fold(
                onSuccess = { categories ->
                    _liveCategoriesState.value = ResourceState.Success(categories)
                },
                onFailure = { error ->
                    _liveCategoriesState.value = ResourceState.Error(error.message ?: "Failed to load live categories")
                }
            )
        }
    }
    
    /**
     * Loads live streams by category
     */
    fun loadLiveStreamsByCategory(categoryId: String) {
        _liveStreamsState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getLiveStreamsByCategory(categoryId)
            
            result.fold(
                onSuccess = { streams ->
                    _liveStreamsState.value = ResourceState.Success(streams)
                },
                onFailure = { error ->
                    _liveStreamsState.value = ResourceState.Error(error.message ?: "Failed to load live streams")
                }
            )
        }
    }
    
    /**
     * Loads all live streams
     */
    fun loadAllLiveStreams() {
        _liveStreamsState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getAllLiveStreams()
            
            result.fold(
                onSuccess = { streams ->
                    _liveStreamsState.value = ResourceState.Success(streams)
                },
                onFailure = { error ->
                    _liveStreamsState.value = ResourceState.Error(error.message ?: "Failed to load live streams")
                }
            )
        }
    }
    
    /**
     * Loads VOD categories
     */
    fun loadVodCategories() {
        _vodCategoriesState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getVodCategories()
            
            result.fold(
                onSuccess = { categories ->
                    _vodCategoriesState.value = ResourceState.Success(categories)
                },
                onFailure = { error ->
                    _vodCategoriesState.value = ResourceState.Error(error.message ?: "Failed to load VOD categories")
                }
            )
        }
    }
    
    /**
     * Loads VOD streams by category
     */
    fun loadVodStreamsByCategory(categoryId: String) {
        _vodStreamsState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getVodStreamsByCategory(categoryId)
            
            result.fold(
                onSuccess = { streams ->
                    _vodStreamsState.value = ResourceState.Success(streams)
                },
                onFailure = { error ->
                    _vodStreamsState.value = ResourceState.Error(error.message ?: "Failed to load VOD streams")
                }
            )
        }
    }
    
    /**
     * Loads all VOD streams
     */
    fun loadAllVodStreams() {
        _vodStreamsState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getAllVodStreams()
            
            result.fold(
                onSuccess = { streams ->
                    _vodStreamsState.value = ResourceState.Success(streams)
                },
                onFailure = { error ->
                    _vodStreamsState.value = ResourceState.Error(error.message ?: "Failed to load VOD streams")
                }
            )
        }
    }
    
    /**
     * Loads series categories
     */
    fun loadSeriesCategories() {
        _seriesCategoriesState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getSeriesCategories()
            
            result.fold(
                onSuccess = { categories ->
                    _seriesCategoriesState.value = ResourceState.Success(categories)
                },
                onFailure = { error ->
                    _seriesCategoriesState.value = ResourceState.Error(error.message ?: "Failed to load series categories")
                }
            )
        }
    }
    
    /**
     * Loads series by category
     */
    fun loadSeriesByCategory(categoryId: String) {
        _seriesState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getSeriesByCategory(categoryId)
            
            result.fold(
                onSuccess = { series ->
                    _seriesState.value = ResourceState.Success(series)
                },
                onFailure = { error ->
                    _seriesState.value = ResourceState.Error(error.message ?: "Failed to load series")
                }
            )
        }
    }
    
    /**
     * Loads all series
     */
    fun loadAllSeries() {
        _seriesState.value = ResourceState.Loading
        
        viewModelScope.launch {
            val result = repository.getAllSeries()
            
            result.fold(
                onSuccess = { series ->
                    _seriesState.value = ResourceState.Success(series)
                },
                onFailure = { error ->
                    _seriesState.value = ResourceState.Error(error.message ?: "Failed to load series")
                }
            )
        }
    }
    
    /**
     * Gets a live stream URL
     */
    fun getLiveStreamUrl(streamId: Int): String {
        return repository.buildLiveStreamUrl(streamId)
    }

    /**
     * Builds the available sources for a live stream
     */
    fun getLiveStreamSource(stream: LiveStream): StreamSource {
        val httpUrl = getLiveStreamUrl(stream.streamId)
        return buildStreamSource(httpUrl, stream.directSource)
    }

    /**
     * Gets a VOD stream URL
     */
    fun getVodStreamUrl(streamId: Int): String {
        return repository.buildVodStreamUrl(streamId)
    }

    /**
     * Builds the available sources for a movie
     */
    fun getVodStreamSource(movie: Movie): StreamSource {
        val httpUrl = getVodStreamUrl(movie.streamId)
        return buildStreamSource(httpUrl, movie.directSource)
    }

    /**
     * Gets a series stream URL
     */
    fun getSeriesStreamUrl(streamId: Int): String {
        return repository.buildSeriesStreamUrl(streamId)
    }

    /**
     * Builds the available sources for an episode
     */
    fun getSeriesStreamSource(episode: Episode): StreamSource {
        val httpUrl = getSeriesStreamUrl(episode.id)
        return buildStreamSource(httpUrl, episode.directSource)
    }
    
    /**
     * Gets series info
     */
    suspend fun getSeriesInfo(seriesId: Int): Result<com.secureiptv.player.data.models.SeriesInfo> {
        return repository.getSeriesInfo(seriesId)
    }
    
    /**
     * Logs out the user
     */
    fun logout() {
        repository.logout()
    }
    
    /**
     * Toggles a favorite item
     */
    fun toggleFavorite(id: String) {
        val currentFavorites = _favorites.value.toMutableSet()
        
        if (currentFavorites.contains(id)) {
            currentFavorites.remove(id)
        } else {
            currentFavorites.add(id)
        }
        
        _favorites.value = currentFavorites
    }
    
    /**
     * Adds an item to recently watched
     */
    fun addToRecentlyWatched(item: RecentlyWatchedItem) {
        val currentList = _recentlyWatched.value.toMutableList()
        
        // Remove if already exists
        currentList.removeIf { it.id == item.id && it.type == item.type }
        
        // Add to the beginning
        currentList.add(0, item)
        
        // Limit to 20 items
        if (currentList.size > 20) {
            currentList.removeAt(currentList.size - 1)
        }
        
        _recentlyWatched.value = currentList
    }
    
    /**
     * Represents the state of a resource
     */
    sealed class ResourceState<out T> {
        object Loading : ResourceState<Nothing>()
        data class Success<T>(val data: T) : ResourceState<T>()
        data class Error(val message: String) : ResourceState<Nothing>()
    }
    
    /**
     * Represents a recently watched item
     */
    data class RecentlyWatchedItem(
        val id: String,
        val type: ItemType,
        val title: String,
        val posterUrl: String?,
        val streamId: Int
    )
    
    /**
     * Represents the type of an item
     */
    enum class ItemType {
        LIVE, VOD, SERIES
    }

    private fun buildStreamSource(httpUrl: String, directSource: String?): StreamSource {
        return StreamSource(
            httpUrl = httpUrl,
            torrentUrl = extractTorrentUrl(directSource)
        )
    }

    private fun extractTorrentUrl(directSource: String?): String? {
        val value = directSource?.trim().orEmpty()
        if (value.isEmpty()) {
            return null
        }

        val lower = value.lowercase()
        return if (lower.startsWith("magnet:") || lower.endsWith(".torrent")) {
            value
        } else {
            null
        }
    }
}