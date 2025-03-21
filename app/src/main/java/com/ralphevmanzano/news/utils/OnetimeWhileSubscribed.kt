package com.ralphevmanzano.news.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transformLatest

/**
 * A [SharingStarted] strategy that starts sharing once there is at least one subscriber and stops
 */
class OnetimeWhileSubscribed(
    private val stopTimeout: Long,
    private val replayExpiration: Long = Long.MAX_VALUE,
) : SharingStarted {

    // Add a way to reset the collection state
    private val resetTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val hasCollected: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        require(stopTimeout >= 0) { "stopTimeout($stopTimeout ms) cannot be negative" }
        require(replayExpiration >= 0) { "replayExpiration($replayExpiration ms) cannot be negative" }
    }

    // Add a function to reset the collection state
    fun reset() {
        hasCollected.value = false
        resetTrigger.tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> =
        merge(
            resetTrigger.map { SharingCommand.STOP },
            combine(hasCollected, subscriptionCount) { collected, counts ->
                collected to counts
            }.transformLatest { pair ->
                val (collected, count) = pair
                if (count > 0 && !collected) {
                    emit(SharingCommand.START)
                    hasCollected.value = true
                } else if (count > 0) {
                    // Just stay started while we have subscribers
                    emit(SharingCommand.START)
                } else {
                    delay(stopTimeout)
                    if (replayExpiration > 0) {
                        emit(SharingCommand.STOP)
                        delay(replayExpiration)
                    }
                }
            }
        ).dropWhile {
            it != SharingCommand.START
        } // don't emit any STOP/RESET_BUFFER to start with, only START
            .distinctUntilChanged() // just in case somebody forgets it, don't leak our multiple sending of START
}