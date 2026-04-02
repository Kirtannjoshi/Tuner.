package com.tuner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.tuner.ui.theme.TunerTheme
import com.tuner.viewmodel.RadioViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen {
    object Home : Screen()
    object Library : Screen()
    object Settings : Screen()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: RadioViewModel by viewModels()

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted/denied — handled gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)

        setContent {
            TunerTheme {
                TunerApp(
                    viewModel = viewModel,
                    onPlayStation = { station ->
                        // KEY: call startForegroundService from Activity context (always in foreground)
                        val intent = viewModel.buildPlayIntent(station)
                        startForegroundService(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun TunerApp(
    viewModel: RadioViewModel,
    onPlayStation: (com.tuner.model.Station) -> Unit
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isFullScreenPlayerOpen by remember { mutableStateOf(false) }
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val audioOutput by viewModel.audioOutput.collectAsState()
    val updateAvailableUrl by viewModel.updateAvailableUrl.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        
        // --- AUTO UPDATER DIALOG ---
        updateAvailableUrl?.let { url ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissUpdateDialog() },
                title = { Text("Update Available", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text("A faster, brand new version of Tuner is available! Please update natively to get the latest polished features.", color = Color.White.copy(alpha=0.7f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                    ) {
                        Text("Update Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissUpdateDialog() }) {
                        Text("Dismiss", color = Color.White.copy(alpha=0.5f))
                    }
                },
                containerColor = Color(0xFF18181b),
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            )
        }

        Scaffold(
            containerColor = Color(0xFF000000),
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // Player bar slides up when a station is selected
                    AnimatedVisibility(
                        visible = currentStation != null,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(300)),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(200))
                    ) {
                        currentStation?.let { station ->
                            PlayerBar(
                                station = station,
                                isPlaying = isPlaying,
                                isFavorite = favorites.contains(station.stationuuid),
                                audioOutput = audioOutput,
                                onPlayPause = { viewModel.togglePlayPause() },
                                onToggleFavorite = { viewModel.toggleFavorite(station.stationuuid) },
                                onExpand = { isFullScreenPlayerOpen = true }
                            )
                        }
                    }

                    // Smooth full-width Material Navigation Bar (Spotify layout)
                NavigationBar(
                    containerColor = Color(0xFF121212),
                    contentColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Explore", modifier = Modifier.padding(bottom = 2.dp)) },
                        label = { Text("Explore", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFFFFF),
                            selectedTextColor = Color(0xFFFFFFFF),
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA),
                            indicatorColor = Color(0xFF27272A)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.Library,
                        onClick = { currentScreen = Screen.Library },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Library", modifier = Modifier.padding(bottom = 2.dp)) },
                        label = { Text("Library", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFFFFF),
                            selectedTextColor = Color(0xFFFFFFFF),
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA),
                            indicatorColor = Color(0xFF27272A)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)) + 
                     scaleIn(initialScale = 0.92f, animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)))
                        .togetherWith(fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                                       scaleOut(targetScale = 1.08f, animationSpec = spring(stiffness = Spring.StiffnessLow)))
                },
                label = "screen_transition"
            ) { screen ->
                    when (screen) {
                        is Screen.Home -> HomeScreen(
                            viewModel = viewModel,
                            onPlayStation = { station ->
                                if (viewModel.selectStation(station)) {
                                    onPlayStation(station)
                                }
                            },
                            onNavigateSettings = { currentScreen = Screen.Settings }
                        )
                        is Screen.Settings -> SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.Home }
                        )
                        is Screen.Library -> LibraryScreen(
                            viewModel = viewModel,
                            onPlayStation = { station ->
                                if (viewModel.selectStation(station)) {
                                    onPlayStation(station)
                                }
                            }
                        )
                    }
            }
        }
    }

    // --- FULL SCREEN PLAYER OVERLAY ---
    AnimatedVisibility(
        visible = isFullScreenPlayerOpen && currentStation != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(300)),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(300)),
        modifier = Modifier.fillMaxSize()
    ) {
        currentStation?.let { station ->
            val stationsList by viewModel.stations.collectAsState()
            val sessionDuration by viewModel.sessionDuration.collectAsState()
            FullScreenPlayer(
                stations = stationsList,
                initialStationId = station.stationuuid,
                isPlaying = isPlaying,
                favorites = favorites,
                audioOutput = audioOutput,
                sessionDuration = sessionDuration,
                onPlayPause = { viewModel.togglePlayPause() },
                onToggleFavorite = { uuid -> viewModel.toggleFavorite(uuid) },
                onMinimize = { isFullScreenPlayerOpen = false },
                onStationSelected = { s -> 
                    if (viewModel.selectStation(s)) {
                        onPlayStation(s)
                    }
                }
            )
        }
    }
}
}
