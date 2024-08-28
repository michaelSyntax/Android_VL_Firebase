package syntax.com.firebaeauthexample.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.btSave.setOnClickListener {
            val firstName = binding.tietFirstName.text.toString()
            val lastName = binding.tietLastName.text.toString()
        }
    }
}