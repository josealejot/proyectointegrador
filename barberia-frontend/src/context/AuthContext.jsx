import { createContext, useContext, useState } from 'react'
import * as api from '../api/recursos.js'

const CLAVE_SESION = 'barberia_usuario'

export const AuthContext = createContext(null)

function leerSesion() {
  try {
    return JSON.parse(localStorage.getItem(CLAVE_SESION)) || null
  } catch {
    return null
  }
}

// Comparte el usuario con sesion iniciada con toda la aplicacion (useContext)
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leerSesion)

  function guardarSesion(datos) {
    localStorage.setItem(CLAVE_SESION, JSON.stringify(datos))
    setUsuario(datos)
  }

  async function iniciarSesion(correo, contrasena) {
    guardarSesion(await api.iniciarSesion(correo, contrasena))
  }

  async function registrar(datos) {
    guardarSesion(await api.registrarUsuario(datos))
  }

  function cerrarSesion() {
    localStorage.removeItem(CLAVE_SESION)
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ usuario, iniciarSesion, registrar, cerrarSesion }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
