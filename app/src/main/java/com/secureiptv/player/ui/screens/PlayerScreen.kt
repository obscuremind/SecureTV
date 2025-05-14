package com.secureiptv.player.ui.screens

import android.content.pm.ActivityInfo
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.secureiptv.player.ui.viewmodels.PlayerViewModel

/**
 * Video player screen
 */
@Composable
fun PlayerScreen(
    streamUrl: String,
    title: String,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsState()
    val isFullScreen by viewModel.isFullScreen.collectAsState()
    
    var showControls by remember { mutableStateOf(true) }
    var controlsTimer by remember { mutableStateOf(0) }
    
    // Initialize player
    LaunchedEffect(Unit) {
        viewModel.initializePlayer()
        viewModel.playStream(streamUrl, title)
    }
    
    // Handle full screen mode
    LaunchedEffect(isFullScreen) {
        val activity = context as? androidx.activity.ComponentActivity
        activity?.let {
            if (isFullScreen) {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
    
    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls) {
            controlsTimer = 0
            while (controlsTimer < 5) {
                kotlinx.coroutines.delay(1000)
                controlsTimer++
            }
            showControls = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ExoPlayer view
        AndroidView(
            factory = { ctx ->
                StyledPlayerView(ctx).apply {
                    player = viewModel.getPlayer()
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Custom controls overlay
        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    
                    IconButton(onClick = { viewModel.toggleFullScreen() }) {
                        Icon(
                            imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullScreen) "Exit Fullscreen" else "Fullscreen",
                            tint = Color.White
                        )
                    }
                }
                
                // Center play/pause button
                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (playerState is PlayerViewModel.PlayerState.Playing) 
                            Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playerState is PlayerViewModel.PlayerState.Playing) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Bottom controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    // Progress bar
                    val progress = remember(viewModel.getCurrentPosition(), viewModel.getDuration()) {
                        if (viewModel.getDuration() > 0) {
                            viewModel.getCurrentPosition().toFloat() / viewModel.getDuration()
                        } else {
                            0f
                        }
                    }
                    
                    Slider(
                        value = progress,
                        onValueChange = { 
                            val newPosition = (it * viewModel.getDuration()).toLong()
                            viewModel.seekTo(newPosition)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    
                    // Time display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(viewModel.getCurrentPosition()),
                            color = Color.White
                        )
                        
                        Text(
                            text = formatTime(viewModel.getDuration()),
                            color = Color.White
                        )
                    }
                }
            }
        }
        
        // Loading indicator
        if (playerState is PlayerViewModel.PlayerState.Buffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        }
        
        // Error message
        if (playerState is PlayerViewModel.PlayerState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${(playerState as PlayerViewModel.PlayerState.Error).message}",
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.playStream(streamUrl, title) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        
        // Click detector for showing/hiding controls
        if (!showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                        .clickable {
                            showControls = true
                        }
                )
            }
        }
    }
    
    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }
}

/**
 * Format time in milliseconds to MM:SS
 */
private fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}