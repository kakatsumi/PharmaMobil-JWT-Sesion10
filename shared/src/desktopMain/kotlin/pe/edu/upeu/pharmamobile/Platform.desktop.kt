package pe.edu.upeu.pharmamobile

private class DesktopPlatform : Platform {
    override val name = "Desktop JVM"
}

actual fun getPlatform(): Platform = DesktopPlatform()
