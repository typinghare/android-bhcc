package us.jameschan.boardgameclock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import us.jameschan.boardgameclock.Application
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
            .replace(R.id.settings_list_fragment, SettingsListFragment.newInstance(settingsList))
            .commit()
    }
}