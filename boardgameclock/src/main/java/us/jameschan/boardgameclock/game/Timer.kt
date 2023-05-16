package us.jameschan.boardgameclock.game

import us.jameschan.boardgameclock.util.setInterval
import us.jameschan.boardgameclock.util.setTimeout
import java.util.Date
import java.util.Timer
import java.util.function.Supplier

/**
 * Game clock.
 */
class Timer(
    private var time: HourMinuteSecond,
    private val timeoutCallback: Supplier<HourMinuteSecond?>
) {
    companion object {
        private const val UPDATE_INTERVAL: Int = 50
    }

    private var intervalTimer: Timer? = null
    private var timeoutTimer: Timer? = null
    private var updatedTimestamp: Long? = null

    /**
     * Gets timer.
     */
    fun getTime(): HourMinuteSecond {
        return time.clone()
    }

    /**
     * Sets a timer.
     */
    private fun setTime(time: HourMinuteSecond) {
        this.time = time.clone()
    }

    fun isRunning(): Boolean {
        return intervalTimer != null
    }

    /**
     * Resumes this timer.
     */
    fun resume() {
        println("Timer resume: $time")

        pause()

        intervalTimer = setInterval(UPDATE_INTERVAL) {
            consume(UPDATE_INTERVAL)
            updatedTimestamp = Date().time
        }

        // Timeout.
        timeoutTimer = setTimeout(time.getMs()) {
            clearAll()

            // Invoke timeout callback.
            val hourMinuteSecond: HourMinuteSecond? = timeoutCallback.get()
            if (hourMinuteSecond != null) {
                setTime(hourMinuteSecond)
                resume()
            }
        }
    }

    /**
     * Pauses this timer.
     */
    fun pause() {
        // Consume excessive time.
        if (updatedTimestamp != null) {
            consume((Date().time - updatedTimestamp!!).toInt())
        }

        clearAll()
    }

    /**
     * Consumes time.
     */
    private fun consume(timeToConsume: Int) {
        val ms: Int = time.getMs() - timeToConsume

        setTime(HourMinuteSecond(if (ms < 0) 0 else ms))
    }

    /**
     * Clears updatedTimestamp, intervalTimer, and timeoutTimer.
     */
    private fun clearAll() {
        updatedTimestamp = null

        if (intervalTimer != null) {
            intervalTimer!!.cancel()
            intervalTimer = null
        }

        if (timeoutTimer != null) {
            timeoutTimer!!.cancel()
            timeoutTimer = null
        }
    }
}