package com.example.Packages._1.Fragment.UI

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.TimeUnit

class AudioPlayerState {
    var isPlaying by mutableStateOf(false)
    var currentPosition by mutableStateOf(0L)
    var duration by mutableStateOf(0L)
    var currentAudioPath by mutableStateOf<String?>(null)
}

class AudioPlayerManager(private val context: Context) {
    private var player: ExoPlayer? = null
    val playerState = AudioPlayerState()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            playerState.isPlaying = state == Player.STATE_READY &&
                    player?.isPlaying == true
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playerState.isPlaying = isPlaying
        }
    }

    init {
        player = ExoPlayer.Builder(context).build().apply {
            addListener(playerListener)
        }
    }

    fun playAudioFromPosition(filePath: String, positionMs: Int) {
        player?.let { exoPlayer ->
            val file = File(filePath)
            if (!file.exists()) {
                return
            }

            try {
                // If it's a new audio file
                if (playerState.currentAudioPath != filePath) {
                    val mediaItem = MediaItem.fromUri(file.toURI().toString())
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    playerState.currentAudioPath = filePath
                    playerState.duration = exoPlayer.duration
                }

                exoPlayer.seekTo(positionMs.toLong())
                exoPlayer.play()
                playerState.isPlaying = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun togglePlayPause() {
        player?.let { exoPlayer ->
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            playerState.isPlaying = exoPlayer.isPlaying
        }
    }

    fun updateProgress() {
        player?.let { exoPlayer ->
            playerState.currentPosition = exoPlayer.currentPosition
        }
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    fun release() {
        player?.release()
        player = null
    }
}

@Composable
fun rememberAudioPlayerManager(): AudioPlayerManager {
    val context = LocalContext.current
    val audioPlayerManager = remember { AudioPlayerManager(context) }

    DisposableEffect(audioPlayerManager) {
        onDispose {
            audioPlayerManager.release()
        }
    }

    return audioPlayerManager
}

@Composable
fun MediaPlayerControls(
    modifier: Modifier = Modifier,
    playerManager: AudioPlayerManager
) {
    val playerState = playerManager.playerState

    // Update progress periodically
    LaunchedEffect(playerState.isPlaying) {
        while (playerState.isPlaying) {
            playerManager.updateProgress()
            delay(1000) // Update every second
        }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Current audio file name
            playerState.currentAudioPath?.let { path ->
                Text(
                    text = File(path).name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Progress bar
            Slider(
                value = playerState.currentPosition.toFloat(),
                onValueChange = { playerManager.seekTo(it.toLong()) },
                valueRange = 0f..playerState.duration.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            // Time display and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current position
                Text(
                    text = formatDuration(playerState.currentPosition),
                    style = MaterialTheme.typography.bodySmall
                )

                // Play/Pause button
                IconButton(onClick = { playerManager.togglePlayPause() }) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause
                        else Icons.Default.PlayArrow,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play"
                    )
                }

                // Duration
                Text(
                    text = formatDuration(playerState.duration),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatDuration(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
