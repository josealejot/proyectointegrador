import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Ruta restringida: sin sesion envia al login.
// Si se indica un rol (o lista de roles) y el usuario no lo tiene, envia al inicio.
export default function RutaProtegida({ rol, roles }) {
  const { usuario } = useAuth()

  if (!usuario) return <Navigate to="/login" replace />
  if (rol && usuario.rol !== rol) return <Navigate to="/" replace />
  if (Array.isArray(roles) && !roles.includes(usuario.rol)) return <Navigate to="/" replace />
  return <Outlet />
}
