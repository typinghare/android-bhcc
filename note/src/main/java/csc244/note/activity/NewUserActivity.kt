package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import csc244.note.R
import csc244.note.dto.UserDto
import csc244.note.service.UserService

class NewUserActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_EMAIL: String = "EXTRA_KEY_EMAIL"
        const val EXTRA_KEY_PASSWORD: String = "EXTRA_KEY_PASSWORD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        val inputEmail: EditText = findViewById(R.id.input_email)
        val inputFirstName: EditText = findViewById(R.id.input_first_name)
        val inputLastName: EditText = findViewById(R.id.input_last_name)
        val inputPassword: EditText = findViewById(R.id.input_new_password)

        val buttonNewUser: Button = findViewById(R.id.button_new_user)
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        buttonNewUser.setOnClickListener {
            val userDto = UserDto().apply {
                this.email = inputEmail.text.toString()
                this.firstName = inputFirstName.text.toString()
                this.lastName = inputLastName.text.toString()
                this.password = inputPassword.text.toString()
                this.extra = ""
            }

            val errorListener = Response.ErrorListener { error ->
                Log.d("NewUserError", error.message.toString())
                if (error is VolleyError) {
                    Log.d("NewUserError", error.networkResponse?.statusCode.toString())
                }
            }

            val request = UserService(applicationContext).newUser(userDto, errorListener) {
                Log.d("NewUser", "success")

                startActivity(Intent(this, TemporaryPasswordActivity::class.java).apply {
                    putExtra(EXTRA_KEY_EMAIL, userDto.email)
                    putExtra(EXTRA_KEY_PASSWORD, userDto.password)
                })
            }

            request.connect(requestQueue)
        }
    }
}