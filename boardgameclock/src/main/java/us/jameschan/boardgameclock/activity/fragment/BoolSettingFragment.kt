package us.jameschan.boardgameclock.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.Volley
import us.jameschan.boardgameclock.Application
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.settings.SettingManager

private const val ARG_IS_USER_SETTINGS = "ARG_IS_USER_SETTINGS"
private const val ARG_SETTING_NAME = "ARG_SETTING_NAME"
private const val ARG_NAME = "ARG_NAME"
private const val ARG_EXPLANATION = "ARG_EXPLANATION"

class BoolSettingFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(
            isUserSettings: Boolean,
            settingName: String,
            name: String,
            explanation: String
        ) =
            StringSettingFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_USER_SETTINGS, isUserSettings)
                    putString(ARG_SETTING_NAME, settingName)
                    putString(ARG_NAME, name)
                    putString(ARG_EXPLANATION, explanation)
                }
            }

        @JvmStatic
        fun newInstance(
            settingName: String,
            name: String,
            explanation: String
        ) =
            StringSettingFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_USER_SETTINGS, false)
                    putString(ARG_SETTING_NAME, settingName)
                    putString(ARG_NAME, name)
                    putString(ARG_EXPLANATION, explanation)
                }
            }
    }

    private var isUserSettings: Boolean = false
    private var settingName: String? = null
    private var name: String? = null
    private var explanation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isUserSettings = it.getBoolean(ARG_IS_USER_SETTINGS)
            settingName = it.getString(ARG_SETTING_NAME)
            name = it.getString(ARG_NAME)
            explanation = it.getString(ARG_EXPLANATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bool_setting, container, false)

        val switchSettingValue: SwitchCompat = view.findViewById(R.id.switch_setting_value)
        switchSettingValue.setOnCheckedChangeListener { _, isChecked ->
            val value = if (isChecked) "true" else "false"
            SettingManager.getSetting(settingName!!)?.setValue(value)

            if (isUserSettings) {
                // Updates user settings.
                val requestQueue = Volley.newRequestQueue(requireActivity())
                Application.persistSettings(requestQueue)
            }
        }

        return view
    }
}