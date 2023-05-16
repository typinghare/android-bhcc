package us.jameschan.boardgameclock.game

class Player(
    private val timeControl: TimeControl
) {
    private val timerController: TimerController = timeControl.getTimerController()

    init {
        timeControl.initialize()
        timerController.initializeTimer()
    }

    fun getTimerController(): TimerController {
        return timerController
    }

    fun getTimeControl(): TimeControl {
        return timeControl
    }
}
