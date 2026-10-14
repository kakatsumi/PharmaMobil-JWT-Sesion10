package pe.edu.upeu.pharmamobile.di

import android.content.Context
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.context.GlobalContext
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguroAndroid

fun iniciarAndroid(context: Context, depuracion: Boolean) {
    if (GlobalContext.getOrNull() == null) {
        iniciarDependencias(AlmacenSeguroAndroid(context), OkHttp.create(), "http://10.0.2.2:8080/", depuracion)
    }
}
