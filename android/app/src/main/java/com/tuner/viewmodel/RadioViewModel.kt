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

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tuner.api.GitHubApi
import com.tuner.BuildConfig

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: StationRepository,
    private val gitHubApi: GitHubApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    enum class UpdateStatus { IDLE, CHECKING, UP_TO_DATE, AVAILABLE, ERROR }

    private val _updateStatus = MutableStateFlow(UpdateStatus.IDLE)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    private val _updateAvailableUrl = MutableStateFlow<String?>(null)
    val updateAvailableUrl: StateFlow<String?> = _updateAvailableUrl.asStateFlow()

    private val _hasUpdateBadge = MutableStateFlow(false)
    val hasUpdateBadge: StateFlow<Boolean> = _hasUpdateBadge.asStateFlow()

    fun dismissUpdateDialog() { 
        _updateAvailableUrl.value = null 
        _updateStatus.value = UpdateStatus.IDLE
    }
    
    fun notifyCheckForUpdatesManual() {
        checkForUpdates(isManual = true)
    }

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

    private val _sessionDuration = MutableStateFlow(0L)
    val sessionDuration: StateFlow<Long> = _sessionDuration.asStateFlow()

    private var durationJob: kotlinx.coroutines.Job? = null

    private fun startDurationTracker() {
        durationJob?.cancel()
        _sessionDuration.value = 0L
        durationJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                if (isPlaying.value) {
                    _sessionDuration.value += 1
                }
            }
        }
    }

    private val prefs = context.getSharedPreferences("tuner_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val _preferredCountry = MutableStateFlow(prefs.getStringSet("pref_countries", emptySet()) ?: emptySet())
    val preferredCountry: StateFlow<Set<String>> = _preferredCountry.asStateFlow()
    
    private val _preferredLanguage = MutableStateFlow(prefs.getStringSet("pref_languages", emptySet()) ?: emptySet())
    val preferredLanguage: StateFlow<Set<String>> = _preferredLanguage.asStateFlow()

    private val _currentFeed = MutableStateFlow("GLOBAL")
    val currentFeed: StateFlow<String> = _currentFeed.asStateFlow()

    private val _isAdminModeEnabled = MutableStateFlow(false)
    val isAdminModeEnabled: StateFlow<Boolean> = _isAdminModeEnabled.asStateFlow()

    private val _apiLatency = MutableStateFlow(0L)
    val apiLatency: StateFlow<Long> = _apiLatency.asStateFlow()

    fun toggleAdminMode() {
        _isAdminModeEnabled.value = !_isAdminModeEnabled.value
    }

    fun updateApiLatency(latency: Long) {
        _apiLatency.value = latency
    }

    fun getMemoryUsage(): String {
        val runtime = Runtime.getRuntime()
        val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMem = runtime.maxMemory() / 1024 / 1024
        return "$usedMem MB / $maxMem MB"
    }

    fun setPreferences(countryCode: String, language: String) {
        val currentCountries = _preferredCountry.value.toMutableSet()
        val currentLanguages = _preferredLanguage.value.toMutableSet()

        if (countryCode.isNotBlank()) {
            if (currentCountries.contains(countryCode)) {
                currentCountries.remove(countryCode)
            } else {
                currentCountries.add(countryCode)
            }
        }
        
        if (language.isNotBlank()) {
            if (currentLanguages.contains(language)) {
                currentLanguages.remove(language)
            } else {
                currentLanguages.add(language)
            }
        }
        
        _preferredCountry.value = currentCountries
        _preferredLanguage.value = currentLanguages
        
        prefs.edit()
            .putStringSet("pref_countries", currentCountries)
            .putStringSet("pref_languages", currentLanguages)
            .apply()
        
        fetchDiscover()
    }

    fun setFeed(feed: String) {
        _currentFeed.value = feed
    }

    init {
        RadioService.onNextRequested = { playNext() }
        RadioService.onPrevRequested = { playPrev() }
        checkForUpdates()
        fetchDiscover()
        setupAudioRouting()
        loadFavorites()
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorite_stations", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Station>>() {}.type
                val savedList: List<Station> = gson.fromJson(json, type)
                _favoriteStations.value = savedList
                _favorites.value = savedList.map { it.stationuuid }.toSet()
            } catch (e: Exception) {
                // Ignore parsing errors
            }
        }
    }

    private fun checkForUpdates(isManual: Boolean = false) {
        if (isManual) _updateStatus.value = UpdateStatus.CHECKING
        viewModelScope.launch {
            try {
                val release = gitHubApi.getLatestRelease()
                val latestVersion = release.tag_name.replace("v", "")
                val currentVersion = BuildConfig.VERSION_NAME
                
                if (latestVersion != currentVersion) {
                    val latestParts = latestVersion.split(".").mapNotNull { it.toIntOrNull() }
                    val currentParts = currentVersion.split(".").mapNotNull { it.toIntOrNull() }
                    
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
                        if (isManual) _updateStatus.value = UpdateStatus.AVAILABLE
                        _hasUpdateBadge.value = true
                        _updateAvailableUrl.value = "https://github.com/Kirtannjoshi/Tuner./releases/latest/download/app-debug.apk"
                    } else {
                        if (isManual) _updateStatus.value = UpdateStatus.UP_TO_DATE
                        _hasUpdateBadge.value = false
                    }
                } else {
                    if (isManual) _updateStatus.value = UpdateStatus.UP_TO_DATE
                    _hasUpdateBadge.value = false
                }
            } catch (e: Exception) {
                if (isManual) _updateStatus.value = UpdateStatus.ERROR
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
            val countries = _preferredCountry.value
            val languages = _preferredLanguage.value
            
            val allStations = mutableListOf<Station>()
            
            if (countries.isEmpty() && languages.isEmpty()) {
                // FETCH GLOBAL TRENDS if no filters set
                allStations.addAll(repository.getDiscoverStations(null, null))
                allStations.addAll(repository.searchNetworks("trending"))
            } else {
                // Fetch for each country
                countries.forEach { code ->
                    allStations.addAll(repository.getDiscoverStations(code, null).take(10))
                }
                // Fetch for each language
                languages.forEach { lang ->
                    allStations.addAll(repository.getDiscoverStations(null, lang).take(10))
                }
                // Combined searches for country + language pairs if manageable
                if (countries.isNotEmpty() && languages.isNotEmpty()) {
                    countries.forEach { code ->
                        languages.forEach { lang ->
                            allStations.addAll(repository.getDiscoverStations(code, lang).take(5))
                        }
                    }
                }
            }
            
            _stations.value = allStations.distinctBy { it.stationuuid }.shuffled()
            _isLoading.value = false
            _currentFeed.value = "GLOBAL"
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
        startDurationTracker()
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
        saveFavorites()
    }

    private fun getActiveList(): List<Station> {
        val curr = _currentStation.value ?: return emptyList()
        val inStations = _stations.value.any { it.stationuuid == curr.stationuuid }
        return if (inStations) _stations.value else _favoriteStations.value
    }

    fun playNext() {
        val curr = _currentStation.value ?: return
        val list = getActiveList()
        if (list.isEmpty()) return
        val idx = list.indexOfFirst { it.stationuuid == curr.stationuuid }
        if (idx != -1) {
            val nextS = list[(idx + 1) % list.size]
            _currentStation.value = nextS
            androidx.core.content.ContextCompat.startForegroundService(context, buildPlayIntent(nextS))
        }
    }

    fun playPrev() {
        val curr = _currentStation.value ?: return
        val list = getActiveList()
        if (list.isEmpty()) return
        val idx = list.indexOfFirst { it.stationuuid == curr.stationuuid }
        if (idx != -1) {
            val prevIdx = if (idx - 1 < 0) list.size - 1 else idx - 1
            val prevS = list[prevIdx]
            _currentStation.value = prevS
            androidx.core.content.ContextCompat.startForegroundService(context, buildPlayIntent(prevS))
        }
    }

    private fun saveFavorites() {
        prefs.edit().putString("favorite_stations", gson.toJson(_favoriteStations.value)).apply()
    }
}
