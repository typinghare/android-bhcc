package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import us.jameschan.boardgameclock.Lang
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.fragment.NavigationFragment

/**
 * @link https://docs.google.com/presentation/d/1qtMkujyhOvK0CV6DHkWr6habZky-3g_HYRwaFhNLSM0
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load navigation.
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_navigation, NavigationFragment.newInstance())
            .commit()

        val buttonNewGame: Button = findViewById(R.id.button_new_game)
        Lang.translate("New Game").apply { buttonNewGame.text = this }
        val buttonSettings: Button = findViewById(R.id.button_settings)
        Lang.translate("Settings").apply { buttonSettings.text = this }
        val buttonAbout: Button = findViewById(R.id.button_about)
        Lang.translate("About").apply { buttonAbout.text = this }

        buttonNewGame.setOnClickListener {
            startActivity(Intent(this, NewGameActivity::class.java))
        }

        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        buttonAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}