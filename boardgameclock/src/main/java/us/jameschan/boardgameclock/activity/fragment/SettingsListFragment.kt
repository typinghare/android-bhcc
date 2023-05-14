package us.jameschan.boardgameclock.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.adapter.SettingsListAdapter
import us.jameschan.boardgameclock.game.settings.Setting

class SettingsListFragment : Fragment() {
    companion object {
        private const val ARG_SETTINGS = "ARG_SETTINGS"

        @JvmStatic
        fun newInstance(settings: List<Setting>) =
            SettingsListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SETTINGS, Gson().toJson(settings))
                }
            }
    }

    private var settingList: MutableList<Setting> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val settingArray: Array<Setting> =
                Gson().fromJson(it.getString(ARG_SETTINGS), Array<Setting>::class.java)
            settingArray.forEach { setting -> settingList.add(setting) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_list, container, false)

        // Set the adapter.
        if (view is RecyclerView) {
            view.adapter = SettingsListAdapter(settingList)
        }
        return view
    }
}