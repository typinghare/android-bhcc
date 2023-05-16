package us.jameschan.boardgameclock.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import us.jameschan.boardgameclock.GameManager
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.util.setInterval

class ClockActivity : AppCompatActivity() {
    companion object {
        val TEXT_COLOR_RUNNING = Color.Black.toArgb()
        val TEXT_COLOR_PAUSED = Color.Gray.toArgb()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        val textClockA: TextView = findViewById(R.id.text_clock_a)
        val textClockB: TextView = findViewById(R.id.text_clock_b)
        val textExtraNumberA: TextView = findViewById(R.id.text_extra_number_a)
        val textExtraNumberB: TextView = findViewById(R.id.text_extra_number_b)

        val game = GameManager.getGame()
        val playerA = game.getPlayer(Role.A)
        val playerB = game.getPlayer(Role.B)
        val playerATimerController = playerA.getTimerController()
        val playerBTimerController = playerB.getTimerController()

        setInterval(100) {
            // Updates time every 100 milliseconds.
            val playerATime: HourMinuteSecond = playerATimerController.getTime()
            val playerBTime: HourMinuteSecond = playerBTimerController.getTime()

            val isPlayerAClockRunning: Boolean = playerATimerController.isTimerRunning()
            val isPlayerBClockRunning: Boolean = playerBTimerController.isTimerRunning()

            runOnUiThread {
                textClockA.text = playerATime.toFormattedString()
                textClockB.text = playerBTime.toFormattedString()

                if (isPlayerAClockRunning) {
                    textClockA.setTextColor(TEXT_COLOR_RUNNING)
                } else {
                    textClockA.setTextColor(TEXT_COLOR_PAUSED)
                }

                if (isPlayerBClockRunning) {
                    textClockB.setTextColor(TEXT_COLOR_RUNNING)
                } else {
                    textClockB.setTextColor(TEXT_COLOR_PAUSED)
                }
            }
        }

        textClockA.setOnClickListener {
            Log.d("Clock:onClick", "A")
            game.playerClickEvent(Role.A)
        }

        textClockB.setOnClickListener {
            Log.d("Clock:onClick", "B")
            game.playerClickEvent(Role.B)
        }
    }
}