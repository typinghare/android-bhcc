package us.jameschan.boardgameclock.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.settings.SettingManager

private const val ARG_IS_USER_SETTINGS = "ARG_IS_USER_SETTINGS"
private const val ARG_SETTING_NAME = "ARG_SETTING_NAME"
private const val ARG_NAME = "ARG_NAME"
private const val ARG_EXPLANATION = "ARG_EXPLANATION"

class OptionsSettingFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_options_setting, container, false)
        val spinnerSettingValue: Spinner = view.findViewById(R.id.spinner_setting_value)

        val options: List<String> = SettingManager.getSetting(settingName!!)!!.getOptionList()
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, options)
        spinnerSettingValue.adapter = adapter

        spinnerSettingValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value = parent?.getItemAtPosition(position) as String
                SettingManager.getSetting(settingName!!)!!.setValue(value)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        return view
    }
}