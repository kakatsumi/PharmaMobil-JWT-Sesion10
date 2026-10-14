package pe.edu.upeu.pharmamobile.presentation.sesion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(estado: SesionUiState, cambiarUsuario: (String) -> Unit, cambiarClave: (String) -> Unit, iniciar: () -> Unit, modoDemostracion: Boolean = false) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PharmaMobil", style = MaterialTheme.typography.headlineLarge)
        Text("Inicia sesión para continuar", modifier = Modifier.padding(bottom = 24.dp))
        if (modoDemostracion) {
            Text("Vista de demostración · usuario: demo · contraseña: demo", modifier = Modifier.padding(bottom = 12.dp))
        }
        OutlinedTextField(estado.usuario, cambiarUsuario, label = { Text("Usuario") },
            singleLine = true, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(estado.clave, cambiarClave, label = { Text("Contraseña") },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            enabled = !estado.cargando, modifier = Modifier.fillMaxWidth())
        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp)) }
        Button(onClick = iniciar, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            if (estado.cargando) CircularProgressIndicator() else Text("Iniciar sesión")
        }
    }
}
