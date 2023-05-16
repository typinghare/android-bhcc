package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import us.jameschan.boardgameclock.GameManager
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.fragment.NavigationFragment
import us.jameschan.boardgameclock.activity.fragment.SettingsListFragment
import us.jameschan.boardgameclock.game.Game
import us.jameschan.boardgameclock.game.Role
import us.jameschan.boardgameclock.game.games.go.ChessGame
import us.jameschan.boardgameclock.game.games.go.GoGame

class GameSettingsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_TIME_CONTROL = "EXTRA_KEY_TIME_CONTROL"
        const val EXTRA_KEY_GAME_TYPE = "EXTRA_KEY_GAME_TYPE"
    }

    private var game: Game? = null

    private var currentTimeControl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        // Load navigation.
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_navigation, NavigationFragment.newInstance())
            .commit()

        // Get time control.
        val timeControl: String? = intent.getStringExtra(EXTRA_KEY_TIME_CONTROL)
        val gameType: String? = intent.getStringExtra(EXTRA_KEY_GAME_TYPE)
        if (timeControl == null) {
            game = when (gameType) {
                "go" -> GameManager.createGame(GoGame())
                "chess" -> GameManager.createGame(ChessGame())
                else -> GameManager.createGame(Game())
            }

            game!!.setTimeControl(0)
        } else {
            game = GameManager.getGame()
            game!!.setTimeControl(timeControl)
        }

        currentTimeControl = timeControl ?: game!!.getTimeControlList()[0].getName()


        // Fill in the spinner
        val spinnerTimeControl: Spinner = findViewById(R.id.spinner_time_control)
        val options: List<String> = game!!.getTimeControlList().map { it.getName() }
        spinnerTimeControl.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinnerTimeControl.setSelection(0)

        spinnerTimeControl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value = parent?.getItemAtPosition(position) as String

                if (value != currentTimeControl) {
                    game!!.setTimeControl(value)
                    currentTimeControl = value

                    initSettings()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        initSettings()

        val buttonStart: Button = findViewById(R.id.button_start)
        buttonStart.setOnClickListener {
            // Start game.
            game!!.start()
            startActivity(Intent(this, ClockActivity::class.java))
        }
    }

    fun initSettings() {
        // Load black settings.
        val blackSettingsList = game!!.getPlayer(Role.A).getTimeControl().getSettingList()
        Log.d("BlackSettings", blackSettingsList.size.toString())
        val blackSettingListFragment = SettingsListFragment.newInstance(blackSettingsList)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.list_fragment_black, blackSettingListFragment)
            .commit()

        // Load white settings.
        val whiteSettingsList = game!!.getPlayer(Role.B).getTimeControl().getSettingList()
        val whiteSettingListFragment = SettingsListFragment.newInstance(whiteSettingsList)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.list_fragment_white, whiteSettingListFragment)
            .commit()

        // Load advanced settings.
        val advancedSettingsList = game!!.getSettingList()
        val advancedSettingListFragment = SettingsListFragment.newInstance(advancedSettingsList)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.list_fragment_advanced, advancedSettingListFragment)
            .commit()
    }
}