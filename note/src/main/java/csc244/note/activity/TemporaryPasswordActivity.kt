package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import csc244.note.R
import csc244.note.dto.RegisterAccountDto
import csc244.note.service.UserService

class TemporaryPasswordActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_EMAIL = "EXTRA_KEY_EMAIL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temporary_password)

        val email = intent.getStringExtra(NewUserActivity.EXTRA_KEY_EMAIL)
        val password = intent.getStringExtra(NewUserActivity.EXTRA_KEY_PASSWORD)

        val inputVerificationCode: TextView = findViewById(R.id.input_verification_code)
        val finishRegistration: Button = findViewById(R.id.finish_registration)
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        finishRegistration.setOnClickListener {
            val userRegisterAccountDto = RegisterAccountDto().apply {
                this.email = email
                this.password = password
                this.tempPassword = inputVerificationCode.text.toString()
            }

            val errorListener = Response.ErrorListener { error ->
                Log.d("RegisterNewAccountError", error.message.toString())
                if (error is VolleyError) {
                    Log.d("NewUser", error.networkResponse?.statusCode.toString())
                }
            }

            val request = UserService(applicationContext)
                .registerAccount(userRegisterAccountDto, errorListener) {
                    Log.d("NewUser", "success (set temporary password)")
                    startActivity(Intent(this, LoginActivity::class.java).apply {
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