import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { mensajeError } from '../api/http.js'
import Campo from '../components/Campo.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { validarFormulario } from '../utils/validaciones.js'

const campos = [
  { nombre: 'correo', etiqueta: 'Correo', tipo: 'correo', requerido: true, max: 100 },
  { nombre: 'contrasena', etiqueta: 'Contraseña', tipo: 'clave', requerido: true, max: 72 },
]

export default function Login() {
  const { usuario, iniciarSesion } = useAuth()
  const navegar = useNavigate()
  const [valores, setValores] = useState({ correo: '', contrasena: '' })
  const [errores, setErrores] = useState({})
  const [errorServidor, setErrorServidor] = useState('')
  const [enviando, setEnviando] = useState(false)

  // Si ya hay sesion no tiene sentido mostrar el login
  if (usuario) return <Navigate to="/" replace />

  async function enviar(evento) {
    evento.preventDefault()
    setErrorServidor('')
    const encontrados = validarFormulario(campos, valores)
    setErrores(encontrados)
    if (Object.keys(encontrados).length > 0) return

    try {
      setEnviando(true)
      await iniciarSesion(valores.correo.trim(), valores.contrasena)
      navegar('/', { replace: true })
    } catch (error) {
      setErrorServidor(mensajeError(error))
      setEnviando(false)
    }
  }

  return (
    <div className="acceso">
      <section className="acceso-portada" aria-hidden="true">
        <div className="poste poste-grande" />
        <p className="acceso-lema">Cada cita, a su hora.</p>
      </section>

      <main className="acceso-panel">
        <form className="acceso-formulario" onSubmit={enviar} noValidate>
          <p className="marca-nombre">La Navaja</p>
          <h1>Iniciar sesión</h1>
          <p className="acceso-texto">Entra para gestionar la agenda de la barbería.</p>

          {errorServidor && (
            <p className="alerta alerta-error" role="alert">
              {errorServidor}
            </p>
          )}

          {campos.map((campo) => (
            <Campo
              key={campo.nombre}
              campo={campo}
              valor={valores[campo.nombre]}
              error={errores[campo.nombre]}
              deshabilitado={enviando}
              onCambio={(valor) => setValores({ ...valores, [campo.nombre]: valor })}
            />
          ))}

          <button type="submit" className="boton boton-primario boton-bloque" disabled={enviando}>
            {enviando ? 'Entrando…' : 'Entrar'}
          </button>

          <p className="acceso-cambio">
            ¿No tienes cuenta? <Link to="/registro">Crear una cuenta</Link>
          </p>
        </form>
      </main>
    </div>
  )
}
