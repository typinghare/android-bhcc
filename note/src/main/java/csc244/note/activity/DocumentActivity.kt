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
import com.android.volley.Response.ErrorListener
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import csc244.note.R
import csc244.note.common.web.Request
import csc244.note.dto.DocumentResponseDto
import csc244.note.service.DocumentService
import java.util.Date
import java.util.UUID

class DocumentActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_DOCUMENT_ID = "EXTRA_KEY_DOCUMENT_ID"
    }

    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        // document id from extra
        documentId = intent.getStringExtra(EXTRA_KEY_DOCUMENT_ID)
        if (documentId != null) {
            val errorListener = ErrorListener { error ->
                Log.d("Document", error.message.toString())
                if (error is VolleyError) {
                    Log.d("Document", error.networkResponse?.statusCode.toString())
                }
            }
            val request: Request<DocumentResponseDto> =
                DocumentService().getDocumentByID(documentId!!, errorListener) { document ->
                    displayDocument(document.title ?: "", document.text ?: "")
                }
            request.connect(Volley.newRequestQueue(this))
        }

        val buttonResetPassword: Button = findViewById(R.id.button_reset_password)

        buttonResetPassword.setOnClickListener {
            startActivity(Intent(this, InputEmailActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.document_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save_document -> saveDocument()
            R.id.menu_share_document -> shareDocument()
            R.id.menu_load_document -> loadDocument()
            R.id.menu_new_document -> newDocument()
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

        return true
    }

    /**
     * Saves the current document.
     */
    private fun saveDocument() {
        val inputTitle: EditText = findViewById(R.id.input_title)
        val inputContent: EditText = findViewById(R.id.input_content)

        val errorListener = ErrorListener { error ->
            if (error is VolleyError) {
                Log.d("SaveDocument", error.networkResponse?.statusCode.toString())
            }
        }

        if (documentId == null) {
            documentId = UUID.randomUUID().toString()
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val request: Request<Any> = DocumentService().saveDocument(
            DocumentResponseDto().apply {
                this.id = documentId
                this.title = inputTitle.text.toString()
                this.text = inputContent.text.toString()
                this.creationDate = Date().time
            }, errorListener
        ) {
            val message = "Successfully save the document."
            Snackbar.make(inputContent, message, Snackbar.LENGTH_SHORT).show()
        }

        request.connect(requestQueue)
    }

    private fun displayDocument(title: String, content: String) {
        val inputTitle: EditText = findViewById(R.id.input_title)
        val inputContent: EditText = findViewById(R.id.input_content)

        inputTitle.setText(title)
        inputContent.setText(content)
    }

    /**
     * Share the current document.
     */
    private fun shareDocument() {
        if (documentId == null) {
            saveDocument()
        }

        startActivity(Intent(this, ShareDocumentActivity::class.java).apply {
            putExtra(ShareDocumentActivity.EXTRA_KEY_DOCUMENT_ID, documentId)
        })
    }

    /**
     * Load documents; jump to document manager.
     */
    private fun loadDocument() {
        startActivity(Intent(this, DocumentManagementActivity::class.java))
    }

    private fun newDocument() {
        documentId = null
        displayDocument("", "")
    }
}