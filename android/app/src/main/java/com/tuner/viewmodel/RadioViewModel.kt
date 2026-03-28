package com.tuner.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuner.model.Station
import com.tuner.repository.StationRepository
import com.tuner.service.RadioService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.tuner.api.GitHubApi
import com.tuner.BuildConfig

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: StationRepository,
    private val gitHubApi: GitHubApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _updateAvailableUrl = MutableStateFlow<String?>(null)
    val updateAvailableUrl: StateFlow<String?> = _updateAvailableUrl.asStateFlow()

    fun dismissUpdateDialog() { _updateAvailableUrl.value = null }

    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentStation = MutableStateFlow<Station?>(null)
    val currentStation: StateFlow<Station?> = _currentStation.asStateFlow()

    // Real playback state from the service, no binding needed
    val isPlaying: StateFlow<Boolean> = RadioService.isPlaying

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    // Persists full Station objects so Library doesn't lose data on navigation
    private val _favoriteStations = MutableStateFlow<List<Station>>(emptyList())
    val favoriteStations: StateFlow<List<Station>> = _favoriteStations.asStateFlow()

    // Audio Output Tracking
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    private val _audioOutput = MutableStateFlow("SPEAKER")
    val audioOutput: StateFlow<String> = _audioOutput.asStateFlow()

    init {
        checkForUpdates()
        fetchDiscover()
        setupAudioRouting()
    }

    private fun checkForUpdates() {
        viewModelScope.launch {
            try {
                val release = gitHubApi.getLatestRelease()
                val latestVersion = release.tag_name.replace("v", "")
                val currentVersion = BuildConfig.VERSION_NAME
                
                if (latestVersion != currentVersion) {
                    val latestParts = latestVersion.split(".").map { it.toIntOrNull() ?: 0 }
                    val currentParts = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
                    
                    var isNewer = false
                    for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
                        val lat = latestParts.getOrElse(i) { 0 }
                        val cur = currentParts.getOrElse(i) { 0 }
                        if (lat > cur) {
                            isNewer = true
                            break
                        } else if (lat < cur) {
                            break
                        }
                    }
                    
                    if (isNewer) {
                        _updateAvailableUrl.value = "https://github.com/Kirtannjoshi/Tuner/releases/latest/download/app-debug.apk"
                    }
                }
            } catch (e: Exception) {
                // Ignore API failures gracefully
            }
        }
    }

    private fun setupAudioRouting() {
        val callback = object : android.media.AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out android.media.AudioDeviceInfo>?) { updateAudioOutput() }
            override fun onAudioDevicesRemoved(removedDevices: Array<out android.media.AudioDeviceInfo>?) { updateAudioOutput() }
        }
        audioManager.registerAudioDeviceCallback(callback, null)
        updateAudioOutput()
    }

    private fun updateAudioOutput() {
        val devices = audioManager.getDevices(android.media.AudioManager.GET_DEVICES_OUTPUTS)
        var output = "SPEAKER"
        var deviceName = "Phone Speaker"
        
        for (device in devices) {
            when (device.type) {
                android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                android.media.AudioDeviceInfo.TYPE_BLE_HEADSET,
                android.media.AudioDeviceInfo.TYPE_BLE_SPEAKER -> {
                    output = "BLUETOOTH"
                    deviceName = device.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Bluetooth Device"
                    break
                }
                android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET,
                android.media.AudioDeviceInfo.TYPE_USB_HEADSET -> {
                    if (output != "BLUETOOTH") {
                        output = "WIRED"
                        deviceName = "Wired Headphones"
                    }
                }
            }
        }
        _audioOutput.value = "$output|$deviceName"
    }

    fun fetchDiscover() {
        viewModelScope.launch {
            _isLoading.value = true
            _stations.value = repository.getDiscoverStations()
            _isLoading.value = false
        }
    }

    private var searchJob: kotlinx.coroutines.Job? = null

    fun fetchNetwork(name: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(400) // Standard robust debounce
            _isLoading.value = true
            _stations.value = repository.searchNetworks(name)
            _isLoading.value = false
        }
    }

    fun fetchByGenre(tag: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _stations.value = repository.getByGenre(tag)
            _isLoading.value = false
        }
    }

    /**
     * Called from Activity (not ViewModel) to start the service safely.
     * Returns the intent to start — Activity calls startService() with Activity context.
     */
    fun buildPlayIntent(station: Station): Intent {
        val url = station.url_resolved?.takeIf { it.isNotBlank() } ?: station.url
        return Intent(context, RadioService::class.java).apply {
            action = RadioService.ACTION_PLAY
            putExtra(RadioService.EXTRA_STREAM_URL, url)
            putExtra(RadioService.EXTRA_STATION_NAME, station.name)
            putExtra("EXTRA_FAVICON_URL", station.favicon)
            putExtra("EXTRA_STATION_TAGS", station.tags?.split(",")?.firstOrNull()?.trim()?.uppercase() ?: "LIVE RADIO")
        }
    }

    fun selectStation(station: Station): Boolean {
        if (_currentStation.value?.stationuuid == station.stationuuid) {
            // Same station — send toggle via normal startService
            context.startService(
                Intent(context, RadioService::class.java).apply {
                    action = RadioService.ACTION_TOGGLE
                }
            )
            return false
        }
        _currentStation.value = station
        // Playback is started by MainActivity via startForegroundService with Activity context
        return true
    }

    fun togglePlayPause() {
        context.startService(
            Intent(context, RadioService::class.java).apply {
                action = RadioService.ACTION_TOGGLE
            }
        )
    }

    fun toggleFavorite(stationUuid: String) {
        val currentFavs = _favorites.value
        if (currentFavs.contains(stationUuid)) {
            _favorites.value = currentFavs - stationUuid
            _favoriteStations.value = _favoriteStations.value.filter { it.stationuuid != stationUuid }
        } else {
            val station = _stations.value.find { it.stationuuid == stationUuid }
                ?: _currentStation.value?.takeIf { it.stationuuid == stationUuid }
            if (station != null) {
                _favorites.value = currentFavs + stationUuid
                _favoriteStations.value = listOf(station) + _favoriteStations.value
            }
        }
    }
}
