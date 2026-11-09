import axios from 'axios'

// endPoint base del backend Spring Boot
export const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const http = axios.create({ baseURL: API_URL })

// Convierte cualquier error de axios en un mensaje para mostrar al usuario.
// El backend responde los errores de negocio como texto plano.
export function mensajeError(error) {
  if (error?.response) {
    const datos = error.response.data
    if (typeof datos === 'string' && datos.trim()) return datos
    if (datos?.message) return datos.message
    if (error.response.status === 400) return 'Los datos enviados no son válidos.'
    return 'El servidor respondió con un error. Intenta de nuevo.'
  }
  return 'No hay conexión con el servidor. Verifica que el backend esté encendido.'
}

export default http
