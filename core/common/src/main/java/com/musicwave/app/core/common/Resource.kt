package com.musicwave.app.core.common

/**
 * A generic sealed class for handling API/repository responses.
 * Wraps data with success, error, and loading states.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    /**
     * Success state with data.
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Error state with optional data and error message.
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Loading state.
     */
    class Loading<T> : Resource<T>()
}
