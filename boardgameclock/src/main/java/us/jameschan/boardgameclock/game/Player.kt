package us.jameschan.boardgameclock.game;

class Player(
    private val timeControl: TimeControl
) {
    private val timerController: TimerController = timeControl.getTimerController()

    init {
        timerController.initializeTimer()
    }

    fun getTimerController(): TimerController {
        return timerController
    }
}
