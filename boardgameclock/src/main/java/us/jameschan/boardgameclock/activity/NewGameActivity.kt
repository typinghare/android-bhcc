package us.jameschan.boardgameclock.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.fragment.NavigationFragment

class NewGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        // Load navigation.
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_navigation, NavigationFragment.newInstance())
            .commit()
    }
}