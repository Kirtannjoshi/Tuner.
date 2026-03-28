package com.tuner.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import coil.imageLoader
import coil.request.ImageRequest
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.MediaMetadataCompat
import com.tuner.R
import com.tuner.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

private const val CHANNEL_ID = "tuner_playback"
private const val NOTIFICATION_ID = 1001

class RadioService : Service() {

    companion object {
        const val EXTRA_STREAM_URL = "stream_url"
        const val EXTRA_STATION_NAME = "station_name"
        const val ACTION_PLAY = "com.tuner.ACTION_PLAY"
        const val ACTION_TOGGLE = "com.tuner.ACTION_TOGGLE"

        // UI observes this — no binding needed
        private val _isPlaying = MutableStateFlow(false)
        val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error.asStateFlow()
    }

    inner class LocalBinder : Binder() {
        fun getService(): RadioService = this@RadioService
    }

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private var currentName = "Tuner"
    private var currentFavicon: String? = null
    private var currentTags = "LIVE RADIO"
    private var currentBitmap: Bitmap? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            updateNotification(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            val isRecoverable = error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED
                    
            if (isRecoverable) {
                // Graceful fallback and auto-restart
                _error.value = "Reconnecting stream..."
                player.seekToDefaultPosition()
                player.prepare()
                player.playWhenReady = true
            } else {
                _isPlaying.value = false
                _error.value = "Playback failed: ${error.message}"
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
            .setUsage(androidx.media3.common.C.USAGE_MEDIA)
            .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        // PERFECT FAST-START BUFFER STRATEGY
        // - minBufferMs: 3000 (satisfies rule: must be >= bufferForPlayback/AfterRebuffer)
        // - bufferForPlaybackMs: 250 (starts audio instantly after capturing just 0.25s of data)
        // - maxBufferMs: 50000 (maintains deep reservoir to prevent chopping)
        val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
            .setBufferDurationsMs(3_000, 50_000, 250, 1_500)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(androidx.media3.common.C.WAKE_MODE_NETWORK)
            .setLoadControl(loadControl)
            .build()
            .also { it.addListener(playerListener) }

        mediaSession = MediaSessionCompat(this, "TunerSession").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { player.play() }
                override fun onPause() { player.pause() }
                override fun onStop() { player.stop() }
            })
            isActive = true
        }

        // KEY FIX: Always start foreground immediately in onCreate()
        val notification = buildNotification(currentName, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this, NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_STREAM_URL) ?: return START_STICKY
                val name = intent.getStringExtra(EXTRA_STATION_NAME) ?: "Tuner"
                currentFavicon = intent.getStringExtra("EXTRA_FAVICON_URL")
                currentTags = intent.getStringExtra("EXTRA_STATION_TAGS") ?: "LIVE RADIO"
                loadAndPlay(url, name)
            }
            ACTION_TOGGLE -> {
                if (player.isPlaying) player.pause() else requestFocusAndPlay()
            }
        }
        return START_STICKY
    }

    private fun loadAndPlay(url: String, name: String) {
        currentName = name
        _error.value = null
        try {
            player.stop()
            player.setMediaItem(MediaItem.fromUri(url))
            player.prepare()
            player.playWhenReady = true
            
            // Fetch Bitmap asynchronously for Lock Screen Notification
            currentBitmap = null
            updateNotification(false) // Show buffering state immediately without picture
            
            val faviconUrl = currentFavicon
            if (!faviconUrl.isNullOrBlank()) {
                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val request = ImageRequest.Builder(this@RadioService)
                            .data(faviconUrl)
                            .size(512)
                            .build()
                        val result = this@RadioService.imageLoader.execute(request)
                        if (result.drawable is BitmapDrawable) {
                            currentBitmap = (result.drawable as BitmapDrawable).bitmap
                            updateNotification(player.isPlaying) // Refresh notification with picture
                        }
                    } catch (e: Exception) { /* ignore image fetch failure */ }
                }
            } else {
                updateNotification(false)
            }
        } catch (e: Exception) {
            _error.value = "Failed to load stream"
        }
    }

    private fun requestFocusAndPlay() {
        player.play()
    }

    private fun updateNotification(playing: Boolean) {
        try {
            // Update the MediaSession metadata with the image so Android OS picks it up!
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentName)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentTags)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, currentBitmap)
                    .build()
            )
            
            val nm = getSystemService(NotificationManager::class.java) ?: return
            nm.notify(NOTIFICATION_ID, buildNotification(currentName, playing))
        } catch (_: Exception) { /* never crash the app for a notification */ }
    }

    private fun buildNotification(name: String, playing: Boolean): Notification {
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val togglePi = PendingIntent.getService(
            this, 1,
            Intent(this, RadioService::class.java).apply { action = ACTION_TOGGLE },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val subtitle = when {
            playing -> "Playing — $name"
            name == "Tuner" -> "Tap a station to play"
            else -> "Paused — $name"
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tuner")
            .setContentText(subtitle)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(currentBitmap)
            .setContentIntent(openApp)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(playing)
            .setSilent(true)
            .apply {
                if (name != "Tuner") {
                    addAction(
                        if (playing) R.drawable.ic_pause else R.drawable.ic_play,
                        if (playing) "Pause" else "Play",
                        togglePi
                    )
                }
            }
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .also { if (name != "Tuner") it.setShowActionsInCompactView(0) }
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANNEL_ID, "Now Playing", NotificationManager.IMPORTANCE_LOW)
                .apply {
                    description = "Tuner radio playback"
                    setShowBadge(false)
                }
                .also { getSystemService(NotificationManager::class.java)?.createNotificationChannel(it) }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        try {
            _isPlaying.value = false
            player.removeListener(playerListener)
            mediaSession.release()
            player.release()
        } catch (_: Exception) { /* clean teardown */ }
        super.onDestroy()
    }
}
