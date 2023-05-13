package us.jameschan.boardgameclock.game.games.go.timecontrol.byoyomi;

import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.TimerController

class ByoyomiTimerController(
    private val main: HourMinuteSecond,
    private val timePerPeriod: HourMinuteSecond,
    private val periods: Int
) : TimerController(main) {

}
