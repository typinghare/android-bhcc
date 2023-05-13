package us.jameschan.boardgameclock.game

/**
 * A timer controller decides how timer works.
 */
open class TimerController(
    protected open val game: Game,
    private val initialTime: HourMinuteSecond
) {
    private var timer: Timer? = null

    fun initializeTimer() {
        timer = Timer(getInitialTime(), getTimeoutCallback())
    }

    open fun getInitialTime(): HourMinuteSecond {
        return initialTime
    }

    open fun getTimeoutCallback(): () -> HourMinuteSecond? {
        return { null }
    }

    fun resume() {
        timer!!.resume()
    }

    fun pause() {
        timer!!.pause()
    }
}