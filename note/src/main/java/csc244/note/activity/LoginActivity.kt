package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import csc244.note.R
import csc244.note.service.UserService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textMessage: TextView = findViewById(R.id.login__text_message)
        val inputEmail: EditText = findViewById(R.id.login__input_email)
        val inputPassword: EditText = findViewById(R.id.login__input_password)

        val extraEmail: String? = intent.getStringExtra(TemporaryPasswordActivity.EXTRA_KEY_EMAIL)
        if (extraEmail != null) {
            inputEmail.setText(extraEmail)
        }

        "Please sign in.".also { textMessage.text = it }

        val buttonSignIn: Button = findViewById(R.id.login__button_sign_in)
        val buttonForgotPassword: Button = findViewById(R.id.login__button_forgot_password)
        val buttonNewUser: Button = findViewById(R.id.login__button_new_user)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        buttonSignIn.setOnClickListener {
            val email: String = inputEmail.text.toString()
            val password: String = inputPassword.text.toString()
            val errorListener = ErrorListener { error ->
                if (error is VolleyError) {
                    val statusCode: Int? = error.networkResponse?.statusCode
                    if (statusCode == 401) {
                        """
                            The email does not exist or the password does not
                            match the email. Please try again!
                        """.trimIndent().replace("\n", "").also { textMessage.text = it }
                    } else if (statusCode == 400) {
                        """
                           This account has not been registered. 
                           Please complete the registration process before authenticating. 
                        """.trimIndent().replace("\n", "").also { textMessage.text = it }
                    }
                }
            }

            val request = UserService(applicationContext).signIn(email, password, errorListener) {
                // Jump to the Document Activity.
                startActivity(Intent(this, DocumentActivity::class.java).apply {
                    putExtra(InputEmailActivity.EXTRA_KEY_EMAIL, email)
                })
            }

            "Signing in, please wait...".also { textMessage.text = it }
            request.connect(requestQueue)
        }

        buttonForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        buttonNewUser.setOnClickListener {
            val intent = Intent(this, NewUserActivity::class.java)
            startActivity(intent)
        }
    }
}