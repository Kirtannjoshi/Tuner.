package com.tuner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tuner.model.Station
import com.tuner.viewmodel.RadioViewModel

// ── Nano Service Network Chips ────────────────────────────────────────────────
private data class NanoNetwork(val name: String, val label: String, val color: Long)

private val NANO_NETWORKS = listOf(
    NanoNetwork("BBC", "BBC Group", 0xFF9146FFL),
    NanoNetwork("Capital FM", "Capital FM", 0xFF00E5FFL),
    NanoNetwork("NPR", "NPR Radio", 0xFF7CB9E8L),
    NanoNetwork("Heart", "Heart FM", 0xFFFF69B4L),
    NanoNetwork("Kiss", "KISS FM", 0xFFFF4500L),
    NanoNetwork("Classical", "Classical", 0xFFD4AF37L)
)

@Composable
fun HomeScreen(viewModel: RadioViewModel, onPlayStation: (Station) -> Unit) {
    val stations by viewModel.stations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedNetwork by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // ── Header ──────────────────────────────────────────────────
        Text(
            text = "Tuner",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
        )

        // ── Search Bar ──────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .height(50.dp),
            shape = RoundedCornerShape(30.dp),
            color = Color(0xFF27272A)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFFA1A1AA)
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { q ->
                        searchQuery = q
                        selectedNetwork = null
                        if (q.isBlank()) viewModel.fetchDiscover()
                        else if (q.length >= 2) viewModel.fetchNetwork(q)
                    },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 15.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Search stations…", color = Color(0x88FFFFFF), fontSize = 15.sp)
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── Nano Network Chips ──────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(NANO_NETWORKS) { network ->
                val isSelected = selectedNetwork == network.name
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(network.color).copy(alpha = 0.22f)
                    else Color(0xFF27272A),
                    modifier = Modifier
                        .height(36.dp)
                        .clickable {
                            selectedNetwork = if (isSelected) null else network.name
                            searchQuery = ""
                            if (isSelected) viewModel.fetchDiscover()
                            else viewModel.fetchNetwork(network.name)
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    ) {
                        Text(
                            text = network.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(network.color) else Color(0xFFA1A1AA)
                        )
                    }
                }
            }
        }

        // ── Section Title ───────────────────────────────────────────
        Text(
            text = selectedNetwork ?: if (searchQuery.isNotBlank()) "Results" else "Discover",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
        )

        // ── Station Grid ────────────────────────────────────────────
        if (isLoading) {
            SkeletonStationGrid()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
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
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) Color(0x339146FF) else Color(0xFF18181B),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, Color(0x669146FF)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(station.favicon)
                        .crossfade(true)
                        .build(),
                    contentDescription = station.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF27272A))
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp).offset(x = 8.dp, y = (-8).dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEC4899) else Color(0xFFA1A1AA),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = station.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActive) {
                        Text("● ", color = Color(0xFFEC4899), fontSize = 10.sp)
                    }
                    val tag = station.tags?.split(",")?.firstOrNull()?.trim()?.uppercase()
                    val location = station.countrycode ?: "GLOBAL"
                    Text(
                        text = if (tag.isNullOrEmpty()) location else "$location • $tag",
                        color = if (isActive) Color(0xFFEC4899) else Color(0xFFA1A1AA),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonStationGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(8) { 
            SkeletonCard()
        }
    }
}

@Composable
fun SkeletonCard() {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "skeleton_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(800, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF18181B),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF27272A).copy(alpha = alpha))
            )
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF27272A).copy(alpha = alpha))
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF27272A).copy(alpha = alpha))
                )
            }
        }
    }
}
