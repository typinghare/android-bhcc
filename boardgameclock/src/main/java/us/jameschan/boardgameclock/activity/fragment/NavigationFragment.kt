package us.jameschan.boardgameclock.activity.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import us.jameschan.boardgameclock.LocalUser
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.MainActivity
import us.jameschan.boardgameclock.activity.SettingsActivity
import us.jameschan.boardgameclock.activity.SignInActivity

class NavigationFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            NavigationFragment().apply {
                arguments = Bundle()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)

        val buttonClock: ImageButton = view.findViewById(R.id.button_clock)
        val buttonUser: ImageButton = view.findViewById(R.id.button_user)

        buttonClock.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

        buttonUser.setOnClickListener {
            if (LocalUser.userId == null) {
                startActivity(Intent(requireActivity(), SignInActivity::class.java))
            } else {
                startActivity(Intent(requireActivity(), SettingsActivity::class.java))
            }
        }

        return view
    }
}