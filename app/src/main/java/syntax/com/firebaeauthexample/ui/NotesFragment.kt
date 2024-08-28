package syntax.com.firebaeauthexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import syntax.com.firebaeauthexample.FirebaseViewModel
import syntax.com.firebaeauthexample.databinding.FragmentNotesBinding
import syntax.com.firebaeauthexample.model.Note
import syntax.com.firebaeauthexample.ui.adapter.NotesAdapter

class NotesFragment: Fragment() {
    private lateinit var binding: FragmentNotesBinding
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSaveNote.setOnClickListener {
            val text = binding.tietNotes.text.toString()
            viewModel.saveNote(text)
        }

        viewModel.notesRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val notes = snapshot.toObjects(Note::class.java)
                binding.rvNotes.adapter = NotesAdapter(notes, viewModel)
            }
        }
    }
}