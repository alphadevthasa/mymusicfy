package com.musicwave.app.core.common.util

/**
 * Application-wide constants.
 */
object Constants {
    const val BASE_URL = "https://api.deezer.com/"

    const val DEFAULT_PAGE_SIZE = 20

    const val STARTING_PAGE_INDEX = 0

    const val NETWORK_TIMEOUT = 30L

    const val DATABASE_NAME = "musicwave_db"

    const val DATABASE_VERSION = 1

    const val PREFERENCES_NAME = "musicwave_preferences"

    const val CACHE_MAX_AGE = 60 * 60 * 1000L

    const val CACHE_MAX_STALE = 7 * 24 * 60 * 60 * 1000L
}
