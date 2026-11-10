import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { mensajeError } from '../api/http.js'
import Campo from '../components/Campo.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { validarFormulario } from '../utils/validaciones.js'

const campos = [
  { nombre: 'identificacion', etiqueta: 'Identificación (Cédula)', tipo: 'digitos', requerido: true, min: 6, max: 10, ayuda: 'De 6 a 10 números' },
  { nombre: 'nombre', etiqueta: 'Nombre completo', tipo: 'texto', requerido: true, min: 3, max: 80 },
  { nombre: 'telefono', etiqueta: 'Teléfono celular', tipo: 'digitos', requerido: true, min: 10, max: 10, ayuda: 'Celular de 10 números' },
  { nombre: 'correo', etiqueta: 'Correo', tipo: 'correo', requerido: true, max: 100 },
  { nombre: 'contrasena', etiqueta: 'Contraseña', tipo: 'clave', requerido: true, min: 8, max: 72, ayuda: 'Mínimo 8 caracteres' },
  { nombre: 'confirmar', etiqueta: 'Confirmar contraseña', tipo: 'clave', requerido: true, max: 72 },
]

export default function Registro() {
  const { usuario, registrar } = useAuth()
  const navegar = useNavigate()
  const [valores, setValores] = useState({
    identificacion: '',
    nombre: '',
    telefono: '',
    correo: '',
    contrasena: '',
    confirmar: '',
  })
  const [errores, setErrores] = useState({})
  const [errorServidor, setErrorServidor] = useState('')
  const [enviando, setEnviando] = useState(false)

  if (usuario) return <Navigate to="/" replace />

  async function enviar(evento) {
    evento.preventDefault()
    setErrorServidor('')
    const encontrados = validarFormulario(campos, valores)
    if (!encontrados.confirmar && valores.confirmar !== valores.contrasena) {
      encontrados.confirmar = 'Las contraseñas no coinciden.'
    }
    setErrores(encontrados)
    if (Object.keys(encontrados).length > 0) return

    try {
      setEnviando(true)
      await registrar({
        rol: 'Cliente',
        identificacion: valores.identificacion.trim(),
        nombre: valores.nombre.trim(),
        telefono: valores.telefono.trim(),
        correo: valores.correo.trim(),
        contrasena: valores.contrasena,
      })
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
          <h1>Crear cuenta de cliente</h1>
          <p className="acceso-texto">Regístrate para consultar servicios, precios y agendar tus citas.</p>

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
            {enviando ? 'Creando cuenta…' : 'Crear cuenta'}
          </button>

          <p className="acceso-cambio">
            ¿Ya tienes cuenta? <Link to="/login">Iniciar sesión</Link>
          </p>
        </form>
      </main>
    </div>
  )
}
