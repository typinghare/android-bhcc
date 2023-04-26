package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import csc244.note.R
import csc244.note.adapter.DocumentAdapter
import csc244.note.api.DocumentApi
import csc244.note.dto.DocumentResponseDto
import csc244.note.service.DocumentService

class DocumentManagementActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_SHOW_SHARED_DOCUMENTS = "EXTRA_KEY_SHOW_SHARED_DOCUMENTS"
    }

    private var showSharedDocuments: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_management)


        val switchShowShareDocument: SwitchCompat = findViewById(R.id.switch_show_share_document)

        // switch
        showSharedDocuments = intent.getBooleanExtra(EXTRA_KEY_SHOW_SHARED_DOCUMENTS, false)
        switchShowShareDocument.isChecked = showSharedDocuments

        switchShowShareDocument.setOnCheckedChangeListener { _, isChecked ->
            showSharedDocuments = isChecked
            loadDocuments()
        }

        // When the activity is loaded, load all documents.
        loadDocuments()
    }

    private fun loadDocuments() {
        val listViewDocument: ListView = findViewById(R.id.list_document)
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val errorListener = Response.ErrorListener { error ->
            Log.d("DocumentManagement", error.message.toString())
            if (error is VolleyError) {
                Log.d("DocumentManagement", error.networkResponse?.statusCode.toString())
            }
        }

        val scope: DocumentApi.DocumentScope =
            if (showSharedDocuments) DocumentApi.DocumentScope.SELF_SHARED else DocumentApi.DocumentScope.SELF
        val request: JsonArrayRequest =
            DocumentService().getAllDocument(scope, errorListener) {
                Log.d("DocumentManagement", "Show documents.")
                listViewDocument.adapter = DocumentAdapter(this, it)
                listViewDocument.setOnItemClickListener { _, _, position, _ ->
                    val document: DocumentResponseDto =
                        listViewDocument.getItemAtPosition(position) as DocumentResponseDto
                    startActivity(Intent(this, DocumentActivity::class.java).apply {
                        putExtra(DocumentActivity.EXTRA_KEY_DOCUMENT_ID, document.id.toString())
                    })
                }
            }

        requestQueue.add(request)
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