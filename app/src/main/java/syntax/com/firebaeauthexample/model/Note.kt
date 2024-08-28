package syntax.com.firebaeauthexample.model

import com.google.firebase.firestore.DocumentId

data class Note(
    @DocumentId
    val id: String = "",
    val text: String = ""
)
