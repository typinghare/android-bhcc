package csc244.note.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.android.volley.Response.ErrorListener
import csc244.note.R
import csc244.note.service.UserService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputEmail: EditText = findViewById(R.id.login__input_email)
        val inputPassword: EditText = findViewById(R.id.login__input_password)

        val buttonSignIn: Button = findViewById(R.id.login__button_sign_in)
        val buttonForgotPassword: Button = findViewById(R.id.login__button_forgot_password)
        val buttonNewUser: Button = findViewById(R.id.login__button_new_user)

        buttonSignIn.setOnClickListener {
            val email: String = inputEmail.text.toString()
            val password: String = inputPassword.text.toString()
            val errorListener = ErrorListener {

            }

            UserService(applicationContext).signIn(email, password, errorListener) {
                val intent = Intent(this, DocumentActivity::class.java)
                startActivity(intent)
            }
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