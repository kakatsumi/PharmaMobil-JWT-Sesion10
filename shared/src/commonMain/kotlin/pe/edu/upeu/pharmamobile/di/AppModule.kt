package pe.edu.upeu.pharmamobile.di

import io.ktor.client.engine.HttpClientEngine
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobile.data.repository.AuthRepositorioRest
import pe.edu.upeu.pharmamobile.data.repository.ProductoCatalogoRest
import pe.edu.upeu.pharmamobile.presentation.producto.CatalogoViewModel
import pe.edu.upeu.pharmamobile.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobile.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.ObtenerSesionGuardadaUseCase
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel

fun iniciarDependencias(almacen: AlmacenSeguro, motor: HttpClientEngine, baseUrl: String, depuracion: Boolean) {
    startKoin {
        modules(module {
            single<AlmacenSeguro> { almacen }
            single { crearHttpClient(motor, get(), baseUrl, depuracion) }
            single<AuthRepository> { AuthRepositorioRest(get(), get()) }
            factory { IniciarSesionUseCase(get()) }
            factory { ObtenerSesionGuardadaUseCase(get()) }
            factory { CerrarSesionUseCase(get()) }
            single { SesionViewModel(get(), get(), get()) }
            single { ProductoCatalogoRest(get()) }
            single { CatalogoViewModel(get(), get()) }
        })
    }
}
