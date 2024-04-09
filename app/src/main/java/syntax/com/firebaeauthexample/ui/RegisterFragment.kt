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
import syntax.com.firebaeauthexample.databinding.FragmentRegisterBinding

class RegisterFragment: Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Button um zurück zum LoginFragment zu navigieren
          */
        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }


        /**
         *  Button um User zu registrieren.
         *  Erst werden E-Mail und passwort aus den Eingabefeldern geholt.
         *  Wenn beide nicht leer sind rufen wir die register Funktion im ViewModel auf.
         */
        binding.btRegister.setOnClickListener {
            val email = binding.tietEmailRegister.text.toString()
            val password = binding.tietPasswordRegister.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.register(email, password)
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