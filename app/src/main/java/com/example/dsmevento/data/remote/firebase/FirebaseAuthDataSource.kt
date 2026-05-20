package com.example.dsmevento.data.remote.firebase

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.dsmevento.data.model.User

class FirebaseAuthDataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(
        email: String,
        password: String,
        displayName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid.isNullOrBlank()) {
                    onError("No se pudo obtener el UID del usuario.")
                    return@addOnSuccessListener
                }

                ensureUserDocument(
                    uid = uid,
                    email = email,
                    displayName = displayName,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo registrar el usuario.")
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo iniciar sesión.")
            }
    }

    fun loginWithGithub(
        activity: Activity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("allow_signup", "true")

        auth.startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener {
                val user = auth.currentUser
                val uid = user?.uid
                val email = user?.email.orEmpty()
                val displayName = user?.displayName?.takeIf { it.isNotBlank() }
                    ?: email.substringBefore("@").ifBlank { "GitHub User" }

                if (uid.isNullOrBlank()) {
                    onError("No se pudo completar el inicio con GitHub.")
                    return@addOnSuccessListener
                }

                ensureUserDocument(
                    uid = uid,
                    email = email,
                    displayName = displayName,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo iniciar sesión con GitHub.")
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUid(): String? = auth.currentUser?.uid

    fun loadCurrentUserProfile(onResult: (User?) -> Unit) {
        val uid = currentUid()
        if (uid.isNullOrBlank()) {
            onResult(null)
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)?.copy(uid = snapshot.id)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    private fun ensureUserDocument(
        uid: String,
        email: String,
        displayName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = User(
            uid = uid,
            createdAt = System.currentTimeMillis(),
            displayName = displayName,
            email = email,
            role = "user"
        )

        db.collection("users")
            .document(uid)
            .set(user, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "No se pudo guardar el perfil del usuario.")
            }
    }
}