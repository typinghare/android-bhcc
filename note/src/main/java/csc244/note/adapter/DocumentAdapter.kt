package csc244.note.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import csc244.note.R
import csc244.note.dto.DocumentResponseDto

class DocumentAdapter(context: Context, documents: List<DocumentResponseDto>) :
    ArrayAdapter<DocumentResponseDto>(context, 0, documents) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        val document: DocumentResponseDto? = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.document_list_view_item, parent, false)
        }

        val titleTextView: TextView = view!!.findViewById(R.id.list_item_document_title)
        val contentTextView: TextView = view.findViewById(R.id.list_item_document_content)

        titleTextView.text = document!!.title
        contentTextView.text = document.text

        return view
    }
}