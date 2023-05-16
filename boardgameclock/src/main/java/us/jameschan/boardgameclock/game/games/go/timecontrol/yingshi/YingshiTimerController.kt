package us.jameschan.boardgameclock.game.games.go.timecontrol.yingshi

import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.game.TimerController

class YingshiTimerController(
    override val game: Game,
    val role: Role,
    main: HourMinuteSecond,
    private val penalty: HourMinuteSecond,
    maxPenalty: Int
) : TimerController(game, main, role) {
    private var penaltyRemaining: Int = maxPenalty

    override fun getTimeoutCallback(): () -> HourMinuteSecond? {
        return lambda@{
            if (penaltyRemaining == 0) {
                return@lambda null
            }

            penaltyRemaining--
            return@lambda penalty.clone()
        }
    }
}