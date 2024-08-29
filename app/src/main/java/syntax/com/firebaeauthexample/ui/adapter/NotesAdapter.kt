package syntax.com.firebaeauthexample.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import syntax.com.firebaeauthexample.FirebaseViewModel
import syntax.com.firebaeauthexample.databinding.ItemNoteBinding
import syntax.com.firebaeauthexample.model.Note

class NotesAdapter(
    private val dataset: List<Note>,
    private val viewModel: FirebaseViewModel
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = dataset[position]
        holder.binding.tvNoteText.text = item.text
        holder.binding.cvNote.setOnClickListener {
            viewModel.deleteNote(item)
        }
    }
}