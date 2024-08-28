package syntax.com.firebaeauthexample

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class FirebaseViewModel : ViewModel() {

    private val auth = Firebase.auth

    private var _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser


    fun register(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        _currentUser.value = auth.currentUser
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
                    } else {
                        Log.e("FIREBASE", "${authResult.exception}")
                    }
                }
        }
    }

    fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}