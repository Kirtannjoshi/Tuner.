package com.tuner.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.tuner.model.Station
import com.tuner.viewmodel.RadioViewModel

private data class NanoNetwork(val name: String, val label: String, val color: Long)

private val NANO_NETWORKS = listOf(
    NanoNetwork("BBC", "BBC Group", 0xFFE50000L),
    NanoNetwork("Capital FM", "Capital", 0xFF0055FFL),
    NanoNetwork("NPR", "NPR Radio", 0xFF222222L),
    NanoNetwork("Heart", "Heart FM", 0xFFFF0055L),
    NanoNetwork("Kiss", "KISS FM", 0xFFAABB00L),
    NanoNetwork("Classical", "Classical", 0xFFD4AF37L)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: RadioViewModel, onPlayStation: (Station) -> Unit, onNavigateSettings: () -> Unit) {
    val stations by viewModel.stations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val currentFeed by viewModel.currentFeed.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedNetwork by remember { mutableStateOf<String?>(null) }

    // --- SCROLL LOGIC FOR SEARCH BAR ---
    val searchBarHeight = 64.dp
    val searchBarHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { searchBarHeight.roundToPx().toFloat() }
    var searchBarOffsetHeightPx by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
            override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: androidx.compose.ui.input.nestedscroll.NestedScrollSource): androidx.compose.ui.geometry.Offset {
                val delta = available.y
                val newOffset = searchBarOffsetHeightPx + delta
                searchBarOffsetHeightPx = newOffset.coerceIn(-searchBarHeightPx, 0f)
                return androidx.compose.ui.geometry.Offset.Zero
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AsyncImage(
                        model = "file:///android_asset/logo.svg",
                        contentDescription = "TUNER",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(26.dp)
                    )
                },
                actions = {
                    val hasUpdate by viewModel.hasUpdateBadge.collectAsState()
                    IconButton(onClick = onNavigateSettings) {
                        Box {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(24.dp))
                            if (hasUpdate) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color(0xFFEC4899), CircleShape)
                                        .border(1.5.dp, Color(0xFF121212), CircleShape)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 12.dp, top = searchBarHeight + 8.dp, end = 12.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // ── BENTO GRID HERO ──────────────────────────────────────────────
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isGlobal = currentFeed == "GLOBAL" && selectedNetwork == null && searchQuery.isEmpty()
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Transparent,
                            border = if (isGlobal) BorderStroke(2.dp, Color.White) else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier
                                .weight(0.6f)
                                .height(160.dp)
                                .scale(if (isGlobal) 1.02f else 1f)
                                .clickable { viewModel.fetchDiscover(); selectedNetwork = null; searchQuery = "" }
                        ) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFF9146FF))))
                            ) {
                                Text(
                                    "Global\nDiscovery",
                                    color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp,
                                    modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(0.4f).height(160.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val isTrending = currentFeed == "TRENDING"
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isTrending) Color(0xFF2D2D30) else Color(0xFF1E1E22),
                                border = if (isTrending) BorderStroke(2.dp, Color(0xFFEC4899).copy(alpha=0.6f)) else BorderStroke(1.dp, Color.White.copy(alpha=0.05f)),
                                modifier = Modifier.weight(1f).fillMaxWidth().scale(if (isTrending) 1.04f else 1f).clickable { viewModel.fetchByGenre("pop"); viewModel.setFeed("TRENDING"); selectedNetwork = null }
                            ) {
                                Box(contentAlignment = Alignment.Center) { 
                                    Text("Trending", color = if (isTrending) Color.White else Color(0xFFA1A1AA), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold) 
                                }
                            }
                            val isNews = currentFeed == "NEWS"
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isNews) Color(0xFF2D2D30) else Color(0xFF1E1E22),
                                border = if (isNews) BorderStroke(2.dp, Color(0xFF9146FF).copy(alpha=0.6f)) else BorderStroke(1.dp, Color.White.copy(alpha=0.05f)),
                                modifier = Modifier.weight(1f).fillMaxWidth().scale(if (isNews) 1.04f else 1f).clickable { viewModel.fetchByGenre("news"); viewModel.setFeed("NEWS"); selectedNetwork = null }
                            ) {
                                Box(contentAlignment = Alignment.Center) { 
                                    Text("News/Talk", color = if (isNews) Color.White else Color(0xFFA1A1AA), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold) 
                                }
                            }
                        }
                    }
                }

                // ── NANO NETWORK FRAMES ──────────────────────────────────────
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(NANO_NETWORKS) { network ->
                            val isSelected = selectedNetwork == network.name
                            val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.Transparent,
                                border = if (isSelected) BorderStroke(2.dp, Color.White) else BorderStroke(1.dp, Color.White.copy(alpha=0.1f)),
                                modifier = Modifier.size(130.dp, 80.dp).scale(scale).clickable {
                                    selectedNetwork = if (isSelected) null else network.name
                                    searchQuery = ""
                                    if (isSelected) viewModel.fetchDiscover() else viewModel.fetchNetwork(network.name)
                                }
                            ) {
                                Box(modifier = Modifier.fillMaxSize().background(
                                    Brush.linearGradient(listOf(Color(network.color).copy(alpha = 0.9f), Color(network.color).copy(alpha = 0.4f)))
                                )) {
                                    Text(network.label, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))
                                }
                            }
                        }
                    }
                }

                // ── SECTION TITLE ───────────────────────────────────────────
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = selectedNetwork ?: if (searchQuery.isNotBlank()) "Results" else "Curated Feed",
                        color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                // ── STATION GRID ────────────────────────────────────────────
                if (isLoading) {
                    items(8) { SkeletonCard() }
                } else {
                    items(stations, key = { it.stationuuid }) { station ->
                        StationCard(
                            station = station,
                            isActive = currentStation?.stationuuid == station.stationuuid,
                            isFavorite = favorites.contains(station.stationuuid),
                            onClick = { onPlayStation(station) },
                            onToggleFavorite = { viewModel.toggleFavorite(station.stationuuid) }
                        )
                    }
                }
            }

            // --- FLOATING SEARCH BAR (ANIMATED) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(searchBarHeight)
                    .graphicsLayer { translationY = searchBarOffsetHeightPx }
                    .background(Color(0xFF121212))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(30.dp),
                    color = Color(0xFF27272A),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFA1A1AA))
                        Spacer(Modifier.width(12.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { q ->
                                searchQuery = q
                                selectedNetwork = null
                                if (q.isBlank()) viewModel.fetchDiscover()
                                else if (q.length >= 2) viewModel.fetchNetwork(q)
                            },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) Text("Search 40,000+ stations...", color = Color(0x66FFFFFF), fontSize = 15.sp)
                                innerTextField()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StationCard(
    station: Station,
    isActive: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isActive) Color(0xFF1E1E22) else Color(0xFF18181B),
        border = if (isActive) BorderStroke(2.dp, Color(0xFF9146FF).copy(alpha=0.6f)) else BorderStroke(1.dp, Color.White.copy(alpha=0.03f)),
        modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).scale(scale).clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current).data(station.favicon).crossfade(true).build(),
                    contentDescription = station.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF27272A))
                )
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp).offset(x = 8.dp, y = (-8).dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEC4899) else Color(0xFF52525B),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column {
                Text(station.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActive) {
                        Text("● ", color = Color(0xFFEC4899), fontSize = 10.sp)
                    }
                    val tag = station.tags?.split(",")?.firstOrNull()?.trim()?.uppercase()
                    Text(
                        text = if (tag.isNullOrEmpty()) (station.countrycode ?: "GLOBAL") else "${station.countrycode ?: "GLOBAL"} • $tag",
                        color = if (isActive) Color(0xFFEC4899).copy(alpha=0.8f) else Color(0xFF71717A),
                        fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 0.5f, animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse))
    Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFF18181B), modifier = Modifier.fillMaxWidth().aspectRatio(0.85f)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = alpha)))
            Column {
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha = alpha)))
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha = alpha)))
            }
        }
    }
}

// Bottom sheet removed
