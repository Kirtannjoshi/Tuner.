package com.tuner.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tuner.model.Station

@Composable
fun PlayerBar(
    station: Station,
    isPlaying: Boolean,
    isFavorite: Boolean,
    audioOutput: String = "SPEAKER",
    onPlayPause: () -> Unit,
    onToggleFavorite: () -> Unit,
    onExpand: () -> Unit
) {
    Surface(
        color = Color(0xFF1E1E1E), // Slightly lighter than standard dark to stand out from bottom bar
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onExpand
            )
    ) {
        AnimatedContent(
            targetState = station,
            transitionSpec = {
                (fadeIn(tween(300)) + slideInVertically { it / 6 }) togetherWith 
                (fadeOut(tween(300)) + slideOutVertically { -it / 6 })
            },
            label = "player_bar_station_transition"
        ) { targetStation ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(targetStation.favicon)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF27272A))
                        .then(
                            if (isPlaying) Modifier.shadow(8.dp, CircleShape, spotColor = Color(0xFFEC4899))
                            else Modifier
                        )
                )

                Spacer(Modifier.width(14.dp))

                // Station info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = targetStation.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isBluetooth = audioOutput.startsWith("BLUETOOTH")
                        val isWired = audioOutput.startsWith("WIRED")
                        val deviceName = audioOutput.substringAfter("|", "")
                        
                        val prefix = when {
                            isBluetooth -> "🎧 "
                            isWired -> "🔌 "
                            isPlaying -> "● "
                            else -> ""
                        }
                        
                        if (prefix.isNotEmpty()) {
                            Text(prefix, color = Color(0xFFEC4899), fontSize = if (prefix == "● ") 10.sp else 12.sp)
                        }

                        val tagStr = targetStation.tags?.split(",")?.firstOrNull()?.trim()?.uppercase() ?: "LIVE RADIO"
                        val displayTxt = if (deviceName.isNotBlank() && deviceName != "Phone Speaker") "$deviceName • $tagStr" else tagStr
                        
                        Text(
                            text = displayTxt,
                            color = if (isPlaying) Color(0xFFEC4899) else Color(0xFFA1A1AA),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Spring bouncy Favorite
                val heartScale by animateFloatAsState(
                    targetValue = if (isFavorite) 1.25f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "heart_scale"
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEC4899) else Color(0xFFA1A1AA),
                        modifier = Modifier.scale(heartScale).size(26.dp)
                    )
                }

                Spacer(Modifier.width(4.dp))

                val playInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val isPlayPressed by playInteraction.collectIsPressedAsState()
                val playScale by animateFloatAsState(
                    targetValue = if (isPlayPressed) 0.85f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "play_scale"
                )

                // Play/Pause Morph
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .scale(playScale)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(
                            interactionSource = playInteraction,
                            indication = androidx.compose.foundation.LocalIndication.current,
                            onClick = onPlayPause
                        )
                ) {
                    AnimatedContent(
                        targetState = isPlaying,
                        transitionSpec = {
                            (fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.5f))
                                .togetherWith(fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.5f))
                        },
                        label = "play_pause"
                    ) { playing ->
                        Icon(
                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playing) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
