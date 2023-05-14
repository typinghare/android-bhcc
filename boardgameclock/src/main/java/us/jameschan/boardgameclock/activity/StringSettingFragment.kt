package us.jameschan.boardgameclock.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import us.jameschan.boardgameclock.R

private const val ARG_NAME = "ARG_NAME"
private const val ARG_EXPLANATION = "ARG_EXPLANATION"

class StringSettingFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(name: String, explanation: String) =
            StringSettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_EXPLANATION, explanation)
                }
            }
    }

    private var name: String? = null
    private var explanation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME)
            explanation = it.getString(ARG_EXPLANATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_string_setting, container, false)



        return view
    }
}