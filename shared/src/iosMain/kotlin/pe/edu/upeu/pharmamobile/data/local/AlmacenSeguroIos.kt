package pe.edu.upeu.pharmamobile.data.local

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

/** Generic-password item scoped to this app; no token leaves Keychain. */
class AlmacenSeguroIos : AlmacenSeguro {
    private val service = "pe.edu.upeu.pharmamobile"
    private val account = "jwt"

    private fun consulta(): CFMutableDictionaryRef? {
        val q = CFDictionaryCreateMutable(kCFAllocatorDefault, 4, null, null)
        CFDictionaryAddValue(q, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(q, kSecAttrService, CFBridgingRetain(service))
        CFDictionaryAddValue(q, kSecAttrAccount, CFBridgingRetain(account))
        return q
    }

    override fun guardarToken(token: String) {
        borrarToken()
        val q = consulta()
        val data = token.encodeToByteArray().usePinned {
            NSData.dataWithBytes(it.addressOf(0), token.encodeToByteArray().size.toULong())
        }
        CFDictionaryAddValue(q, kSecValueData, CFBridgingRetain(data))
        CFDictionaryAddValue(q, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
        check(SecItemAdd(q, null) == errSecSuccess) { "No se pudo guardar la sesión en Keychain" }
    }

    override fun leerToken(): String? = memScoped {
        val q = consulta()
        CFDictionaryAddValue(q, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(q, kSecMatchLimit, kSecMatchLimitOne)
        val result = alloc<CFTypeRefVar>()
        when (SecItemCopyMatching(q, result.ptr)) {
            errSecItemNotFound -> null
            errSecSuccess -> {
                val data = CFBridgingRelease(result.value) as? NSData
                    ?: error("Keychain devolvió datos inválidos")
                NSString.create(data, NSUTF8StringEncoding)?.toString()
                    ?: error("Token ilegible en Keychain")
            }
            else -> error("No se pudo leer la sesión de Keychain")
        }
    }

    override fun borrarToken() {
        val status = SecItemDelete(consulta())
        check(status == errSecSuccess || status == errSecItemNotFound) { "No se pudo borrar la sesión de Keychain" }
    }
}
