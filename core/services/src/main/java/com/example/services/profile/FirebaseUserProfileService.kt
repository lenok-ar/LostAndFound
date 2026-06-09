package com.example.services.profile

import com.example.services.auth.TokenStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserProfileService @Inject constructor(
    private val tokenStorage: TokenStorage
) : UserProfileService {

    override fun saveCurrentProfile(name: String, email: String) {
        ensureFirebaseUser(
            onReady = { userId ->
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    users().document(userId).set(
                        CloudUserProfile(userId, name, email, token, System.currentTimeMillis())
                    )
                }
            }
        )
    }

    override fun updateFcmToken(token: String) {
        ensureFirebaseUser(
            onReady = { userId ->
                users().document(userId).update(
                    mapOf("fcmToken" to token, "updatedAt" to System.currentTimeMillis())
                )
            }
        )
    }

    override fun observeCurrentProfile(
        onChanged: (CloudUserProfile?) -> Unit,
        onError: (Throwable) -> Unit
    ): () -> Unit {
        var registration: ListenerRegistration? = null
        var stopped = false
        ensureFirebaseUser(
            onReady = { userId ->
                if (!stopped) {
                    registration = users().document(userId).addSnapshotListener { snapshot, error ->
                        if (error != null) onError(error)
                        else onChanged(snapshot?.toObject(CloudUserProfile::class.java))
                    }
                }
            },
            onError = onError
        )
        return {
            stopped = true
            registration?.remove()
        }
    }

    private fun ensureFirebaseUser(
        onReady: (String) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        if (tokenStorage.user() == null) return
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let(onReady) ?: auth.signInAnonymously()
            .addOnSuccessListener { result ->
                result.user?.uid?.let(onReady)
                    ?: onError(IllegalStateException("Firebase user ID is missing"))
            }
            .addOnFailureListener(onError)
    }

    private fun users() = FirebaseFirestore.getInstance().collection("users")
}
