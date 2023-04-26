package csc244.note.service

import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import csc244.note.api.DocumentApi
import csc244.note.common.web.Request
import csc244.note.dto.DocumentAccessorDto
import csc244.note.dto.DocumentDto
import csc244.note.dto.DocumentResponseDto

class DocumentService {
    fun saveDocument(
        documentResponseDto: DocumentResponseDto,
        errorListener: Response.ErrorListener,
        successCallback: () -> Unit
    ): Request<Any> {
        return DocumentApi.updateDocument(documentResponseDto, {
            successCallback()
        }, errorListener)
    }

    fun updateAccessors(
        documentId: String,
        accessors: List<String>,
        errorListener: Response.ErrorListener,
        successCallback: () -> Unit
    ): Request<Any> {
        return DocumentApi.setDocumentAccessor(DocumentAccessorDto().apply {
            this.documentId = documentId
            this.accessorList = accessors
        }, { successCallback() }, errorListener)
    }

    fun getAllDocument(
        documentScope: DocumentApi.DocumentScope,
        errorListener: Response.ErrorListener,
        successCallback: (documentList: List<DocumentResponseDto>) -> Unit
    ): JsonArrayRequest {
        return DocumentApi.getAllDocument(documentScope, { documentList ->
            successCallback(documentList)
        }, errorListener)
    }

    fun getDocumentByID(
        documentId: String,
        errorListener: Response.ErrorListener,
        successCallback: (DocumentResponseDto) -> Unit
    ): Request<DocumentResponseDto> {
        return DocumentApi.getDocument(
            DocumentDto().apply { this.documentId = documentId },
            successCallback,
            errorListener
        )
    }
}