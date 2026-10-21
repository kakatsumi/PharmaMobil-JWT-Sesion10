package pe.edu.upeu.pharmamobile

import android.os.Bundle
import pe.edu.upeu.pharmamobile.di.iniciarAndroid
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import android.widget.Toast
import java.net.URI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val depuracion = applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
        val preferencias = getSharedPreferences("backend_config", MODE_PRIVATE)
        val baseUrl = preferencias.getString("base_url", null) ?: "http://10.0.2.2:8080/"
        iniciarAndroid(applicationContext, depuracion, baseUrl)
        setContent {
            var mostrarConfiguracion by remember { mutableStateOf(false) }
            var url by remember { mutableStateOf(baseUrl) }
            var error by remember { mutableStateOf<String?>(null) }
            App(onConfigurarServidor = { mostrarConfiguracion = true })
            if (mostrarConfiguracion) {
                AlertDialog(
                    onDismissRequest = { mostrarConfiguracion = false },
                    title = { Text("Servidor PharmaSoft") },
                    text = {
                        androidx.compose.foundation.layout.Column {
                            Text("Emulador: 10.0.2.2. Celular: IP local de tu computadora. Reabre la app después de guardar.")
                            OutlinedTextField(url, { url = it; error = null }, label = { Text("URL del backend") })
                            error?.let { Text(it) }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (urlValida(url, depuracion)) {
                                preferencias.edit().putString("base_url", url.trimEnd('/') + "/").apply()
                                mostrarConfiguracion = false
                                Toast.makeText(this, "Servidor guardado. Cierra y vuelve a abrir PharmaMobil.", Toast.LENGTH_LONG).show()
                            } else error = "Usa HTTPS o, en depuración, HTTP con una IP privada de tu red."
                        }) { Text("Guardar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarConfiguracion = false }) { Text("Cancelar") } }
                )
            }
        }
    }
}

private fun urlValida(valor: String, depuracion: Boolean): Boolean = try {
    val uri = URI(valor.trim())
    val host = uri.host.orEmpty()
    val local = host == "10.0.2.2" || host == "localhost" || host == "127.0.0.1" ||
        host.startsWith("192.168.") || host.startsWith("10.") ||
        Regex("172\\.(1[6-9]|2[0-9]|3[01])\\..+").matches(host)
    uri.userInfo == null && host.isNotBlank() && uri.port in 1..65535 &&
        (uri.scheme == "https" || (depuracion && uri.scheme == "http" && local))
} catch (_: Exception) { false }

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
