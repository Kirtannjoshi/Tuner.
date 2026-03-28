package com.tuner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tuner.model.Station
import com.tuner.viewmodel.RadioViewModel

@Composable
fun LibraryScreen(viewModel: RadioViewModel, onPlayStation: (Station) -> Unit) {
    val likedStations by viewModel.favoriteStations.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Text(
            text = "Library",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
        )

        if (likedStations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFFA1A1AA),
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("No favorites yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Tap ♡ on any station to save it here", color = Color(0xFFA1A1AA), fontSize = 13.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(likedStations, key = { it.stationuuid }) { station ->
                    LibraryStationCard(
                        station = station,
                        isActive = currentStation?.stationuuid == station.stationuuid,
                        onClick = { onPlayStation(station) },
                        onUnlike = { viewModel.toggleFavorite(station.stationuuid) }
                    )
                }
            }
        }
    }
}

@Composable
fun LibraryStationCard(
    station: Station,
    isActive: Boolean,
    onClick: () -> Unit,
    onUnlike: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) Color(0x339146FF) else Color(0xFF18181B),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, Color(0x669146FF)) else null,
        modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = station.favicon,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF27272A))
                )
                IconButton(
                    onClick = onUnlike,
                    modifier = Modifier.size(32.dp).offset(x = 8.dp, y = (-8).dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Unlike",
                        tint = Color(0xFFFF4081),
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
