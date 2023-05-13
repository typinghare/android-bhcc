package us.jameschan.boardgameclock.game

import java.util.function.Supplier

open class TimerController(
    private val initialTime: HourMinuteSecond
) {
    private var timer: Timer? = null

    fun initializeTimer() {
        timer = Timer(getInitialTime(), getTimeoutCallback())
    }

    open fun getInitialTime(): HourMinuteSecond {
        return initialTime
    }

    open fun getTimeoutCallback(): Supplier<HourMinuteSecond?> {
        return Supplier { -> null }
    }

    fun resume() {
        timer!!.resume()
    }

    fun pause() {
        timer!!.pause()
    }
}