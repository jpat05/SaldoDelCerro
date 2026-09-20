package com.movil.saldodelcerro.auth

import java.security.MessageDigest

object AuthManager {
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
        return hashPassword(inputPassword) == storedHash
    }
}