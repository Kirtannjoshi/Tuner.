package com.tuner.model

data class Station(
    val stationuuid: String,
    val name: String,
    val url: String,
    val url_resolved: String?,
    val favicon: String?,
    val country: String?,
    val countrycode: String?,
    val tags: String?
)
