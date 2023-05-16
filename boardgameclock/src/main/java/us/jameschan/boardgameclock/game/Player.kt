package us.jameschan.boardgameclock.game

class Player(
    private val timeControl: TimeControl
) {
    private var timerController: TimerController? = null

    init {
        timeControl.initialize()
    }

    fun initTimerController() {
        timerController = timeControl.getTimerController()
    }

    fun getTimerController(): TimerController {
        return timerController!!
    }

    fun getTimeControl(): TimeControl {
        return timeControl
    }
}
