package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import csc244.note.R
import csc244.note.dto.RegisterAccountDto
import csc244.note.service.UserService

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val email: String? = intent.getStringExtra(InputEmailActivity.EXTRA_KEY_EMAIL)

        val inputEmail: EditText = findViewById(R.id.input_email)
        val inputVerificationCode: EditText = findViewById(R.id.input_verification_code)
        val inputPassword: EditText = findViewById(R.id.input_password)
        val buttonSetPassword: Button = findViewById(R.id.button_set_password)

        inputEmail.setText(email)

        buttonSetPassword.setOnClickListener {
            val temporaryPassword: String = inputVerificationCode.text.toString()
            val password: String = inputPassword.text.toString()

            val errorListener = Response.ErrorListener { error ->
                if (error is VolleyError) {
                    Log.d("InputEmail", error.networkResponse?.statusCode.toString())
                }
            }

            UserService(applicationContext).registerAccount(RegisterAccountDto().apply {
                this.tempPassword = temporaryPassword
                this.password = password
            }, errorListener) {
                startActivity(Intent(this, DocumentActivity::class.java))
            }
        }
    }
}