package us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.game.TimerController

class ByoyomiTimerController(
    override val game: Game,
    val role: Role,
    main: HourMinuteSecond,
    private val timePerPeriod: HourMinuteSecond,
    periods: Int
) : TimerController(game, main, role) {
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
