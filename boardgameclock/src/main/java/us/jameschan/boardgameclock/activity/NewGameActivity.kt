package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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

        // Set images.
        val imageChess: ImageView = findViewById(R.id.image_chess)
        val imageGo: ImageView = findViewById(R.id.image_go)
        val imageCheckers: ImageView = findViewById(R.id.image_checkers)

        imageChess.setOnClickListener { startNewGameSettings("chess") }
        imageGo.setOnClickListener { startNewGameSettings("go") }
        imageCheckers.setOnClickListener { startNewGameSettings("checkers") }
    }

    private fun startNewGameSettings(gameType: String) {
        startActivity(Intent(this, GameSettingsActivity::class.java).apply {
            putExtra(GameSettingsActivity.EXTRA_KEY_GAME_TYPE, gameType)
        })
    }
}