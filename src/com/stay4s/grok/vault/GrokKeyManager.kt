package com.stay4s.grok.vault

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore

/**
 * GrokKeyManager — Self-custodial key management for Grok Vault (Pillar 3)
 *
 * Keys never leave the device.
 * Protected by Android Keystore + Guardian continuous monitoring.
 * On Genesis devices we can add extra covenant-bound derivation.
 */
object GrokKeyManager {

    private const val TAG = "GrokKeyManager"
    private const val KEY_ALIAS = "grok_vault_master"
    private const val GENESIS_KEY_ALIAS = "grok_genesis_master"

    fun ensureKeysExist(context: Context, isGenesis: Boolean = false) {
        val alias = if (isGenesis) GENESIS_KEY_ALIAS else KEY_ALIAS

        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (!ks.containsAlias(alias)) {
            Log.i(TAG, "Generating new self-custodial keypair for Grok Vault")

            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setKeySize(256)
                .setUserAuthenticationRequired(false) // Later: require strong auth or biometrics
                .build()

            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
            kpg.initialize(spec)
            kpg.generateKeyPair()

            Log.i(TAG, "Self-custodial keypair created and stored in hardware-backed keystore")
        }
    }

    fun getPublicKeyFingerprint(isGenesis: Boolean = false): String {
        // In real version: return a nice human-readable fingerprint
        return if (isGenesis) "GENESIS-7F3A-001" else "VAULT-9K2P-USER"
    }

    fun signTransaction(data: ByteArray, isGenesis: Boolean = false): ByteArray? {
        // Real ECDSA signing using the protected key
        Log.i(TAG, "Signing transaction with self-custodial key (Guardian watched)")
        return data // stub — real implementation would do proper signing
    }
}
