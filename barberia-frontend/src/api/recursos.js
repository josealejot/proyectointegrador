import http from './http.js'

// Operaciones CRUD contra un controlador del backend (por ejemplo "/clientes")
export const consultarTodos = (endpoint) => http.get(endpoint).then((r) => r.data)
export const adicionar = (endpoint, datos) => http.post(endpoint, datos).then((r) => r.data)
export const modificar = (endpoint, id, datos) =>
  http.put(`${endpoint}/${id}`, datos).then((r) => r.data)
export const eliminar = (endpoint, id) => http.delete(`${endpoint}/${id}`).then((r) => r.data)

// Acceso
export const iniciarSesion = (correo, contrasena) =>
  http.post('/auth/login', { correo, contrasena }).then((r) => r.data)
export const registrarUsuario = (datos) => http.post('/auth/registro', datos).then((r) => r.data)
export const consultarCatalogos = () => http.get('/auth/catalogos').then((r) => r.data)

// Consulta nativa: ingresos por barbero
export const consultarResumenBarberos = () =>
  http.get('/citas/resumen-barberos').then((r) => r.data)
