package com.tuner.repository

import com.tuner.api.RadioApi
import com.tuner.model.Station
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class StationRepository @Inject constructor(
    private val api: RadioApi
) {
    private suspend fun <T> safeApiCall(block: suspend () -> T, default: T): T {
        return try {
            withContext(Dispatchers.IO) { block() }
        } catch (e: Exception) {
            default
        }
    }

    private fun List<Station>.filterValid() =
        filter { !it.url_resolved.isNullOrEmpty() && !it.name.isNullOrEmpty() }

    suspend fun getDiscoverStations(countryCode: String? = null, language: String? = null): List<Station> {
        if (countryCode.isNullOrBlank() && language.isNullOrBlank()) {
            return safeApiCall({ api.getTopStations().filterValid() }, emptyList())
        }
        return safeApiCall({ 
            api.advancedSearch(
                countrycode = countryCode?.takeIf { it.isNotBlank() },
                language = language?.takeIf { it.isNotBlank() }
            ).filterValid() 
        }, emptyList())
    }

    suspend fun searchNetworks(query: String): List<Station> =
        safeApiCall({ api.searchStations(name = query).filterValid() }, emptyList())

    suspend fun getByGenre(tag: String): List<Station> =
        safeApiCall({ api.getByTag(tag = tag).filterValid() }, emptyList())

    suspend fun getNearby(countryCode: String): List<Station> =
        safeApiCall({ api.getByCountry(countryCode = countryCode).filterValid() }, emptyList())
}
