#define _POSIX_C_SOURCE 200809L
#include <errno.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/* Native one-click launcher: find ver-movil.sh next to this executable. */
int main(int argc, char **argv) {
    char executable[PATH_MAX];
    ssize_t length = readlink("/proc/self/exe", executable, sizeof(executable) - 1);
    if (length < 0 || length >= (ssize_t)sizeof(executable) - 1) {
        perror("No se pudo localizar PharmaMobil");
        return 1;
    }
    executable[length] = '\0';
    char *separator = strrchr(executable, '/');
    if (separator == NULL) return 1;
    separator[1] = '\0';

    char script[PATH_MAX];
    int size = snprintf(script, sizeof(script), "%sver-movil.sh", executable);
    if (size < 0 || size >= (int)sizeof(script)) return 1;

    char **arguments = calloc((size_t)argc + 1, sizeof(char *));
    if (arguments == NULL) return 1;
    arguments[0] = script;
    for (int i = 1; i < argc; ++i) arguments[i] = argv[i];
    execv(script, arguments);
    fprintf(stderr, "No se pudo abrir PharmaMobil: %s\n", strerror(errno));
    free(arguments);
    return 1;
}
