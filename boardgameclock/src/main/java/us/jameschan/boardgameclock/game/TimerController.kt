package us.jameschan.boardgameclock.game

/**
 * A timer controller decides how timer works.
 */
open class TimerController(
    protected open val game: Game,
    private val initialTime: HourMinuteSecond,
    private val role: Role
) {
    private var timer: Timer? = null

    fun initializeTimer() {
        timer = Timer(getInitialTime(), getTimeoutCallback())
    }

    open fun getInitialTime(): HourMinuteSecond {
        return initialTime
    }

    open fun getTimeoutCallback(): () -> HourMinuteSecond? {
        return lambda@{
            game.clockStop(role)
            return@lambda null
        }
    }

    fun resume() {
        timer!!.resume()
    }

    fun pause() {
        timer!!.pause()
    }

    fun getTime(): HourMinuteSecond {
        return timer!!.getTime()
    }

    fun isTimerRunning(): Boolean {
        return timer!!.isRunning()
    }

    open fun getExtraNumber(): Int? {
        return null
    }
}