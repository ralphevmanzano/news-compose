package com.ralphevmanzano.news.utils

import android.content.Context
import com.ralphevmanzano.news.R
import com.ralphevmanzano.news.domain.model.networking.NetworkError

/**
 * Converts a [NetworkError] to a string for display to the user.
 */
fun NetworkError.toString(context: Context): String {
    return when (this) {
        is NetworkError.AuthenticationFailed -> this.message
        NetworkError.InternalServerError -> context.getString(R.string.unexpected_error)
        NetworkError.InvalidRequest -> context.getString(R.string.invalid_request)
        NetworkError.NoInternetConnection -> context.getString(R.string.no_internet_connection)
        NetworkError.SerializationError -> context.getString(R.string.serialization_error)
        NetworkError.Timeout -> context.getString(R.string.request_timed_out)
        NetworkError.TooManyRequests -> context.getString(R.string.too_many_request_error)
        NetworkError.Unknown -> context.getString(R.string.unexpected_error)
    }
}