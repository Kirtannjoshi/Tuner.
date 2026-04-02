package com.tuner.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tuner.model.Station
import androidx.palette.graphics.Palette
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import kotlinx.coroutines.withContext
import androidx.compose.animation.core.tween

@Composable
fun rememberDominantColor(imageUrl: String?): State<Color> {
    val defaultColor = Color(0xFF121212)
    val color = remember(imageUrl) { mutableStateOf(defaultColor) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(imageUrl) {
        if (imageUrl == null) return@LaunchedEffect
        val request = coil.request.ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false) // Necessary to get underlying pixels
            .size(128, 128) // Small size for faster color extraction
            .build()
        
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            val result = context.imageLoader.execute(request)
            if (result is coil.request.SuccessResult) {
                val bitmap = result.drawable.toBitmap()
                Palette.from(bitmap).generate { palette ->
                    // Favor vibrant colors for a "premium" ambient feel
                    val selectedColor = palette?.getVibrantColor(0)
                        ?.takeIf { it != 0 }
                        ?: palette?.getLightVibrantColor(0)
                        ?.takeIf { it != 0 }
                        ?: palette?.getDominantColor(0)
                        ?.takeIf { it != 0 }
                        ?: 0xFF121212.toInt()
                    
                    color.value = Color(selectedColor)
                }
            }
        }
    }
    return color
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun FullScreenPlayer(
    stations: List<Station>,
    initialStationId: String,
    isPlaying: Boolean,
    favorites: Set<String>,
    audioOutput: String = "SPEAKER",
    sessionDuration: Long = 0L,
    onPlayPause: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMinimize: () -> Unit,
    onStationSelected: (Station) -> Unit
) {
    androidx.activity.compose.BackHandler { onMinimize() }

    val initialIndex = remember { stations.indexOfFirst { it.stationuuid == initialStationId }.coerceAtLeast(0) }
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = initialIndex,
        pageCount = { stations.size }
    )

    var lastPage by remember { mutableIntStateOf(initialIndex) }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != lastPage) {
            lastPage = pagerState.currentPage
            onStationSelected(stations[pagerState.currentPage])
        }
    }

    androidx.compose.foundation.pager.VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageStation = stations[page]
        val isCurrentPlaying = (pagerState.currentPage == page) && isPlaying 
        
        PlayerPage(
            station = pageStation,
            isPlaying = isCurrentPlaying,
            isFavorite = favorites.contains(pageStation.stationuuid),
            audioOutput = audioOutput,
            sessionDuration = sessionDuration,
            onPlayPause = onPlayPause,
            onToggleFavorite = { onToggleFavorite(pageStation.stationuuid) },
            onMinimize = onMinimize
        )
    }
}

@Composable
fun PlayerPage(
    station: Station,
    isPlaying: Boolean,
    isFavorite: Boolean,
    audioOutput: String = "SPEAKER",
    sessionDuration: Long = 0L,
    onPlayPause: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMinimize: () -> Unit
) {
    val dominantColor by rememberDominantColor(station.favicon)
    val bgColor by androidx.compose.animation.animateColorAsState(targetValue = dominantColor, animationSpec = tween(700))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Blurred beautiful background
        coil.compose.AsyncImage(
            model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                .data(station.favicon)
                .crossfade(true)
                .size(coil.size.Size.ORIGINAL) // Ensures high quality thumbnail
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )
        
        // Overlay gradient for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMinimize) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "NOW PLAYING FROM ${station.country?.uppercase() ?: "GLOBE"}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // balance layout
            }

            Spacer(modifier = Modifier.weight(1f))

            val artScale by animateFloatAsState(
                targetValue = if (isPlaying) 1f else 0.88f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "art_scale"
            )

            // Minimalist Square Cover Art - Sober & Delicate
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) 
                    .scale(artScale),
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFF18181B),
                shadowElevation = 20.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Glow behind the art
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.9f)
                            .blur(40.dp)
                            .background(bgColor.copy(alpha = 0.2f))
                    )

                    coil.compose.AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                            .data(station.favicon)
                            .crossfade(true)
                            .size(coil.size.Size.ORIGINAL)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Subtle delicate overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Station Name & Favorite logic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { visible = true }
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, delayMillis = 200)) + slideInHorizontally(initialOffsetX = { -40 }, animationSpec = tween(600, delayMillis = 200))
                    ) {
                        Text(
                            text = station.name,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, delayMillis = 400)) + slideInHorizontally(initialOffsetX = { -30 }, animationSpec = tween(600, delayMillis = 400))
                    ) {
                        Text(
                            text = station.tags?.replace(",", " • ")?.uppercase() ?: "LIVE RADIO",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                val heartScale by animateFloatAsState(
                    targetValue = if (isFavorite) 1.25f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "heart_scale_full"
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEC4899) else Color(0xFFA1A1AA),
                        modifier = Modifier
                            .scale(heartScale)
                            .size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Tracker Bar (Session Duration)
            Column(modifier = Modifier.fillMaxWidth()) {
                val progress = (sessionDuration % 60) / 60f
                val minutes = sessionDuration / 60
                val seconds = sessionDuration % 60
                
                Slider(
                    value = progress,
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        disabledThumbColor = Color.White,
                        disabledActiveTrackColor = Color.White,
                        disabledInactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "LIVE",
                        color = Color(0xFFEC4899),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val playInteraction = remember { MutableInteractionSource() }
                val isPlayPressed by playInteraction.collectIsPressedAsState()
                val playScale by animateFloatAsState(
                    targetValue = if (isPlayPressed) 0.85f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "play_scale_full"
                )
                
                var controlsVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { controlsVisible = true }
                
                AnimatedVisibility(
                    visible = controlsVisible,
                    enter = fadeIn(tween(800, delayMillis = 600)) + scaleIn(initialScale = 0.5f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .scale(playScale)
                            .clip(CircleShape)
                            .background(Color(0xFFEC4899))
                            .clickable(
                                interactionSource = playInteraction,
                                indication = androidx.compose.foundation.LocalIndication.current,
                                onClick = onPlayPause
                            )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Hardware Output
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isBluetooth = audioOutput.startsWith("BLUETOOTH")
                val isWired = audioOutput.startsWith("WIRED")
                val deviceName = audioOutput.substringAfter("|", "")
                
                val icon = when {
                    isBluetooth -> "🎧"
                    isWired -> "🔌"
                    else -> "🔊"
                }

                val displayTxt = if (deviceName.isNotBlank()) deviceName else "Phone Speaker"

                Text(
                    text = "$icon  $displayTxt",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
