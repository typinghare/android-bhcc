package us.jameschan.boardgameclock.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.Volley
import us.jameschan.boardgameclock.Application
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.settings.SettingManager

private const val ARG_IS_USER_SETTINGS = "ARG_IS_USER_SETTINGS"
private const val ARG_SETTING_NAME = "ARG_SETTING_NAME"
private const val ARG_NAME = "ARG_NAME"
private const val ARG_EXPLANATION = "ARG_EXPLANATION"

class StringSettingFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_string_setting, container, false)

        val inputSettingValue: EditText = view.findViewById(R.id.input_setting_value)
        inputSettingValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && isUserSettings) {
                // Updates user settings.
                val requestQueue = Volley.newRequestQueue(requireActivity())
                Application.persistSettings(requestQueue)
            }
        }
        SettingManager.getSetting(settingName!!)?.setValue(inputSettingValue.text.toString())

        return view
    }
}