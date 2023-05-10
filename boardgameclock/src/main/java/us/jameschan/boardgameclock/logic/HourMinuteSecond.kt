package us.jameschan.boardgameclock.logic

import java.util.concurrent.TimeUnit
import kotlin.math.floor

@Suppress("JoinDeclarationAndAssignment")
class HourMinuteSecond(
    private val ms: Int
) : Cloneable {
    companion object {
        private val MILLISECONDS_IN_SECOND: Int = TimeUnit.SECONDS.toMillis(1).toInt();
        private val SECONDS_IN_MINUTE: Int = TimeUnit.MINUTES.toSeconds(1).toInt();
        private val MINUTES_IN_HOUR: Int = TimeUnit.HOURS.toMinutes(1).toInt();
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
}