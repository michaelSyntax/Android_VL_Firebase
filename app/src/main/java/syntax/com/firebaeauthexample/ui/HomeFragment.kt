package syntax.com.firebaeauthexample.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import syntax.com.firebaeauthexample.FirebaseViewModel
import syntax.com.firebaeauthexample.R
import syntax.com.firebaeauthexample.databinding.FragmentHomeBinding
import syntax.com.firebaeauthexample.model.Profile

class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var getContent: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null) {
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.btToNotes.setOnClickListener {
            findNavController().navigate(R.id.notesFragment)
        }

        binding.btSave.setOnClickListener {
            val firstName = binding.tietFirstName.text.toString()
            val lastName = binding.tietLastName.text.toString()
            viewModel.updateProfile(firstName, lastName)
        }

        viewModel.profileRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val profile = snapshot.toObject(Profile::class.java)
                binding.tietFirstName.setText(profile?.firstName)
                binding.tietLastName.setText(profile?.lastName)
                binding.ivProfilePic.load(profile?.profilePicture)
            }
        }

        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    binding.ivProfilePic.setImageURI(uri)
                    viewModel.uploadImage(uri)
                }
            }
        }

        binding.ivProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            getContent.launch(intent)
        }
    }
}