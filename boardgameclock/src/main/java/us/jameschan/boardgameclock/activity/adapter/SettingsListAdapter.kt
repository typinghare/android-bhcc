package us.jameschan.boardgameclock.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import us.jameschan.boardgameclock.R
import us.jameschan.boardgameclock.settings.Setting

class SettingsListAdapter(
    private val settings: List<Setting>
) : RecyclerView.Adapter<SettingsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_string_setting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val setting = settings[position]
        holder.textSettingName.text = setting.getLabel()
        holder.inputSettingValue.text = setting.getDefaultValue()

        val explanation = setting.getExplanation()
        if (explanation != "") {
            holder.textExplanation.text = explanation
        } else {
            holder.textExplanation.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textSettingName: TextView = view.findViewById(R.id.text_setting_name)
        val inputSettingValue: TextView = view.findViewById(R.id.input_setting_value)
        val textExplanation: TextView = view.findViewById(R.id.text_explanation)
    }
}