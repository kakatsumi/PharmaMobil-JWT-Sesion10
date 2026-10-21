package pe.edu.upeu.pharmamobile.di

import io.ktor.client.engine.HttpClientEngine
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pe.edu.upeu.pharmamobile.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobile.data.local.CreadorDeDriver
import pe.edu.upeu.pharmamobile.data.local.ProductoLocal
import pe.edu.upeu.pharmamobile.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobile.data.repository.AuthRepositorioRest
import pe.edu.upeu.pharmamobile.data.repository.ProductoCatalogoRest
import pe.edu.upeu.pharmamobile.data.repository.ProductoApi
import pe.edu.upeu.pharmamobile.data.repository.ProductoRepositorioOffline
import pe.edu.upeu.pharmamobile.data.repository.ProductoRepositorioEnRed
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobile.db.PharmaDb
import pe.edu.upeu.pharmamobile.data.repository.ClienteRest
import pe.edu.upeu.pharmamobile.data.repository.VentaRest
import pe.edu.upeu.pharmamobile.presentation.cliente.ClienteViewModel
import pe.edu.upeu.pharmamobile.presentation.pedido.VentaViewModel
import pe.edu.upeu.pharmamobile.presentation.producto.CatalogoViewModel
import pe.edu.upeu.pharmamobile.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobile.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobile.domain.usecase.ObtenerSesionGuardadaUseCase
import pe.edu.upeu.pharmamobile.presentation.sesion.SesionViewModel

fun iniciarDependencias(almacen: AlmacenSeguro, motor: HttpClientEngine, baseUrl: String, depuracion: Boolean, creadorDriver: CreadorDeDriver? = null) {
    val local = creadorDriver?.let { ProductoLocal(PharmaDb(it.crear())) }
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
            single<ProductoApi> { get<ProductoCatalogoRest>() }
            single<ProductoRepository> {
                if (local == null) ProductoRepositorioEnRed(get()) else ProductoRepositorioOffline(local, get())
            }
            single { CatalogoViewModel(get(), get()) }
            single { ClienteRest(get()) }
            single { ClienteViewModel(get(), get()) }
            single { VentaRest(get()) }
            single { VentaViewModel(get(), get()) }
        })
    }
}
