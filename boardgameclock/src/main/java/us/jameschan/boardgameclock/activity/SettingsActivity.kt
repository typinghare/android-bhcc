package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import us.jameschan.boardgameclock.Application
import us.jameschan.boardgameclock.Lang
import us.jameschan.boardgameclock.LocalUser
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.fragment.NavigationFragment
import us.jameschan.boardgameclock.activity.fragment.SettingsListFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Load navigation.
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_navigation, NavigationFragment.newInstance())
            .commit()

        // Load settings.
        val settingsList = Application.getSettings()
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.settings_list_fragment,
                SettingsListFragment.newUserSettingsInstance(settingsList)
            )
            .commit()

        val buttonSignOut: Button = findViewById(R.id.button_sign_out)
        Lang.translate("Sign Out").let { buttonSignOut.text = it }
        buttonSignOut.setOnClickListener {
            LocalUser.settings(LocalUser.defaultSettingsDto)
            LocalUser.userId = null

            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}