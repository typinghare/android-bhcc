package us.jameschan.boardgameclock.game

import android.util.Log
import java.util.concurrent.TimeUnit
import kotlin.math.floor

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

    private fun getHour(): Int {
        return floor(ms.toDouble() / SECONDS_IN_MINUTE / MILLISECONDS_IN_SECOND / MINUTES_IN_HOUR).toInt()
    }

    private fun getMinute(): Int {
        return (floor(ms.toDouble() / SECONDS_IN_MINUTE / MILLISECONDS_IN_SECOND) % MINUTES_IN_HOUR).toInt()
    }

    private fun getSecond(): Int {
        return (floor(ms.toDouble() / MILLISECONDS_IN_SECOND) % SECONDS_IN_MINUTE).toInt()
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

    fun toFormattedString(): String {
        val second = paddingZero(getSecond())
        val minute = paddingZero(getMinute())
        val hour = paddingZero(getHour())

//        Log.d("toFormattedString:ms", ms.toString())
//        Log.d("toFormattedString:second", second.toString())
//        Log.d("toFormattedString:minute", minute.toString())
//        Log.d("toFormattedString:hour", hour.toString())

        return "$hour:$minute:$second"
    }

    private fun paddingZero(num: Int): String {
        return if (num >= 10) num.toString() else "0$num"
    }
}