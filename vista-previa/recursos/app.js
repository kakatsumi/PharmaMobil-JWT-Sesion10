const BASE = "http://localhost:8080/api/v1";
const $ = (id) => document.getElementById(id);
const mensaje = (texto) => { $("mensaje").textContent = texto; };
const nodo = (tipo, texto, clase = "") => {
  const n = document.createElement(tipo);
  n.textContent = texto;
  if (clase) n.className = clase;
  return n;
};
let token = "";
let usuario = "";
let permisos = new Set();
let db;

function abrirDb() {
  return new Promise((resolve, reject) => {
    const solicitud = indexedDB.open("pharmamobil-vista-previa", 1);
    solicitud.onupgradeneeded = () => {
      solicitud.result.createObjectStore("productos", { keyPath: "id" });
      solicitud.result.createObjectStore("categorias", { keyPath: "id" });
    };
    solicitud.onsuccess = () => resolve(solicitud.result);
    solicitud.onerror = () => reject(solicitud.error);
  });
}

function leerTodos(tabla) {
  return new Promise((resolve, reject) => {
    const req = db.transaction(tabla).objectStore(tabla).getAll();
    req.onsuccess = () => resolve(req.result);
    req.onerror = () => reject(req.error);
  });
}

function guardar(tabla, valor) {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(tabla, "readwrite");
    tx.objectStore(tabla).put(valor);
    tx.oncomplete = resolve;
    tx.onerror = () => reject(tx.error);
  });
}

function confirmarProducto(idLocal, servidor) {
  return new Promise((resolve, reject) => {
    const tx = db.transaction("productos", "readwrite");
    tx.objectStore("productos").delete(idLocal);
    tx.objectStore("productos").put({ ...servidor, pendiente: false });
    tx.oncomplete = resolve;
    tx.onerror = () => reject(tx.error);
  });
}

function reemplazar(tabla, datos, conservarPendientes = false) {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(tabla, "readwrite");
    const store = tx.objectStore(tabla);
    const req = store.getAll();
    req.onsuccess = () => {
      for (const fila of req.result) {
        if (!conservarPendientes || !fila.pendiente) store.delete(fila.id);
      }
      for (const fila of datos) store.put({ ...fila, ...(conservarPendientes ? { pendiente: false } : {}) });
    };
    tx.oncomplete = resolve;
    tx.onerror = () => reject(tx.error);
  });
}

