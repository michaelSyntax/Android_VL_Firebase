package syntax.com.firebaeauthexample

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import syntax.com.firebaeauthexample.model.Profile

class FirebaseViewModel: ViewModel() {

    /**
     * Instanz von Firebase Authentication.
     * Ersetzt in diesem Fall ein Repository.
     */
    private val firebaseAuth = FirebaseAuth.getInstance()

    /**
     * Instanz von Firebase Firestore.
     */
    private val fireStore = FirebaseFirestore.getInstance()

    /**
     * Instanz und Referenz von Firebase Storage.
     */
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    /**
     * LiveData um den aktuellen User zu halten.
     * Initialwert ist in diesem Fall firebaseAuth.currentUser.
     * Das gewährleistet, dass der User sofort wieder eingeloggt ist sollte er sich bereits einmal eingeloggt haben.
     * LiveData kann auch "null" sein (Wenn der User nicht eingeloggt ist).
     */
    private var _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    /**
     * profileRef ist lateinit, da sie vom currentUser abhängt.
     * Wird gesetzt, sobald User eingeloggt ist.
     */
    lateinit var profileRef: DocumentReference

    /**
     * "Cookie-Funktion": Wenn ein User die App startet und bereits eingeloggt ist.
     * Dann müssen wir auch die profileRef Variable setzen, damit App nicht abstürzt.
     */
    init {
        if (firebaseAuth.currentUser != null) {
            profileRef = fireStore.collection("profiles").document(firebaseAuth.currentUser!!.uid)
        }
    }

    /**
     * Funktion um das Profil eines Users zu updaten.
     */
    fun updateProfile(profile: Profile) {
        profileRef.set(profile)
    }


    /**
     * Funktion um neuen User zu erstellen.
     * CompleteListener sorgt dafür, dass wir anschließend feststellen können, ob das funktioniert hat.
     *
     * Wenn Registrierung erfolgreich ist:
     *      - Senden wir eine E-Mail, um die E-Mail-Adresse zu bestätigen.
     *      - Die Profil-Referenz wird gesetzt, da diese vom aktuellen User abhängt.
     *      - Ein neues, leeres Profil wird für jeden User erstellt, der zum ersten Mal einen Account für die App anlegt.
     *      - Danach führen wir logout Funktion aus, da beim Erstellen eines Users dieser sofort eingeloggt wird.
     *
     * Wenn Registrierung nicht erfolgreich ist:
     *      - Log, falls Fehler beim Erstellen eines Users auftritt.
     */
    fun register(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                firebaseAuth.currentUser?.sendEmailVerification()
                profileRef = fireStore.collection("profiles").document(firebaseAuth.currentUser!!.uid)
                profileRef.set(Profile())
                logout()
            } else {
                Log.e("FIREBASE", "${authResult.exception}")
            }
        }
    }

    /**
     * Funktion um User einzuloggen.
     * CompleteListener sorgt dafür, dass wir anschließend feststellen können, ob das funktioniert hat.
     *
     * Überprüfung, ob User bereits E-Mail verifiziert hat.
     *      Wenn E-Mail verifiziert:
     *          - Wird LiveData mit dem eingeloggten User befüllt.
     *          - Das triggert dann die Navigation im LoginFragment.
     *          - Die Profil-Referenz wird jetzt gesetzt, da diese vom aktuellen User abhängt.
     *
     *      Wenn User zwar existiert und Eingaben stimmen aber User seine E-Mail noch nicht bestätigt hat:
     *          - Wird User wieder ausgeloggt und eine Fehlermeldung ausgegeben.
     *
     *  Log, falls Fehler beim Login eines Users auftritt.
     */
    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                if (firebaseAuth.currentUser!!.isEmailVerified) {
                    profileRef = fireStore.collection("profiles").document(firebaseAuth.currentUser!!.uid)
                    _currentUser.value = firebaseAuth.currentUser
                } else {
                    Log.e("FIREBASE", "User not verified")
                    logout()
                }
            } else {
                Log.e("FIREBASE", "${authResult.exception}")
            }
        }
    }

    /**
     * Funktion um Passwort-Vergessen E-Mail zu senden.
     */
    fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    /**
     * Funktion um User auszuloggen.
     *
     * 1. Erst wird die Firebase-Funktion signOut aufgerufen.
     * 2. Danach wird der Wert der currentUser LiveData auf den aktuellen Wert des Firebase-CurrentUser gesetzt.
     *    Nach Logout ist dieser Wert null, also ist auch in der LiveData danach der Wert null gespeichert.
     *    Dies triggert die Navigation aus dem HomeFragment zurück zum LoginFragment.
     */
    fun logout() {
        firebaseAuth.signOut()
        _currentUser.value = firebaseAuth.currentUser
    }

    /**
     * Funktion um Bild in den Firebase Storage hochzuladen.
     *
     * 1. Erstellen einer Referenz und des Upload-Tasks.
     * 2. Wenn Upload-Task ausgeführt und erfolgreich ist, wird die Download-Url
     *    des Bildes an die setUserImage Funktion weitergegeben.
     */
    fun uploadImage(uri: Uri) {
        val imageRef = storageRef.child("images/${firebaseAuth.currentUser!!.uid}/profilePic")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnCompleteListener {
            imageRef.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    setUserImage(it.result)
                }
            }
        }
    }

    /**
     * Funktion um Url zu neue hochgeladenem Bild im Firestore dem aktuellen Userprofil hinzuzufügen.
     */
    private fun setUserImage(uri: Uri) {
        profileRef.update("profilePicture", uri.toString())
    }
}