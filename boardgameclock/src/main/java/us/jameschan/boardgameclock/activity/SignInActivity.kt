package us.jameschan.boardgameclock.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import us.jameschan.boardgameclock.Api
import us.jameschan.boardgameclock.Lang
import us.jameschan.boardgameclock.LocalUser
import us.jameschan.boardgameclock.R

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Load navigation.
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val navigationFragment = NavigationFragment.newInstance()
        fragmentTransaction.add(R.id.fragment_container, navigationFragment)
        fragmentTransaction.commit()

        val labelWelcome: TextView = findViewById(R.id.text_welcome)
        Lang.translate("Please input username and password.").apply { labelWelcome.text = this }

        val labelUsername: TextView = findViewById(R.id.label_username)
        Lang.translate("username:").apply { labelUsername.text = this }

        val labelPassword: TextView = findViewById(R.id.label_password)
        Lang.translate("password:").apply { labelPassword.text = this }

        val buttonSignIn: Button = findViewById(R.id.button_sign_in)
        Lang.translate("Sign In").apply { buttonSignIn.text = this }

        val buttonCreateNewAccount: Button = findViewById(R.id.button_create_new_account)
        Lang.translate("Create New Account").apply { buttonCreateNewAccount.text = this }

        buttonSignIn.setOnClickListener {
            val inputUsername: EditText = findViewById(R.id.input_username)
            val inputPassword: EditText = findViewById(R.id.input_password)

            val username: String = inputUsername.text.trim().toString()
            val password: String = inputPassword.text.trim().toString()


            // Sign in.
            val request = Api.signIn(username, password, { userDto ->
                LocalUser.userId = userDto.userId
                LocalUser.token = userDto.token

                // Load Settings.
                val request = Api.getUserSettings(userDto.userId, { userSettingsDto ->
                    LocalUser.settings(userSettingsDto)

                    // Start Main Activity
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                }, {
                    Log.d("SignIn:LoadSettings", it.message.toString())
                })

                Lang.translate("Loading settings...").apply {
                    labelWelcome.text = this
                }

                Volley.newRequestQueue(this@SignInActivity).add(request)
            }, {
                Log.d("SignIn:SignIn", it.message.toString())
            })

            Lang.translate("Signing in...").apply {
                labelWelcome.text = this
            }

            Volley.newRequestQueue(this).add(request)
        }

        buttonCreateNewAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}