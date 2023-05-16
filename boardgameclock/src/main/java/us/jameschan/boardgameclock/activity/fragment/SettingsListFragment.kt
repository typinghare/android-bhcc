package us.jameschan.boardgameclock.activity.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.activity.adapter.SettingsListAdapter
import us.jameschan.boardgameclock.settings.Setting
import us.jameschan.boardgameclock.util.JsonHelper

class SettingsListFragment : Fragment() {
    companion object {
        private const val ARG_SETTINGS = "ARG_SETTINGS"
        private const val ARG_IS_USER_SETTINGS = "ARG_IS_USER_SETTINGS"

        @JvmStatic
        fun newInstance(settings: List<Setting>) =
            SettingsListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_USER_SETTINGS, false)
                    putString(ARG_SETTINGS, JsonHelper.toJson(settings))
                }
            }

        @JvmStatic
        fun newUserSettingsInstance(settings: List<Setting>) =
            SettingsListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_USER_SETTINGS, true)
                    putString(ARG_SETTINGS, JsonHelper.toJson(settings))
                }
            }
    }

    private var settingList: MutableList<Setting> = mutableListOf()

    private var isUserSettings: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            isUserSettings = it.getBoolean(ARG_IS_USER_SETTINGS)
            settingList.addAll(JsonHelper.fromJsonToSettingList(it.getString(ARG_SETTINGS)!!))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_list, container, false)

        // Set the adapter.
        Log.d("Setting:Size", settingList.size.toString())
        if (view is RecyclerView) {
            view.adapter = SettingsListAdapter(requireContext(), settingList, isUserSettings)
        }
        return view
    }
}