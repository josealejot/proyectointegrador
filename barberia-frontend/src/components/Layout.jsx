import { CalendarClock, LayoutDashboard, LogOut, Scissors, ShieldCheck, Tag, Users } from 'lucide-react'
import { NavLink, Outlet } from 'react-router-dom'
import { modulos } from '../configs/modulos.js'
import { useAuth } from '../context/AuthContext.jsx'

const iconos = {
  citas: CalendarClock,
  clientes: Users,
  barberos: Scissors,
  servicios: Tag,
  usuarios: ShieldCheck,
}

// Estructura comun de las paginas con sesion: menu lateral y contenido
export default function Layout() {
  const { usuario, cerrarSesion } = useAuth()
  const visibles = modulos.filter((modulo) => !modulo.soloAdmin || usuario.rol === 'Administrador')

  return (
    <div className="aplicacion">
      <aside className="menu-lateral">
        <div className="poste" aria-hidden="true" />
        <div className="marca">
          <span className="marca-nombre">La Navaja</span>
          <span className="marca-detalle">Barbería · Agenda</span>
        </div>

        <nav aria-label="Módulos">
          <NavLink to="/" end className="enlace-menu">
            <LayoutDashboard size={18} /> <span>Inicio</span>
          </NavLink>
          {visibles.map((modulo) => {
            const Icono = iconos[modulo.clave]
            return (
              <NavLink key={modulo.clave} to={modulo.ruta} className="enlace-menu">
                <Icono size={18} /> <span>{modulo.titulo}</span>
              </NavLink>
            )
          })}
        </nav>

        <div className="sesion">
          <div className="sesion-datos">
            <strong>{usuario.nombre}</strong>
            <span>{usuario.rol}</span>
          </div>
          <button type="button" className="boton-salir" onClick={cerrarSesion}>
            <LogOut size={16} /> <span>Cerrar sesión</span>
          </button>
        </div>
      </aside>

      <main className="contenido">
        <Outlet />
      </main>
    </div>
  )
}
