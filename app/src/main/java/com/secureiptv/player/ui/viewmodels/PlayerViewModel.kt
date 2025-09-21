package com.secureiptv.player.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.secureiptv.player.IPTVApplication
import java.io.File
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

    private val _torrentStats = MutableStateFlow<TorrentStats?>(null)
    val torrentStats: StateFlow<TorrentStats?> = _torrentStats.asStateFlow()

    private val _isTorrentStatsVisible = MutableStateFlow(false)
    val isTorrentStatsVisible: StateFlow<Boolean> = _isTorrentStatsVisible.asStateFlow()

    private val _isUsingTorrent = MutableStateFlow(false)
    val isUsingTorrent: StateFlow<Boolean> = _isUsingTorrent.asStateFlow()

    private var currentHttpUrl: String? = null
    private var currentSubtitleUrl: String? = null
    private var currentTitle: String? = null

    private var torrentStream: TorrentStream? = null
    private var torrentListenerRegistered = false

    private val torrentListener = object : TorrentListener {
        override fun onStreamPrepared(torrent: Torrent?) {
            torrent?.videoFile?.let { file ->
                val title = currentTitle ?: file.name
                val mediaMetadata = com.google.android.exoplayer2.MediaMetadata.Builder()
                    .setTitle(title)
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.fromFile(file))
                    .setMediaMetadata(mediaMetadata)
                    .build()

                player?.stop()
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.play()
            }
        }

        override fun onStreamStarted(torrent: Torrent?) {
            _playerState.value = PlayerState.Buffering
        }

        override fun onStreamProgress(torrent: Torrent?, status: StreamStatus?) {
            if (status != null) {
                _torrentStats.value = TorrentStats(
                    progress = status.progress.toFloat(),
                    bufferProgress = status.bufferProgress.toFloat(),
                    downloadSpeed = status.downloadSpeed.toFloat(),
                    uploadSpeed = status.uploadSpeed.toFloat(),
                    seeds = status.seeds,
                    peers = status.peers
                )
            }
        }

        override fun onStreamReady(torrent: Torrent?) {
            _playerState.value = PlayerState.Playing
        }

        override fun onStreamStopped() {
            _torrentStats.value = null
            _isUsingTorrent.value = false
            _isTorrentStatsVisible.value = false
        }

        override fun onStreamError(torrent: Torrent?, e: Exception?) {
            handleTorrentError(e)
        }
    }
    
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
                
                override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                    _playerState.value = PlayerState.Error(error.message ?: "Playback error")
                }
            })
        }
    }
    
    /**
     * Plays a stream URL, optionally using a torrent source.
     */
    fun playStream(
        url: String,
        title: String,
        torrentUrl: String? = null,
        subtitleUrl: String? = null
    ) {
        initializePlayer()
        currentHttpUrl = url
        currentSubtitleUrl = subtitleUrl
        currentTitle = title
        _isTorrentStatsVisible.value = false

        if (!torrentUrl.isNullOrBlank()) {
            startTorrentStream(torrentUrl)
        } else {
            playHttpStream(url, title, subtitleUrl)
        }
    }

    private fun startTorrentStream(torrentUrl: String) {
        stopTorrentStream()
        val stream = ensureTorrentStream()
        _isUsingTorrent.value = true
        _playerState.value = PlayerState.Buffering
        stream.startStream(torrentUrl)
    }

    private fun ensureTorrentStream(): TorrentStream {
        if (torrentStream == null) {
            torrentStream = try {
                TorrentStream.getInstance()
            } catch (e: IllegalStateException) {
                null
            }

            if (torrentStream == null) {
                val context = IPTVApplication.instance
                val torrentDir = File(context.cacheDir, "torrent_streams").apply {
                    if (!exists()) {
                        mkdirs()
                    }
                }

                val options = TorrentOptions.Builder()
                    .saveLocation(torrentDir)
                    .removeFilesAfterStop(true)
                    .build()

                torrentStream = TorrentStream.init(options)
            }
        }

        if (!torrentListenerRegistered) {
            torrentStream?.addListener(torrentListener)
            torrentListenerRegistered = true
        }

        return torrentStream!!
    }

    private fun playHttpStream(url: String, title: String, subtitleUrl: String?) {
        stopTorrentStream()
        _isUsingTorrent.value = false

        val mediaItem = if (!subtitleUrl.isNullOrBlank()) {
            val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleUrl))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                .setLanguage("en")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
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

        player?.stop()
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    private fun handleTorrentError(error: Exception?) {
        val fallbackUrl = currentHttpUrl
        val title = currentTitle ?: ""

        if (fallbackUrl != null) {
            _playerState.value = PlayerState.Buffering
            playHttpStream(fallbackUrl, title, currentSubtitleUrl)
        } else {
            _playerState.value = PlayerState.Error(error?.message ?: "Playback error")
        }
    }

    private fun stopTorrentStream() {
        torrentStream?.let { stream ->
            try {
                stream.stopStream()
            } catch (_: Exception) {
                // Ignored – stopping is best effort
            }

            if (torrentListenerRegistered) {
                stream.removeListener(torrentListener)
                torrentListenerRegistered = false
            }
        }

        torrentStream = null
        _torrentStats.value = null
        _isTorrentStatsVisible.value = false
        _isUsingTorrent.value = false
    }

    fun toggleTorrentStatsVisibility() {
        if (_torrentStats.value != null) {
            _isTorrentStatsVisible.value = !_isTorrentStatsVisible.value
        }
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
        stopTorrentStream()
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
     * Snapshot of the current torrent streaming stats
     */
    data class TorrentStats(
        val progress: Float,
        val bufferProgress: Float,
        val downloadSpeed: Float,
        val uploadSpeed: Float,
        val seeds: Int,
        val peers: Int
    )

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