package us.jameschan.boardgameclock.logic

import java.util.Timer
import java.util.TimerTask

/**
 * Equivalent `setInterval` function.
 */
fun setInterval(ms: Number, handler: () -> Unit): Timer {
    return Timer().apply {
        scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler()
            }
        }, 0, ms.toLong())
    }
}

/**
 * Equivalent `setTimeout` function.
 */
fun setTimeout(ms: Number, handler: () -> Unit): Timer {
    return Timer().apply {
        schedule(object : TimerTask() {
            override fun run() {
                handler()
            }
        }, ms.toLong())
    }
}