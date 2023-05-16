package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import us.jameschan.boardgameclock.GameManager
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.HourMinuteSecond
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.util.setInterval

class ClockActivity : AppCompatActivity() {
    companion object {
        val TEXT_COLOR_RUNNING = Color.Black.toArgb()
        val TEXT_COLOR_PAUSED = Color.Gray.toArgb()
        val TEXT_COLOR_STOP = Color.Red.toArgb()
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

            val playerAExtraNumber: Int? = playerATimerController.getExtraNumber()
            val playerBExtraNumber: Int? = playerBTimerController.getExtraNumber()

            val gameStopped: Boolean = game.isClockStop()

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

                if (playerAExtraNumber == null) {
                    textExtraNumberA.visibility = View.INVISIBLE
                } else {
                    textExtraNumberA.visibility = View.VISIBLE
                    textExtraNumberA.text = playerAExtraNumber.toString()
                }

                if (playerBExtraNumber == null) {
                    textExtraNumberB.visibility = View.INVISIBLE
                } else {
                    textExtraNumberB.visibility = View.VISIBLE
                    textExtraNumberB.text = playerBExtraNumber.toString()
                }

                if (gameStopped) {
                    it.cancel()
                    val stopRole: Role = game.clockStopRole()
                    if (stopRole == Role.A) {
                        textClockA.setTextColor(TEXT_COLOR_STOP)
                        textClockB.setTextColor(TEXT_COLOR_PAUSED)
                    } else {
                        textClockB.setTextColor(TEXT_COLOR_STOP)
                        textClockA.setTextColor(TEXT_COLOR_PAUSED)
                    }
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

        textClockA.setOnLongClickListener {
            longClick(game)

            true
        }

        textClockB.setOnLongClickListener {
            longClick(game)

            true
        }
    }

    private fun longClick(game: Game) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Exit Game")
        alertDialogBuilder.setMessage("Do you want to exit this game?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            game.close()
            startActivity(Intent(this, MainActivity::class.java))
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}