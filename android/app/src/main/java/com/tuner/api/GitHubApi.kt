package com.tuner.api

import retrofit2.http.GET

data class GitHubReleaseResponse(
    val tag_name: String
)

interface GitHubApi {
    @GET("repos/Kirtannjoshi/Tuner/releases/latest")
    suspend fun getLatestRelease(): GitHubReleaseResponse
}
