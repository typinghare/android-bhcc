package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import csc244.note.R
import csc244.note.service.UserService

class InputEmailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_EMAIL = "EXTRA_KEY_EMAIL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_email_acivity)

        val inputEmail: EditText = findViewById(R.id.input_email)
        val buttonReset: Button = findViewById(R.id.button_reset)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        buttonReset.setOnClickListener {
            val email = inputEmail.text.toString()
            val errorListener = Response.ErrorListener { error ->
                if (error is VolleyError) {
                    Log.d("InputEmail", error.networkResponse?.statusCode.toString())
                }
            }

            val request = UserService(applicationContext).resetPassword(email, errorListener) {
                startActivity(Intent(this, ResetPasswordActivity::class.java).apply {
                    putExtra(EXTRA_KEY_EMAIL, email)
                })
            }

            request.connect(requestQueue)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.back_to_document_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_back_to_document -> {
                startActivity(Intent(this, DocumentActivity::class.java))
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

        return true
    }
}