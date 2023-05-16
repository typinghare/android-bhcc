package us.jameschan.boardgameclock

import org.junit.Test
import us.jameschan.boardgameclock.game.HourMinuteSecond

class TimeTest {
    @Test
    fun hourMinuteSecondTest() {
        val time = HourMinuteSecond(3500000)
        val time2 = HourMinuteSecond.parse("10 min")

        println(time)
        println(time.toFormattedString())

        println(time2)
        println(time2.toFormattedString())
    }
}