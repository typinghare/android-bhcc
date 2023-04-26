package csc244.note.api

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.toolbox.JsonArrayRequest
import csc244.note.common.User
import csc244.note.common.web.Listener
import csc244.note.common.web.Request
import csc244.note.dto.DocumentAccessorDto
import csc244.note.dto.DocumentDto
import csc244.note.dto.DocumentResponseDto
import org.json.JSONObject

object DocumentApi {
    private const val API_METHOD_GET_DOCUMENT = "getDocument"
    private const val API_METHOD_GET_DOCUMENTS = "getDocuments"
    private const val API_METHOD_DELETE_DOCUMENT = "deleteDocument"
    private const val API_METHOD_SET_DOCUMENT = "setDocument"
    private const val API_METHOD_SET_DOCUMENT_ACCESSOR = "setDocumentAccessors"
    private const val API_METHOD_GET_DOCUMENT_ACCESSOR = "setDocumentAccessors"

    public enum class DocumentScope(val intVal: Int) { SELF(0), SHARED(1), SELF_SHARED(2) }

    /**
     * Retrieves a document.
     */
    fun getDocument(
        documentDto: DocumentDto,
        callback: (documentResponseDto: DocumentResponseDto) -> Unit,
        errorListener: ErrorListener
    ): Request<DocumentResponseDto> {
        val map = mutableMapOf<String, Any>()
        map["document_id"] = documentDto.documentId!!

        return Api.post(
            API_METHOD_GET_DOCUMENT,
            map,
            Listener(DocumentResponseDto::class, callback),
            errorListener
        )
    }

    /**
     * Retrieves all documents.
     */
    fun getAllDocument(
        documentScope: DocumentScope,
        callback: (documentList: List<DocumentResponseDto>) -> Unit,
        errorListener: Response.ErrorListener
    ): JsonArrayRequest {
        val jsonObject = JSONObject()
        jsonObject.put("method", API_METHOD_GET_DOCUMENTS)
        jsonObject.put("scope", documentScope.intVal)

        return object : JsonArrayRequest(
            Method.POST, Api.URL, null, { response ->
                val documentList = mutableListOf<DocumentResponseDto>()
                for (i in 0 until response.length()) {
                    val document = response.getJSONObject(i)
                    val documentResponseDto = DocumentResponseDto()

                    documentResponseDto.id = document.getString("id")
                    documentResponseDto.title = document.getString("title")
                    documentResponseDto.text = document.getString("text")
                    documentResponseDto.creationDate = document.getLong("creation_date")

                    documentList.add(documentResponseDto)
                }

                callback(documentList)
            }, errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["autho_token"] = User.getToken().toString()

                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray()
            }
        }
    }

    /**
     * Deletes a document.
     */
    fun deleteDocument(
        documentDto: DocumentDto,
        callback: () -> Unit,
        errorListener: ErrorListener
    ): Request<Any> {
        val map = mutableMapOf<String, Any>().apply {
            put("document_id", documentDto.documentId!!)
        }

        return Api.post(
            API_METHOD_DELETE_DOCUMENT,
            map,
            Listener(callback),
            errorListener
        )
    }

    /**
     * Updates a document.
     */
    fun updateDocument(
        documentDto: DocumentResponseDto,
        callback: () -> Unit,
        errorListener: ErrorListener
    ): Request<Any> {
        val map = mutableMapOf<String, Any>().apply {
            put("document", JSONObject().apply {
                put("id", documentDto.id!!)
                put("title", documentDto.title!!)
                put("text", documentDto.text!!)
                put("creation_date", documentDto.creationDate!!)
            })
        }

        return Api.post(
            API_METHOD_SET_DOCUMENT,
            map,
            Listener(callback),
            errorListener
        )
    }

    /**
     * Sets accessors for a document.
     */
    fun setDocumentAccessor(
        documentAccessorDto: DocumentAccessorDto,
        callback: () -> Unit,
        errorListener: ErrorListener
    ): Request<Any> {
        return Api.post(
            API_METHOD_SET_DOCUMENT_ACCESSOR,
            mutableMapOf<String, Any>().apply {
                put("document_id", documentAccessorDto.documentId!!)
                put("accessors", documentAccessorDto.accessorList!!)
            },
            Listener(callback),
            errorListener
        )
    }

    /**
     * Retrieves all accessors of a document.
     */
    fun setDocumentAccessor(
        documentDto: DocumentDto,
        callback: (documentAccessorDto: DocumentAccessorDto) -> Unit,
        errorListener: ErrorListener
    ): Request<DocumentAccessorDto> {
        return Api.post(
            API_METHOD_GET_DOCUMENT_ACCESSOR,
            mutableMapOf<String, Any>().apply { put("document_id", documentDto.documentId!!) },
            Listener(DocumentAccessorDto::class, callback),
            errorListener
        )
    }
}