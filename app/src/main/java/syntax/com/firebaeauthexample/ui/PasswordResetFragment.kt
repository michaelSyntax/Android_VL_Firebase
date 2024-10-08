package syntax.com.firebaeauthexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import syntax.com.firebaeauthexample.FirebaseViewModel
import syntax.com.firebaeauthexample.R
import syntax.com.firebaeauthexample.databinding.FragmentPasswordForgottenBinding

class PasswordResetFragment: Fragment() {

    private lateinit var binding: FragmentPasswordForgottenBinding
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordForgottenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSendEmailPasswordReset.setOnClickListener {
            val email = binding.tietEmailPasswordReset.text.toString()
            viewModel.sendPasswordReset(email)
        }

        binding.btBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }
}