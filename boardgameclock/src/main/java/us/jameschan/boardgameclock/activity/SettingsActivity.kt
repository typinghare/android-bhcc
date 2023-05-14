package us.jameschan.boardgameclock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.fragment.SettingsListFragment

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsListFragment: SettingsListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Load navigation.
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val navigationFragment = NavigationFragment.newInstance()
        fragmentTransaction.add(R.id.fragment_container, navigationFragment)
        fragmentTransaction.commit()

        // Load settings.
        settingsListFragment = SettingsListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_list_fragment, settingsListFragment)
            .commit()

    }
}