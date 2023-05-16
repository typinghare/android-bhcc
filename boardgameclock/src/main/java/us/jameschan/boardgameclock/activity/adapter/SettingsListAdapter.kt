package us.jameschan.boardgameclock.activity.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import us.jameschan.boardgameclock.Application
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.settings.Setting

class SettingsListAdapter(
    private val context: Context,
    private val settings: List<Setting>,
    private val isUserSettings: Boolean = false
) : RecyclerView.Adapter<SettingsListAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val setting = settings[position]
        return when (setting.getType()) {
            Setting.Companion.Type.STRING -> 0
            Setting.Companion.Type.BOOL -> 1
            Setting.Companion.Type.OPTIONS -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = when (viewType) {
            0 -> inflater.inflate(R.layout.fragment_string_setting, parent, false)
            1 -> inflater.inflate(R.layout.fragment_bool_setting, parent, false)
            2 -> inflater.inflate(R.layout.fragment_options_setting, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val setting = settings[position]

        // General parts.
        holder.textSettingName.text = setting.getLabel()

        val explanation = setting.getExplanation()
        if (explanation != "") {
            holder.textExplanation.text = explanation
        } else {
            holder.textExplanation.visibility = View.INVISIBLE
        }

        // Special parts.
        holder.bind(setting)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView
        val textSettingName: TextView = view.findViewById(R.id.text_setting_name)
        val textExplanation: TextView = view.findViewById(R.id.text_explanation)

        fun bind(setting: Setting) {
            when (setting.getType()) {
                Setting.Companion.Type.STRING -> bindString(setting)
                Setting.Companion.Type.BOOL -> bindBoolean(setting)
                Setting.Companion.Type.OPTIONS -> bindOptions(setting)
            }
        }

        private fun bindString(setting: Setting) {
            val inputSettingValue: EditText = view.findViewById(R.id.input_setting_value)

            val initValue: String = setting.getValue()
            inputSettingValue.setText(initValue)

            inputSettingValue.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) return@setOnFocusChangeListener

                val value = inputSettingValue.text.toString()

                Log.d("SettingsChange", "String")
                Log.d("SettingsChange:String", value)
                setting.setValue(value)

                if (isUserSettings) {
                    // Updates user settings.
                    Log.d("SettingsChange", "String@isUserSettings")

                    val requestQueue = Volley.newRequestQueue(context)
                    Application.persistSettings(requestQueue)
                }
            }
        }

        private fun bindBoolean(setting: Setting) {
            val switchSettingValue: SwitchCompat = view.findViewById(R.id.switch_setting_value)

            val initValue = setting.getValue()
            switchSettingValue.isChecked = initValue.toBoolean()

            switchSettingValue.setOnCheckedChangeListener { _, isChecked ->
                Log.d("SettingsChange", "Boolean")
                val value = if (isChecked) "true" else "false"
                setting.setValue(value)

                if (isUserSettings) {
                    Log.d("SettingsChange", "Boolean@isUserSettings")
                    // Updates user settings.
                    val requestQueue = Volley.newRequestQueue(context)
                    Application.persistSettings(requestQueue)
                }
            }
        }

        private fun bindOptions(setting: Setting) {
            val spinnerSettingValue: Spinner = view.findViewById(R.id.spinner_setting_value)

            val options: List<String> = setting.getOptionList()
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
            spinnerSettingValue.adapter = adapter

            val initValue = setting.getValue()
            val index = options.indexOf(initValue)
            spinnerSettingValue.setSelection(index)

            spinnerSettingValue.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        Log.d("SettingsChange", "Options")
                        val value = parent?.getItemAtPosition(position) as String
                        setting.setValue(value)

                        if (isUserSettings) {
                            Log.d("SettingsChange", "Options@SettingsChange")

                            // Updates user settings.
                            val requestQueue = Volley.newRequestQueue(context)
                            Application.persistSettings(requestQueue)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        }
    }
}