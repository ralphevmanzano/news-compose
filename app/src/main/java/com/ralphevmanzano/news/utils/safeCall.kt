package com.ralphevmanzano.news.utils

import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.Result
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.coroutineContext

/**
 * Safely calls a network request and returns a [Result] with the success body or a [NetworkError].
 * This function checks for IOException and SerializationException which are errors that occurs
 * before getting the network response.
 */
suspend inline fun <reified T> safeCall(call: () -> Response<T>): Result<T, NetworkError> {
    val response: Response<T> = try {
        call()
    } catch (e: IOException) {
        return Result.Error(NetworkError.NoInternetConnection)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(NetworkError.SerializationError)
    }
    catch (e: Exception) {
        // CancellationException might be caught here
        // ensureActive() makes sure it's thrown if the coroutine is cancelled
        // so that the coroutine is cancelled properly
        coroutineContext.ensureActive()
        e.printStackTrace()
        return Result.Error(NetworkError.Unknown)
    }

    return responseToResult(response)
}