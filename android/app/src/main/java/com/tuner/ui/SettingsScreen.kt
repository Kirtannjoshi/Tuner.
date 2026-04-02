package com.tuner.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import com.tuner.viewmodel.RadioViewModel

@Composable
fun SettingsScreen(viewModel: RadioViewModel, onNavigateBack: () -> Unit) {
    val preferredCountries by viewModel.preferredCountry.collectAsState()
    val preferredLanguage by viewModel.preferredLanguage.collectAsState()
    val updateAvailableUrl by viewModel.updateAvailableUrl.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val updateUrl = updateAvailableUrl // Alias for compatibility with rest of the file
    val isAdminModeEnabled by viewModel.isAdminModeEnabled.collectAsState()
    val apiLatency by viewModel.apiLatency.collectAsState()
    var tapCount by remember { mutableIntStateOf(0) }

    val countries = listOf("" to "Global", "US" to "United States", "GB" to "United Kingdom", "IN" to "India", "CA" to "Canada", "AU" to "Australia", "DE" to "Germany", "FR" to "France")
    val languages = listOf("" to "Any Language", "english" to "English", "spanish" to "Spanish", "hindi" to "Hindi", "french" to "French", "german" to "German")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        // --- HEADER ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.padding(start = 8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            // --- REGION (MULTI-SELECT) ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Preferred Regions (Multi-select)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA1A1AA))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(countries) { (code, name) ->
                        val isSelected = if (code.isEmpty()) preferredCountries.isEmpty() else preferredCountries.contains(code)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF9146FF) else Color(0xFF27272A),
                            border = if (isSelected) BorderStroke(1.dp, Color.White.copy(alpha=0.2f)) else null,
                            modifier = Modifier.clickable { 
                                if (code.isEmpty()) {
                                    // Clear all if "Global" clicked or if it was already selected
                                    preferredCountries.forEach { viewModel.setPreferences(it, "") }
                                } else {
                                    viewModel.setPreferences(code, "")
                                }
                            }
                        ) {
                            Text(name, color = Color.White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                        }
                    }
                }
            }

            // --- LANGUAGE (MULTI-SELECT) ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Preferred Languages (Multi-select)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA1A1AA))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(languages) { (code, name) ->
                        val isSelected = if (code.isEmpty()) preferredLanguage.isEmpty() else preferredLanguage.contains(code)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFEC4899) else Color(0xFF27272A),
                            border = if (isSelected) BorderStroke(1.dp, Color.White.copy(alpha=0.2f)) else null,
                            modifier = Modifier.clickable { 
                                if (code.isEmpty()) {
                                    // Clear all if "Any Language" clicked
                                    preferredLanguage.forEach { viewModel.setPreferences("", it) }
                                } else {
                                    viewModel.setPreferences("", code)
                                }
                            }
                        ) {
                            Text(name, color = Color.White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                        }
                    }
                }
            }

            // --- UPDATE STATUS ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Application Status", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA1A1AA))
                
                val lastChecked by viewModel.lastCheckedTimestamp.collectAsState()
                
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E1E22),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Check for Updates", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                lastChecked?.let {
                                    Text(it, color = Color(0xFF71717A), fontSize = 12.sp)
                                }
                            }
                            
                            if (updateStatus == RadioViewModel.UpdateStatus.CHECKING) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF9146FF), strokeWidth = 2.dp)
                            } else {
                                TextButton(onClick = { viewModel.notifyCheckForUpdatesManual() }) {
                                    Text("CHECK NOW", color = Color(0xFF9146FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        if (updateStatus == RadioViewModel.UpdateStatus.AVAILABLE && updateUrl != null) {
                            Spacer(Modifier.height(20.dp))
                            
                            // Important explanation for the user
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF9146FF).copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, Color(0xFF9146FF).copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Software Update Available", 
                                        color = Color.White, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "Installing the update will automatically replace the current version. Your favorites and settings will remain safe.",
                                        color = Color(0xFFA1A1AA),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            val context = androidx.compose.ui.platform.LocalContext.current
                            Button(
                                onClick = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(updateUrl))) },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                            ) {
                                Text("Download & Install Update", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (updateStatus == RadioViewModel.UpdateStatus.UP_TO_DATE) {
                            Spacer(Modifier.height(12.dp))
                            Text("🎉 You are using the latest version of Tuner.", color = Color(0xFF22C55E), fontSize = 13.sp)
                        } else if (updateStatus == RadioViewModel.UpdateStatus.ERROR) {
                            Spacer(Modifier.height(12.dp))
                            Text("Unable to reach GitHub. Please check your connection.", color = Color(0xFFEF4444), fontSize = 13.sp)
                        }
                    }
                }
            }

            // --- ADMIN / DEVELOPER DASHBOARD ---
            androidx.compose.animation.AnimatedVisibility(visible = isAdminModeEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Developer Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEC4899))
                    
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF1E1E22),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Memory Heap Usage", color = Color.White, fontSize = 14.sp)
                                Text(viewModel.getMemoryUsage(), color = Color(0xFFA1A1AA), fontSize = 14.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Network Latency", color = Color.White, fontSize = 14.sp)
                                Text("${apiLatency}ms", color = Color(0xFFA1A1AA), fontSize = 14.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("System Architecture", color = Color.White, fontSize = 14.sp)
                                Text(android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown", color = Color(0xFFA1A1AA), fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // --- ABOUT & VISION ---
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("About Tuner", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA1A1AA))
                
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E1E22),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF9146FF).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📻", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("What is Tuner?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text(
                                "A high-fidelity streaming engine aggregating over 40,000 global stations for a borderless listening experience.",
                                color = Color(0xFFA1A1AA), fontSize = 13.sp, lineHeight = 18.sp
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E1E22),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEC4899).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌍", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("The Vision", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text(
                                "Bridging global cultures through sound. Discover the heartbeat of any country with a single tap.",
                                color = Color(0xFFA1A1AA), fontSize = 13.sp, lineHeight = 18.sp
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E1E22),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val hasUpdate by viewModel.hasUpdateBadge.collectAsState()
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Contact Support", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (hasUpdate) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(modifier = Modifier.size(6.dp).background(Color(0xFFEC4899), CircleShape))
                                }
                            }
                            Text("Email ID coming soon", color = Color(0xFF71717A), fontSize = 12.sp)
                        }
                        Text(
                            text = "V${com.tuner.BuildConfig.VERSION_NAME} BETA", 
                            color = Color(0xFF52525B), 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                tapCount++
                                if (tapCount >= 7) {
                                    viewModel.toggleAdminMode()
                                    tapCount = 0
                                }
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
