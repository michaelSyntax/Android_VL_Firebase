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

        /**
         * Funktion um Bild vom Gerät auszuwählen.
         * Startet den Ressource-Picker und zeigt uns alle Bilder auf dem Gerät an
         */
        binding.ivProfilePic.setOnClickListener {
            getContent.launch("image/*")
        }

        /**
         * Snapshot Listener: Hört auf Änderungen in dem Firestore Document, das beobachtet wird
         * Hier: Referenz auf Profil wird beobachtet.
         * Umwandeln des Snapshots in eine Klassen-Instanz von der Klasse Profil und setzen der Felder.
         */
        viewModel.profileRef.addSnapshotListener { value, error ->
            if (error == null && value != null) {
                val myProfile = value.toObject(Profile::class.java)
                binding.tietFirstName.setText(myProfile!!.firstName)
                binding.tietLastName.setText(myProfile.lastName)
                binding.ivProfilePic.load(myProfile.profilePicture)
            }
        }

        /**
         * Beim Klick auf Logout wird die Logoutfunktion im ViewModel aufgerufen.
         */
        binding.btLogout.setOnClickListener {
            viewModel.logout()
        }

        /**
         * currentUser LiveData aus dem ViewModel wird beobachtet.
         * Wenn currentUser gleich null (also der User nicht mehr eingeloggt ist),
         * wird zum LoginFragment navigiert.
         */
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null) {
                findNavController().navigate(R.id.loginFragment)
            }
        }

        /**
         * Neue Profil-Daten in Firestore speichern.
          */
        binding.btSave.setOnClickListener {
            val firstName = binding.tietFirstName.text.toString()
            val lastName = binding.tietLastName.text.toString()

            if (firstName.isNotBlank() && lastName.isNotBlank()) {
                val newProfile = Profile(firstName, lastName)
                viewModel.updateProfile(newProfile)
            }
        }
    }

    /**
     *  Erstellen der GetContent-Funktion, um Bilder vom Gerät auszuwählen und anschließend ans ViewModel weiterzugeben.
     */
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            viewModel.uploadImage(uri)
        }
    }
}