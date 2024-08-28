package syntax.com.firebaeauthexample

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import syntax.com.firebaeauthexample.model.Note
import syntax.com.firebaeauthexample.model.Profile

class FirebaseViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    lateinit var profileRef: DocumentReference
    lateinit var notesRef: CollectionReference

    private var _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    init {
        if (auth.currentUser != null) {
            profileRef = firestore.collection("profiles").document(auth.currentUser!!.uid)
            notesRef = profileRef.collection("notes")
        }
    }


    fun register(email: String, password: String, firstname: String, lastname: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        _currentUser.value = auth.currentUser
                        profileRef =
                            firestore.collection("profiles").document(auth.currentUser!!.uid)
                        profileRef.set(Profile(firstname, lastname))
                    } else {
                        Log.e("FIREBASE", "${authResult.exception}")
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        _currentUser.value = auth.currentUser
                        profileRef =
                            firestore.collection("profiles").document(auth.currentUser!!.uid)
                    } else {
                        Log.e("FIREBASE", "${authResult.exception}")
                    }
                }
        }
    }

    fun updateProfile(firstname: String, lastname: String) {
        profileRef.set(Profile(firstname, lastname))
    }

    fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    fun saveNote(text: String) {
        val note = Note(text = text)
        notesRef.add(note)
    }
}