package us.jameschan.boardgameclock.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import us.jameschan.boardgameclock.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Load navigation.
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val navigationFragment = NavigationFragment.newInstance()
        fragmentTransaction.add(R.id.fragment_container, navigationFragment)
        fragmentTransaction.commit()
    }
}