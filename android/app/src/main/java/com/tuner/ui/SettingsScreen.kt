package com.tuner.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.tuner.viewmodel.RadioViewModel

@Composable
fun SettingsScreen(viewModel: RadioViewModel, onNavigateBack: () -> Unit) {
    val preferredCountries by viewModel.preferredCountry.collectAsState()
    val preferredLanguage by viewModel.preferredLanguage.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val updateUrl by viewModel.updateAvailableUrl.collectAsState()

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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Application Status", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA1A1AA))
                Button(
                    onClick = { viewModel.notifyCheckForUpdatesManual() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                    enabled = updateStatus != RadioViewModel.UpdateStatus.CHECKING
                ) {
                    when (updateStatus) {
                        RadioViewModel.UpdateStatus.IDLE -> Text("Check for Application Updates", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        RadioViewModel.UpdateStatus.CHECKING -> {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                            Text("Checking GitHub...", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        RadioViewModel.UpdateStatus.UP_TO_DATE -> Text("App is up to date! 🎉", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        RadioViewModel.UpdateStatus.AVAILABLE -> Text("New Version Available", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        RadioViewModel.UpdateStatus.ERROR -> Text("Check Failed", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (updateStatus == RadioViewModel.UpdateStatus.AVAILABLE && updateUrl != null) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(updateUrl))) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                    ) {
                        Text("Download Update", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("What is Tuner?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tuner is a high-fidelity, community-driven radio streaming platform designed to bridge the gap between global cultures through sound. We aggregate over 40,000 stations to bring the world to your pocket.",
                            color = Color(0xFFA1A1AA), fontSize = 14.sp, lineHeight = 20.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E1E22),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Our Vision", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "We believe that music and talk radio are the purest forms of human expression. Our vision is to create a borderless listening experience where anyone, anywhere, can discover the heartbeat of a different country with a single tap.",
                            color = Color(0xFFA1A1AA), fontSize = 14.sp, lineHeight = 20.sp
                        )
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
                            Text("Contact Support", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Email ID coming soon", color = Color(0xFF71717A), fontSize = 12.sp)
                        }
                        Text("V1.2 BETA", color = Color(0xFF52525B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
