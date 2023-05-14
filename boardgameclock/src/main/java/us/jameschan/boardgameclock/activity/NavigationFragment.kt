package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import us.jameschan.boardgameclock.R

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
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
        }

        return view
    }
}