package us.jameschan.boardgameclock.game

import java.util.concurrent.TimeUnit
import kotlin.math.floor

@Suppress("JoinDeclarationAndAssignment")
class HourMinuteSecond(
    private val ms: Int
) : Cloneable {
    companion object {
        private val MILLISECONDS_IN_SECOND: Int = TimeUnit.SECONDS.toMillis(1).toInt()
        private val SECONDS_IN_MINUTE: Int = TimeUnit.MINUTES.toSeconds(1).toInt()
        private val MINUTES_IN_HOUR: Int = TimeUnit.HOURS.toMinutes(1).toInt()
        private val MILLISECONDS_IN_MINUTE: Int = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE
        private val MILLISECONDS_IN_HOUR: Int = MILLISECONDS_IN_MINUTE * MINUTES_IN_HOUR

        fun of(hour: Int, minute: Int, second: Int): HourMinuteSecond {
            return HourMinuteSecond(hour * MILLISECONDS_IN_HOUR + minute * MILLISECONDS_IN_MINUTE + second * MILLISECONDS_IN_SECOND)
        }

        fun parse(string: String): HourMinuteSecond {
            val sp = string.trim().split(" ")
            val type: String = sp[1]
            val num: Int = sp[0].toInt()

            return when (type) {
                "hour" -> of(num, 0, 0)
                "min" -> of(0, num, 0)
                "sec" -> of(0, 0, num)
                else -> of(0, 0, 0)
            }
        }
    }

    private val hour: Int
    private val minute: Int
    private val second: Int

    init {
        second = floor(ms.toDouble() / MILLISECONDS_IN_SECOND).toInt()
        minute = floor(second.toDouble() / SECONDS_IN_MINUTE).toInt()
        hour = floor(minute.toDouble() / MINUTES_IN_HOUR).toInt()
    }

    fun getHour(): Int {
        return hour
    }

    fun getMinute(): Int {
        return minute
    }

    fun getSecond(): Int {
        return second
    }

    fun getMs(): Int {
        return ms
    }

    public override fun clone(): HourMinuteSecond {
        return HourMinuteSecond(ms)
    }

    override fun toString(): String {
        return "Time($ms ms)"
    }
}