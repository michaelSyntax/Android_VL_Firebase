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
import syntax.com.firebaeauthexample.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Button um zum RegisterFragment zu navigieren.
         */
        binding.btToRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        /**
         * Button um zum PasswordResetFragment zu navigieren.
         */
        binding.btSendPasswordReset.setOnClickListener {
            findNavController().navigate(R.id.passwordResetFragment)
        }

        /**
         * Button um User einzuloggen.
         * Erst werden E-Mail und passwort aus den Eingabefeldern geholt.
         * Wenn beide nicht leer sind, rufen wir die login Funktion im ViewModel auf
         */
        binding.btLogin.setOnClickListener {
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.login(email, password)
            }
        }

        /**
         * currentUser LiveData aus dem ViewModel wird beobachtet.
         * Wenn currentUser nicht gleich null (also der User eingeloggt ist) wird zum HomeFragment navigiert.
         */
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }
}