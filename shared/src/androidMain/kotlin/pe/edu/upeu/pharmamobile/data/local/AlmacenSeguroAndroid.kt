package pe.edu.upeu.pharmamobile.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** AES-GCM: the key remains in AndroidKeyStore; preferences contain only IV and ciphertext. */
class AlmacenSeguroAndroid(context: Context) : AlmacenSeguro {
    private val prefs = context.getSharedPreferences("sesion_cifrada", Context.MODE_PRIVATE)
    private val alias = "pharmamobil_jwt_aes"
    private val key: SecretKey
        get() {
            val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            (store.getKey(alias, null) as? SecretKey)?.let { return it }
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
                init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build())
            }.generateKey()
        }

    override fun guardarToken(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key) }
        val encrypted = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val data = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        check(prefs.edit().putString("jwt", "$iv:$data").commit()) { "No se pudo guardar la sesión" }
    }

    override fun leerToken(): String? {
        val value = prefs.getString("jwt", null) ?: return null
        val parts = value.split(':', limit = 2)
        require(parts.size == 2) { "Sesión cifrada dañada" }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, Base64.decode(parts[0], Base64.NO_WRAP)))
        }
        return cipher.doFinal(Base64.decode(parts[1], Base64.NO_WRAP)).toString(Charsets.UTF_8)
    }

    override fun borrarToken() {
        check(prefs.edit().remove("jwt").commit()) { "No se pudo borrar la sesión" }
    }
}
