package syntax.com.firebaeauthexample.model

/**
 * Datenklasse um ein Profil abzubilden.
 */
data class Profile(
    val firstName: String = "",
    val lastName: String = "",
    val profilePicture: String = ""
)