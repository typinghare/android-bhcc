package csc244.calculator

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView

@Suppress("DEPRECATION")
class HistoryActivity : AppCompatActivity() {
    companion object {
        const val SELECTED_ID = "SELECTED_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyList = intent.getStringArrayListExtra(HISTORY_EXTRA)
        val listView: ListView = findViewById(R.id.list_view_history)

        if (!historyList.isNullOrEmpty()) {
            val adapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(this, R.layout.list_view_item, historyList.toTypedArray())
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, _, id ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SELECTED_ID, id)

                // send the intent as a request
                startActivityForResult(intent, REQUEST_HISTORY)
                setResult(Activity.RESULT_OK, intent)
                Log.d("id", id.toString())

                // destroy this activity
                finish()
            }
        }
    }
}