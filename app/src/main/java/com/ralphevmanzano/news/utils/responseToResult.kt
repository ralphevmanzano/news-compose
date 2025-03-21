package com.ralphevmanzano.news.utils

import com.ralphevmanzano.news.data.remote.dto.ErrorDto
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import com.ralphevmanzano.news.domain.model.networking.Result

/**
 * Converts a [Response] to a [Result] with the success body or a [NetworkError].
 * This function can be extended to handle more error codes and error messages as needed.
 */
inline fun <reified T> responseToResult(
    response: Response<T>
): Result<T, NetworkError> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body == null) {
            Result.Error(NetworkError.Unknown)
        } else {
            Result.Success(body)
        }
    } else {
        when (response.code()) {
            400 -> Result.Error(NetworkError.InvalidRequest)
            401 -> {
                // We can also get the error message from the response body when needed and pass it to the NetworkError
                // if necessary for more detailed error handling, like in the example below
                val apiError = if (response.errorBody() != null) {
                    try {
                        Json.decodeFromString<ErrorDto>(response.errorBody()!!.string())
                    } catch (e: SerializationException) {
                        e.printStackTrace()
                        return Result.Error(NetworkError.SerializationError)
                    }
                } else {
                    return Result.Error(NetworkError.Unknown)
                }
                Result.Error(NetworkError.AuthenticationFailed(apiError.message))
            }
            408 -> Result.Error(NetworkError.Timeout)
            429 -> Result.Error(NetworkError.TooManyRequests)
            500 -> Result.Error(NetworkError.InternalServerError)
            else -> Result.Error(NetworkError.Unknown)
        }
    }
}