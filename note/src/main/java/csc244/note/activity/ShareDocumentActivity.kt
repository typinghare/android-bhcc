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
import csc244.note.common.web.Request
import csc244.note.service.DocumentService

class ShareDocumentActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_DOCUMENT_ID = "EXTRA_KEY_DOCUMENT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_document)

        val documentId: String? = intent.getStringExtra(EXTRA_KEY_DOCUMENT_ID)
        val inputEmail: EditText = findViewById(R.id.input_email)
        val buttonShareDocument: Button = findViewById(R.id.button_share_document)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        buttonShareDocument.setOnClickListener {
            if (documentId == null) {
                Log.d("ShareDocument", "The document id is null.")
                return@setOnClickListener
            }

            val email: String = inputEmail.text.toString()
            val errorListener = Response.ErrorListener { error ->
                Log.d("NewUserError", error.message.toString())
                if (error is VolleyError) {
                    Log.d("NewUserError", error.networkResponse?.statusCode.toString())
                }
            }

            val request: Request<Any> =
                DocumentService().updateAccessors("", listOf(email), errorListener) {
                    startActivity(Intent(this, DocumentActivity::class.java).apply {
                        putExtra(DocumentActivity.EXTRA_KEY_DOCUMENT_ID, documentId)
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