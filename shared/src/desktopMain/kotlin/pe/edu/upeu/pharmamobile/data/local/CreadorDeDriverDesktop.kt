package pe.edu.upeu.pharmamobile.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import pe.edu.upeu.pharmamobile.db.PharmaDb

class CreadorDeDriverDesktop(private val archivo: File) : CreadorDeDriver {
    override fun crear(): SqlDriver {
        archivo.parentFile?.mkdirs()
        val nueva = !archivo.exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${archivo.absolutePath}")
        if (nueva) PharmaDb.Schema.create(driver)
        return driver
    }
}
