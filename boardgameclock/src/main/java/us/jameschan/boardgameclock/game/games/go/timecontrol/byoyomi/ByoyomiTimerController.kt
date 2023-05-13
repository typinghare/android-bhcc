package us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.TimerController

class ByoyomiTimerController(
    override val game: Game,
    main: HourMinuteSecond,
    private val timePerPeriod: HourMinuteSecond,
    periods: Int
) : TimerController(game, main) {
    private var periodsRemaining = periods

    override fun getTimeoutCallback(): () -> HourMinuteSecond? {
        return lambda@{
            if (periodsRemaining == 0) {
                return@lambda null
            }

            periodsRemaining--
            return@lambda timePerPeriod.clone()
        }
    }
}
