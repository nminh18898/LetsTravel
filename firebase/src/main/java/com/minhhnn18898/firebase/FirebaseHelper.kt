package com.minhhnn18898.firebase

import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun String.gsUriToHttpsUrl(): String =
    suspendCoroutine { cont ->
        val gsReference = Firebase.storage.getReferenceFromUrl(this)
        gsReference.downloadUrl
            .addOnSuccessListener {
                cont.resume(it.toString())
            }
            .addOnFailureListener {
                cont.resume("")
            }
    }

suspend fun List<String>.gsUriToHttpsUrl(): List<String> {
    return this.map { it.gsUriToHttpsUrl() }
}