package com.ralphevmanzano.news.domain.model.networking

sealed interface NetworkError: Error {
    data object NoInternetConnection: NetworkError
    data object Timeout: NetworkError
    data object TooManyRequests: NetworkError
    data object Unknown: NetworkError
    data object SerializationError: NetworkError
    data class AuthenticationFailed(val message: String): NetworkError
    data object InternalServerError: NetworkError
    data object InvalidRequest: NetworkError
}