async function api(ruta, opciones = {}) {
  const headers = { Accept: "application/json", ...(opciones.body ? { "Content-Type": "application/json" } : {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  let respuesta;
  try {
    respuesta = await fetch(`${BASE}${ruta}`, { ...opciones, headers, signal: AbortSignal.timeout(15000) });
  } catch (error) {
    $("conexion").textContent = "Sin conexión con PharmaSoft";
    throw new Error(error.name === "TimeoutError" ? "Se agotó el tiempo de espera" : "No se pudo conectar con PharmaSoft");
  }
  $("conexion").textContent = "PharmaSoft conectado";
  if (!respuesta.ok) {
    if (respuesta.status === 401) { token = ""; actualizarAcceso(); }
    const errores = { 400: "Datos inválidos", 401: "Sesión expirada: inicia sesión", 403: "No tienes permiso", 404: "Recurso inexistente", 409: "Conflicto: revisa si el registro ya existe", 500: "Error del servidor" };
    throw new Error(errores[respuesta.status] || `Error HTTP ${respuesta.status}`);
  }
  return respuesta.status === 204 ? null : respuesta.json();
}

async function paginado(ruta) {
  const todos = [];
  for (let pagina = 0; pagina < 100; pagina++) {
    const respuesta = await api(`${ruta}?pagina=${pagina}&tamanio=100`);
    todos.push(...respuesta.contenido);
    if (respuesta.ultima) return todos;
  }
  throw new Error("El catálogo supera el límite de páginas de esta vista previa");
}

function perfilGuardado() {
  try {
    const perfil = JSON.parse(localStorage.getItem("pharmamobil-perfil"));
    return perfil?.expira > Date.now() ? perfil : null;
  } catch { return null; }
}

function puede(permiso) { return permisos.has(permiso) || (!token && perfilGuardado()?.permisos?.includes(permiso)); }

function actualizarAcceso() {
  const perfil = perfilGuardado();
  $("login-form").hidden = Boolean(token);
  $("salir").hidden = !token && !perfil;
  $("usuario-actual").textContent = token ? `Sesión: ${usuario}` : perfil ? `Datos locales de ${perfil.usuario}; inicia sesión para sincronizar` : "Sin sesión activa";
  $("nuevo-producto").hidden = !puede("CATALOGO_ESCRIBIR");
  $("nuevo-cliente").hidden = !token || !puede("CLIENTES_ESCRIBIR");
  $("nueva-venta").hidden = !token || !puede("VENTAS_REGISTRAR");
}

async function mostrarProductos() {
  const productos = (await leerTodos("productos")).sort((a, b) => a.nombre.localeCompare(b.nombre));
  const categorias = await leerTodos("categorias");
  const nombres = new Map(categorias.map((c) => [c.id, c.nombre]));
  $("pendientes").textContent = `${productos.filter((p) => p.pendiente).length} producto(s) pendiente(s)`;
  const lista = $("lista-productos");
  lista.replaceChildren();
  if (!productos.length) lista.append(nodo("p", "No hay productos guardados. Inicia sesión y sincroniza al menos una vez."));
  for (const p of productos) {
    const tarjeta = nodo("article", "", "elemento");
    const datos = nodo("div", "");
    datos.append(nodo("strong", `${p.nombre} · S/ ${Number(p.precio).toFixed(2)}`));
    datos.append(nodo("p", `Stock: ${p.stock} · ${nombres.get(p.categoriaId) || `Categoría ${p.categoriaId}`} · ${p.estado ? "Activo" : "Inactivo"}`));
    if (p.pendiente) datos.append(nodo("span", `Pendiente · ID ${p.id}`, "etiqueta"));
    tarjeta.append(datos);
    if (token && puede("CATALOGO_ESCRIBIR") && p.id > 0) {
      const acciones = nodo("div", "", "acciones");
      const editar = nodo("button", "Editar", "secundario");
      editar.onclick = () => abrirProducto(p);
      acciones.append(editar);
      if (p.estado) {
        const borrar = nodo("button", "Dar de baja", "secundario");
        borrar.onclick = async () => {
          if (!confirm(`¿Dar de baja a ${p.nombre}?`)) return;
          try { await api(`/productos/${p.id}`, { method: "DELETE" }); await sincronizar(); }
          catch (e) { mensaje(e.message); }
        };
        acciones.append(borrar);
      }
      tarjeta.append(acciones);
    }
    lista.append(tarjeta);
  }
  const selector = $("producto-form").elements.categoriaId;
  const anterior = selector.value;
  selector.replaceChildren();
  for (const c of categorias.filter((c) => c.estado)) {
    const opcion = document.createElement("option");
    opcion.value = String(c.id);
    opcion.textContent = c.nombre;
    selector.append(opcion);
  }
  selector.value = anterior || selector.options[0]?.value || "";
}

function abrirProducto(p = null) {
  const form = $("producto-form");
  form.reset();
  form.elements.id.value = p?.id || "";
  form.elements.nombre.value = p?.nombre || "";
  form.elements.precio.value = p?.precio || "";
  form.elements.stock.value = p?.stock ?? "";
  if (p) form.elements.categoriaId.value = String(p.categoriaId);
  $("producto-titulo").textContent = p ? "Editar producto" : "Registrar producto";
  form.hidden = false;
}

async function sincronizar() {
  if (!token) { mensaje("Inicia sesión para sincronizar. Los productos guardados siguen disponibles."); return; }
  const boton = $("sincronizar");
  boton.disabled = true;
  try {
    const pendientes = (await leerTodos("productos")).filter((p) => p.pendiente).sort((a, b) => b.id - a.id);
    for (const p of pendientes) {
      const creado = await api("/productos", { method: "POST", body: JSON.stringify({ nombre: p.nombre, precio: p.precio, stock: p.stock, estado: p.estado, categoriaId: p.categoriaId }) });
      if (!Number.isInteger(creado.id) || creado.id <= 0) throw new Error("PharmaSoft no confirmó un ID válido");
      await confirmarProducto(p.id, creado);
      await mostrarProductos();
    }
    const servidor = await paginado("/productos");
    await reemplazar("productos", servidor, true);
    const categorias = await api("/categorias");
    await reemplazar("categorias", categorias);
    await mostrarProductos();
    mensaje("Sincronización completada con PharmaSoft");
  } catch (e) {
    await mostrarProductos();
    mensaje(`${e.message}. Los pendientes permanecen guardados.`);
  } finally { boton.disabled = false; }
}

async function mostrarClientes() {
  const lista = $("lista-clientes");
  lista.replaceChildren();
  try {
    if (!token) throw new Error("Inicia sesión para consultar clientes");
    const clientes = await paginado("/clientes");
    if (!clientes.length) lista.append(nodo("p", "No hay clientes registrados."));
    for (const c of clientes) {
      const tarjeta = nodo("article", "", "elemento");
      const datos = nodo("div", "");
      datos.append(nodo("strong", `${c.nombres} ${c.apellidos} · ${c.dni}`));
      datos.append(nodo("p", `${c.email} · ${c.estado ? "Activo" : "Inactivo"}`));
      tarjeta.append(datos);
      if (puede("CLIENTES_ESCRIBIR")) {
        const acciones = nodo("div", "", "acciones");
        const editar = nodo("button", "Editar", "secundario");
        editar.onclick = () => {
          const f = $("cliente-form"); f.reset();
          for (const campo of ["id", "dni", "nombres", "apellidos", "email", "telefono", "direccion"]) f.elements[campo].value = c[campo] ?? "";
          f.dataset.estado = String(c.estado);
          f.hidden = false;
        };
        acciones.append(editar);
        if (c.estado) {
          const baja = nodo("button", "Dar de baja", "secundario");
          baja.onclick = async () => {
            if (!confirm(`¿Dar de baja a ${c.nombres} ${c.apellidos}?`)) return;
            try { await api(`/clientes/${c.id}`, { method: "DELETE" }); await mostrarClientes(); }
            catch (e) { mensaje(e.message); }
          };
          acciones.append(baja);
        }
        tarjeta.append(acciones);
      }
      lista.append(tarjeta);
    }
  } catch (e) { mensaje(e.message); }
}

async function mostrarVentas() {
  const lista = $("lista-ventas");
  lista.replaceChildren();
  try {
    if (!token) throw new Error("Inicia sesión para consultar ventas");
    const ventas = await paginado("/ventas");
    if (!ventas.length) lista.append(nodo("p", "No hay ventas registradas."));
    for (const v of ventas) {
      const tarjeta = nodo("article", "", "elemento");
      const datos = nodo("div", "");
      datos.append(nodo("strong", `Venta #${v.id} · ${v.clienteNombre}`));
      datos.append(nodo("p", `S/ ${Number(v.total).toFixed(2)} · ${v.estado} · ${v.fecha}`));
      tarjeta.append(datos);
      lista.append(tarjeta);
    }
  } catch (e) { mensaje(e.message); }
}

$("login-form").addEventListener("submit", async (ev) => {
  ev.preventDefault();
  const form = ev.currentTarget;
  try {
    const respuesta = await api("/auth/login", { method: "POST", body: JSON.stringify({ username: form.elements.usuario.value.trim(), password: form.elements.clave.value }) });
    token = respuesta.token;
    usuario = respuesta.username;
    permisos = new Set(respuesta.permisos);
    localStorage.setItem("pharmamobil-perfil", JSON.stringify({ usuario, permisos: [...permisos], expira: Date.now() + respuesta.expiraEnSegundos * 1000 }));
    form.elements.clave.value = "";
    actualizarAcceso();
    await sincronizar();
  } catch (e) { mensaje(e.message); }
});

$("salir").onclick = () => {
  token = ""; usuario = ""; permisos.clear();
  localStorage.removeItem("pharmamobil-perfil");
  actualizarAcceso();
  mensaje("Sesión cerrada. Los datos locales se conservan en este navegador.");
};
$("sincronizar").onclick = sincronizar;
$("nuevo-producto").onclick = () => abrirProducto();
$("cancelar-producto").onclick = () => { $("producto-form").hidden = true; };
$("producto-form").addEventListener("submit", async (ev) => {
  ev.preventDefault();
  const form = ev.currentTarget;
  const id = Number(form.elements.id.value);
  const datos = {
    nombre: form.elements.nombre.value.trim(), precio: Number(form.elements.precio.value),
    stock: Number(form.elements.stock.value), estado: true,
    categoriaId: Number(form.elements.categoriaId.value)
  };
  if (!puede("CATALOGO_ESCRIBIR") || !Number.isInteger(datos.categoriaId) || datos.categoriaId <= 0) {
    mensaje("Inicia sesión y sincroniza las categorías antes de registrar productos."); return;
  }
  try {
    if (id > 0) {
      await api(`/productos/${id}`, { method: "PUT", body: JSON.stringify(datos) });
      form.hidden = true;
      await sincronizar();
    } else {
      const existentes = await leerTodos("productos");
      const idLocal = Math.min(0, ...existentes.map((p) => p.id)) - 1;
      await guardar("productos", { id: idLocal, ...datos, pendiente: true });
      form.hidden = true;
      await mostrarProductos();
      mensaje(`Producto guardado localmente con ID ${idLocal}.`);
      if (token) await sincronizar();
    }
  } catch (e) { mensaje(e.message); }
});

$("actualizar-clientes").onclick = mostrarClientes;
$("nuevo-cliente").onclick = () => { $("cliente-form").reset(); $("cliente-form").elements.id.value = ""; $("cliente-form").dataset.estado = "true"; $("cliente-form").hidden = false; };
$("cancelar-cliente").onclick = () => { $("cliente-form").hidden = true; };
$("cliente-form").addEventListener("submit", async (ev) => {
  ev.preventDefault();
  const f = ev.currentTarget;
  const id = Number(f.elements.id.value);
  const datos = Object.fromEntries(["dni", "nombres", "apellidos", "email", "telefono", "direccion"].map((k) => [k, f.elements[k].value.trim()]));
  datos.estado = f.dataset.estado !== "false";
  try {
    await api(id > 0 ? `/clientes/${id}` : "/clientes", { method: id > 0 ? "PUT" : "POST", body: JSON.stringify(datos) });
    f.hidden = true; await mostrarClientes(); mensaje("Cliente guardado en PharmaSoft");
  } catch (e) { mensaje(e.message); }
});

$("actualizar-ventas").onclick = mostrarVentas;
$("nueva-venta").onclick = () => { $("venta-form").reset(); $("venta-form").hidden = false; };
$("cancelar-venta").onclick = () => { $("venta-form").hidden = true; };
$("venta-form").addEventListener("submit", async (ev) => {
  ev.preventDefault();
  const f = ev.currentTarget;
  const datos = { clienteId: Number(f.elements.clienteId.value), detalles: [{ productoId: Number(f.elements.productoId.value), cantidad: Number(f.elements.cantidad.value) }] };
  try {
    await api("/ventas", { method: "POST", body: JSON.stringify(datos) });
    f.hidden = true; await mostrarVentas(); mensaje("Venta registrada en PharmaSoft");
  } catch (e) { mensaje(e.message); }
});

document.querySelectorAll("nav button").forEach((boton) => {
  boton.onclick = () => {
    for (const b of document.querySelectorAll("nav button")) b.removeAttribute("aria-current");
    boton.setAttribute("aria-current", "page");
    for (const panel of document.querySelectorAll(".panel")) panel.hidden = panel.id !== boton.dataset.tab;
    if (boton.dataset.tab === "clientes") mostrarClientes();
    if (boton.dataset.tab === "ventas") mostrarVentas();
  };
});

window.addEventListener("online", () => { $("conexion").textContent = "Red disponible; pulsa Sincronizar"; });
window.addEventListener("offline", () => { $("conexion").textContent = "Sin conexión; usando datos guardados"; });

try {
  db = await abrirDb();
  actualizarAcceso();
  await mostrarProductos();
  $("conexion").textContent = navigator.onLine ? "Sin comprobar PharmaSoft" : "Sin conexión; usando datos guardados";
} catch (error) {
  mensaje(`No se pudo abrir IndexedDB: ${error.message}`);
}
