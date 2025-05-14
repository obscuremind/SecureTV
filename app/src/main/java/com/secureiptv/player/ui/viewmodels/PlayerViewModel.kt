package com.secureiptv.player.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.secureiptv.player.IPTVApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the video player
 */
class PlayerViewModel : ViewModel() {

    private var player: SimpleExoPlayer? = null
    private val trackSelector = DefaultTrackSelector(IPTVApplication.instance)
    
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    
    private val _isFullScreen = MutableStateFlow(false)
    val isFullScreen: StateFlow<Boolean> = _isFullScreen.asStateFlow()
    
    /**
     * Initializes the player
     */
    fun initializePlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(IPTVApplication.instance)
                .setTrackSelector(trackSelector)
                .build()
            
            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> _playerState.value = PlayerState.Buffering
                        Player.STATE_READY -> _playerState.value = PlayerState.Ready
                        Player.STATE_ENDED -> _playerState.value = PlayerState.Ended
                        Player.STATE_IDLE -> _playerState.value = PlayerState.Idle
                    }
                }
                
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        _playerState.value = PlayerState.Playing
                    } else if (_playerState.value == PlayerState.Playing) {
                        _playerState.value = PlayerState.Paused
                    }
                }
                
                override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException) {
                    _playerState.value = PlayerState.Error(error.message ?: "Playback error")
                }
            })
        }
    }
    
    /**
     * Plays a stream URL
     */
    fun playStream(url: String, title: String, subtitleUrl: String? = null) {
        initializePlayer()
        
        val mediaItem = if (subtitleUrl != null) {
            val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(subtitleUrl))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                .setLanguage("en")
                .setSelectionFlags(MediaItem.SubtitleConfiguration.SELECTION_FLAG_DEFAULT)
                .build()
                
            MediaItem.Builder()
                .setUri(url)
                .setMediaMetadata(
                    com.google.android.exoplayer2.MediaMetadata.Builder()
                        .setTitle(title)
                        .build()
                )
                .setSubtitleConfigurations(listOf(subtitleConfig))
                .build()
        } else {
            MediaItem.Builder()
                .setUri(url)
                .setMediaMetadata(
                    com.google.android.exoplayer2.MediaMetadata.Builder()
                        .setTitle(title)
                        .build()
                )
                .build()
        }
        
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
    
    /**
     * Gets the player instance
     */
    fun getPlayer(): SimpleExoPlayer? {
        return player
    }
    
    /**
     * Toggles play/pause
     */
    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    
    /**
     * Toggles fullscreen mode
     */
    fun toggleFullScreen() {
        _isFullScreen.value = !_isFullScreen.value
    }
    
    /**
     * Releases the player
     */
    fun releasePlayer() {
        player?.release()
        player = null
        _playerState.value = PlayerState.Idle
    }
    
    /**
     * Seeks to a position
     */
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
    
    /**
     * Gets the current position
     */
    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }
    
    /**
     * Gets the duration
     */
    fun getDuration(): Long {
        return player?.duration ?: 0
    }
    
    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
    
    /**
     * Represents the state of the player
     */
    sealed class PlayerState {
        object Idle : PlayerState()
        object Buffering : PlayerState()
        object Ready : PlayerState()
        object Playing : PlayerState()
        object Paused : PlayerState()
        object Ended : PlayerState()
        data class Error(val message: String) : PlayerState()
    }
}