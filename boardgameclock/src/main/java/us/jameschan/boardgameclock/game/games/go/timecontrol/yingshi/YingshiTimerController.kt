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
    private val maxPenalty: Int
) : TimerController(game, main, role) {
    private var penaltyTimes: Int = 0

    override fun getTimeoutCallback(): () -> HourMinuteSecond? {
        return lambda@{
            if (penaltyTimes == maxPenalty) {
                game.clockStop(role)
                return@lambda null
            }

            penaltyTimes++
            return@lambda penalty.clone()
        }
    }

    override fun getExtraNumber(): Int {
        return penaltyTimes
    }
}