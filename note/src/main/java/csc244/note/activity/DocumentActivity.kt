package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import csc244.note.R

class DocumentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        val buttonResetPassword: Button = findViewById(R.id.button_reset_password)

        buttonResetPassword.setOnClickListener {
            startActivity(Intent(this, InputEmailActivity::class.java))
        }
    }
}